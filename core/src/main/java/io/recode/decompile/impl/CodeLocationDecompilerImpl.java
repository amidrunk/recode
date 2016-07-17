package io.recode.decompile.impl;

import io.recode.CodeLocation;
import io.recode.decompile.*;
import io.recode.util.Methods;
import io.recode.util.Range;
import io.recode.classfile.ClassFile;
import io.recode.classfile.ClassFileReader;
import io.recode.classfile.Method;
import io.recode.classfile.impl.ClassFileReaderImpl;
import io.recode.model.Element;
import io.recode.model.Expression;
import io.recode.model.Statement;
import io.recode.util.Sequence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class CodeLocationDecompilerImpl implements CodeLocationDecompiler {

    private final ClassFileReader classFileReader;

    private final Decompiler decompiler;

    public CodeLocationDecompilerImpl() {
        this(new ClassFileReaderImpl(), new DecompilerImpl());
    }

    public CodeLocationDecompilerImpl(ClassFileReader classFileReader, Decompiler decompiler) {
        this.classFileReader = classFileReader;
        this.decompiler = decompiler;
    }

    @Override
    public CodePointer[] decompileCodeLocation(CodeLocation codeLocation) throws IOException {
        return decompileCodeLocation(codeLocation, DecompilationProgressCallback.NULL);
    }

    public CodePointer[] decompileCodeLocation(CodeLocation codeLocation, DecompilationProgressCallback callback) throws IOException {
        assert codeLocation != null : "codeLocation can't be null";
        assert callback != null : "callback can't be null";

        return codeForCaller(codeLocation, callback);
    }

    private CodePointer[] codeForCaller(CodeLocation codeLocation, DecompilationProgressCallback callback) throws IOException {
        final ClassFile classFile = loadClassFile(codeLocation.getClassName());

        if (classFile == null) {
            return null;
        }

        final Method method = resolveMethodFromClassFile(classFile, codeLocation);
        final Range codeRange = Methods.getCodeRangeForLineNumber(method, codeLocation.getLineNumber());

        try (CodeStream code = new InputStreamCodeStream(method.getCode().getCode())) {
            code.skip(codeRange.getFrom());

            final AtomicReference<Expression> lingeringExpression = new AtomicReference<>();
            final AtomicInteger exitStackSize = new AtomicInteger(-1);

            final Element[] elements = decompiler.parse(method, code, new CompositeDecompilationProgressCallback(new DecompilationProgressCallbackAdapter() {
                @Override
                public void afterInstruction(DecompilationContext context, int instruction) {
                    // Abort as soon as (a) we've exceeded the PC and (b) the stack is empty
                    if (context.getProgramCounter().get() >= codeRange.getTo()) {
                        final List<Expression> stackedExpressions = context.getStackedExpressions();

                        exitStackSize.compareAndSet(-1, stackedExpressions.size());

                        if (stackedExpressions.isEmpty()) {
                            context.abort();
                        } else {
                            if (lingeringExpression.get() == null) {
                                if (stackedExpressions.size() == 1) {
                                    lingeringExpression.set(stackedExpressions.get(0));
                                }
                            } else {
                                if (stackedExpressions.size() > exitStackSize.get()) {
                                    context.pop();
                                    context.abort();
                                } else {
                                    final Sequence<Statement> statements = context.getStatements();

                                    if (!statements.isEmpty() && statements.last().get().equals(lingeringExpression.get())) {
                                        context.pop();
                                        context.abort();
                                    }
                                }
                            }
                        }
                    }
                }
            }, callback));

            return Arrays.stream(elements).map(e -> new CodePointerImpl<>(method, e)).toArray(CodePointer[]::new);
        }
    }

    private Method resolveMethodFromClassFile(ClassFile classFile, CodeLocation codeLocation) {
        return classFile.getMethods().stream()
                .filter(m -> m.getName().equals(codeLocation.getMethodName()))
                .filter(m -> Methods.containsLineNumber(m, codeLocation.getLineNumber()))
                .map(m -> {
                    if (!m.isLambdaBackingMethod()) {
                        return m;
                    }

                    try {
                        return Lambdas.withEnclosedVariables(decompiler, m);
                    } catch (IOException e) {
                        // Ignore and hope for the best
                        return m;
                    }
                }).findFirst().orElseThrow(() -> new IllegalStateException("Method '" + codeLocation.getMethodName() + "' not found on line number " + codeLocation.getLineNumber() + " in class '" + classFile.getName() + "'"));
    }

    private ClassFile loadClassFile(String className) throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/" + className.replace('.', '/') + ".class")) {
            if (in == null) {
                return null;
            }

            return classFileReader.read(in);
        }
    }
}
