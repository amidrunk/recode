package io.recode.util;

import io.recode.classfile.*;
import io.recode.classfile.Method;
import io.recode.decompile.CodePointer;
import io.recode.model.Lambda;
import io.recode.model.MethodCall;
import io.recode.model.Signature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.Optional;

public final class Methods {

    public static boolean isDefinitelyVarArgsMethodCall(MethodCall methodCall) {
        assert methodCall != null : "methodCall can't be null";

        if (!(methodCall.getTargetType() instanceof Class)) {
            return false;
        }

        final Optional<java.lang.reflect.Method> methodOptional = findMethodForNameAndSignature(
                (Class) methodCall.getTargetType(),
                methodCall.getMethodName(),
                methodCall.getSignature());

        if (!methodOptional.isPresent()) {
            return false;
        }

        final java.lang.reflect.Method method = methodOptional.get();
        final Parameter[] parameters = method.getParameters();

        if (parameters.length == 0) {
            return false;
        }

        return parameters[parameters.length - 1].isVarArgs();
    }

    public static Optional<java.lang.reflect.Method> findMethodForNameAndSignature(Class<?> type, String name, Signature signature) {
        assert type != null : "type can't be null";
        assert name != null && !name.isEmpty() : "name can't be null or empty";
        assert signature != null : "signature can't be null";

        for (final java.lang.reflect.Method method : type.getDeclaredMethods()) {
            if (method.getName().equals(name) && signature.test(method)) {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    public static Optional<Method> findMethodForNameAndLineNumber(ClassFile classFile, String methodName, int lineNumber) {
        assert classFile != null : "Class file can't be null";
        assert methodName != null && !methodName.isEmpty() : "Method name can't be null or empty";
        assert lineNumber >= 0 : "Line number must be positive";

        return Optional.empty();
    }

    public static Optional<Method> findMethodForLineNumber(ClassFile classFile, int lineNumber) {
        assert classFile != null : "Class file can't be null";
        assert lineNumber >= 0 : "Line number must be positive";

        for (Method method : classFile.getMethods()) {
            final Optional<LineNumberTable> optionalLineNumberTable = method.getLineNumberTable();

            if (optionalLineNumberTable.isPresent()) {
                final LineNumberTable lineNumberTable = optionalLineNumberTable.get();
                final Range sourceFileRange = lineNumberTable.getSourceFileRange();

                if (lineNumber >= sourceFileRange.getFrom() && lineNumber <= sourceFileRange.getTo()) {
                    return Optional.of(method);
                }
            }
        }

        return Optional.empty();
    }

    public static Optional<ExceptionTableEntry> getExceptionTableEntryForCatchLocation(Method method, int pc) {
        assert method != null : "Method can't be null";
        assert pc >= 0 : "PC must be positive";

        for (ExceptionTableEntry entry : method.getCode().getExceptionTable()) {
            if (entry.getEndPC() == pc) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

    public static Optional<LocalVariable> findLocalVariableForIndexAndPC(Method method, int index, int pc) {
        assert method != null : "Method can't be null";
        assert index >= 0 : "Index must be positive";
        assert pc >= 0 : "PC must be positive";

        final Optional<LocalVariableTable> optionalLocalVariableTable = method.getLocalVariableTable();

        if (!optionalLocalVariableTable.isPresent()) {
            return Optional.empty();
        }

        return optionalLocalVariableTable.get().getLocalVariables().stream()
                .filter(local -> local.getIndex() == index && (pc >= local.getStartPC() && pc < local.getStartPC() + local.getLength()) || (local.getIndex() == index && local.getStartPC() == -1))
                .findFirst();
    }

    public static boolean containsLineNumber(Method method, int lineNumber) {
        assert method != null : "Method can't be null";
        assert lineNumber >= 0 : "Line number must be positive";

        final Optional<LineNumberTable> lineNumberTable = method.getLineNumberTable();

        if (!lineNumberTable.isPresent()) {
            throw new IllegalArgumentException("Method contains no line number table");
        }

        for (LineNumberTableEntry entry : lineNumberTable.get().getEntries()) {
            if (entry.getLineNumber() == lineNumber) {
                return true;
            }
        }

        return false;
    }

    public static InputStream getCodeForLineNumber(Method method, int lineNumber) {
        assert method != null : "method can't be null";
        assert lineNumber >= 0 : "lineNumber must be greater than zero";

        final Range codeRangeForLineNumber = getCodeRangeForLineNumber(method, lineNumber);

        try (InputStream inputStream = method.getCode().getCode()) {
            final int numberOfByteCodes = codeRangeForLineNumber.getTo() - codeRangeForLineNumber.getFrom() + 1;

            inputStream.skip(codeRangeForLineNumber.getFrom());

            if (inputStream.available() == numberOfByteCodes) {
                return inputStream;
            }

            final byte[] buffer = new byte[numberOfByteCodes];

            inputStream.read(buffer);

            return new ByteArrayInputStream(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the first and last program counter for the given line number.
     *
     * @param method     The method to check.
     * @param lineNumber The line number for which the PC range should be returned.
     * @return The range of program counters mapping to the provided line number.
     */
    public static Range getCodeRangeForLineNumber(Method method, int lineNumber) {
        assert method != null : "method can't be null";
        assert lineNumber >= 0 : "lineNumber must be greater than zero";

        final LineNumberTable lineNumberTable = method.getLineNumberTable()
                .orElseThrow(() -> new IllegalArgumentException("Method " + method.getClassFile().getName() + "::"
                        + method.getName() + " (" + method.getSignature()
                        + ") does not contain a line-number table. Was debug information removed during compilation?"));

        int first = Integer.MAX_VALUE;
        int next = -1;

        for (final LineNumberTableEntry currentEntry : lineNumberTable.getEntries()) {
            if (currentEntry.getLineNumber() == lineNumber) {
                first = Math.min(currentEntry.getStartPC(), first);
            } else if (currentEntry.getLineNumber() > lineNumber) {
                next = currentEntry.getStartPC();
                break;
            }
        }

        if (first == Integer.MAX_VALUE) {
            throw new IllegalStateException("No code exists at line number " + lineNumber + " in method " + method.getClassFile().getName() + "::" + method.getName());
        }

        return new Range(first, next != -1 ? next - 1 : method.getCode().getCodeLength());
    }

    public static Optional<Method> getBackingMethod(CodePointer<Lambda> lambda) {
        assert lambda != null : "lambda can't be null";

        final Lambda element = lambda.getElement();

        if (!element.getDeclaringClass().getTypeName().equals(lambda.getMethod().getClassFile().getName())) {
            return Optional.empty();
        }

        final Signature backingMethodSignature = element.getBackingMethodSignature();

        for (final Method method : lambda.getMethod().getClassFile().getMethods()) {
            if (method.getName().equals(element.getBackingMethodName()) && method.getSignature().equals(backingMethodSignature)) {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    public static Optional<Method> getBackingMethod(ClassFileResolver classFileResolver, CodePointer<Lambda> lambda) {
        assert classFileResolver != null : "classFileResolver can't be null";
        assert lambda != null : "lambda can't be null";

        final Optional<Method> localBackingMethod = getBackingMethod(lambda);

        if (localBackingMethod.isPresent()) {
            return localBackingMethod;
        }

        final Lambda element = lambda.getElement();
        final Signature signature = element.getBackingMethodSignature();
        final ClassFile classFile = classFileResolver.resolveClassFile(element.getDeclaringClass());

        for (final Method method : classFile.getMethods()) {
            if (method.getName().equals(element.getBackingMethodName()) && method.getSignature().equals(signature)) {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }
}
