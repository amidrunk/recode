package io.recode.decompile.impl;

import io.recode.classfile.*;
import io.recode.decompile.*;
import io.recode.model.Expression;
import io.recode.model.Signature;
import io.recode.model.impl.MethodCallImpl;
import io.recode.model.MethodSignature;

import java.io.IOException;
import java.lang.reflect.Type;

import static io.recode.model.AST.constant;

public final class MethodCallInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.invokeinterface).then(invokeinterface());
        configurationBuilder.on(ByteCode.invokespecial).then(invokespecial());
        configurationBuilder.on(ByteCode.invokevirtual).then(invokespecial());
        configurationBuilder.on(ByteCode.invokestatic).then(invokestatic());
    }

    public static DecompilerDelegate invokeinterface() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final InterfaceMethodRefDescriptor interfaceMethodRefDescriptor = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getInterfaceMethodRefDescriptor(codeStream.nextUnsignedShort());

                invoke(context, interfaceMethodRefDescriptor, false);

                if (codeStream.nextUnsignedByte() == 0) {
                    throw new ClassFileFormatException("Expected byte subsequent to interface method invocation to be non-zero");
                }
            }
        };
    }

    public static DecompilerDelegate invokespecial() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final MethodRefDescriptor methodRefDescriptor = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getMethodRefDescriptor(codeStream.nextUnsignedShort());

                invoke(context, methodRefDescriptor, false);
            }
        };
    }

    public static DecompilerDelegate invokevirtual() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final MethodRefDescriptor methodRefDescriptor = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getMethodRefDescriptor(codeStream.nextUnsignedShort());

                invoke(context, methodRefDescriptor, false);
            }
        };
    }

    public static DecompilerDelegate invokestatic() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final ConstantPool constantPool = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool();

                final int index = codeStream.nextUnsignedShort();
                final ConstantPoolEntry constantPoolEntry = constantPool.getEntry(index);
                final MethodRefDescriptor methodRefDescriptor;

                switch (constantPoolEntry.getTag()) {
                    case METHOD_REF:
                        methodRefDescriptor = constantPool.getMethodRefDescriptor(index);
                        break;
                    case INTERFACE_METHOD_REF:
                        // Static method in interface (added to Java 1.8)
                        methodRefDescriptor = constantPool.getInterfaceMethodRefDescriptor(index);
                        break;
                    default:
                        throw new ClassFileFormatException("Invalid constant pool entry at index " + index
                                + " for invokestatic: "+ constantPoolEntry);
                }

                invoke(context, methodRefDescriptor, true);
            }
        };
    }

    private static void invoke(DecompilationContext context, MethodRefDescriptor methodReference, boolean isStatic) {
        final Signature signature = MethodSignature.parse(methodReference.getDescriptor());
        final Expression[] arguments = new Expression[signature.getParameterTypes().size()];
        final Type targetType = context.resolveType(methodReference.getClassName());

        for (int i = arguments.length - 1; i >= 0; i--) {
            arguments[i] = context.pop();
        }

        final Type expressionType;

        if (methodReference.getMethodName().equals("<init>")) { // TODO Correctional enhancement
            expressionType = targetType;
        } else {
            expressionType = signature.getReturnType();
        }

        final Expression thiz;

        if (isStatic) {
            thiz = null;
        } else {
            thiz = context.pop();
        }

        context.push(new MethodCallImpl(
                targetType,
                methodReference.getMethodName(),
                signature,
                thiz,
                arguments,
                expressionType));
    }
}
