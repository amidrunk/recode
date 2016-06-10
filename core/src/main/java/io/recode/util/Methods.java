package io.recode.util;

import io.recode.classfile.*;
import io.recode.util.Range;
import io.recode.model.Signature;

import java.util.Optional;

public final class Methods {

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

}
