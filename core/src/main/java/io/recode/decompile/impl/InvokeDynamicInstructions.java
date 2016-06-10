package io.recode.decompile.impl;

import io.recode.classfile.*;
import io.recode.decompile.DecompilationStateSelector;
import io.recode.decompile.DecompilerConfigurationBuilder;
import io.recode.decompile.DecompilerDelegate;
import io.recode.decompile.DecompilerDelegation;
import io.recode.model.*;
import io.recode.model.impl.LambdaImpl;
import io.recode.model.MethodSignature;
import io.recode.util.Sequence;

import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;

public final class InvokeDynamicInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.invokedynamic).then(invokedynamic());

        configurationBuilder.after(ByteCode.invokedynamic).when(DecompilationStateSelector.ALL).then((context, codeStream, byteCode) -> {
            // Ok, this is like really weird. There's no way to determine that a lambda method reference
            // is an instance method reference as opposed to a static method reference. However, the compiler
            // will insert a <targetInstance>.getClass() prior to the method call and immediately discard the
            // result. Presumably, this has to do with the loading of the class or similar.

            final Lambda lambda = context.peek().as(Lambda.class);

            if (lambda.getReferenceKind() == ReferenceKind.INVOKE_VIRTUAL) {
                final Sequence<Statement> statements = context.getStatements();

                if (!statements.isEmpty()) {
                    final Statement statement = statements.last().get();

                    if (statement.getElementType() == ElementType.METHOD_CALL) {
                        final MethodCall methodCall = statement.as(MethodCall.class);
                        if (Object.class.equals(methodCall.getTargetType()) && methodCall.getMethodName().equals("getClass")) {
                            context.getStatements().last().remove();
                        }
                    }
                }
            }
        });
    }

    public static DecompilerDelegate invokedynamic() {
        return (context, stream, byteCode) -> {
            final ClassFile classFile = context.getMethod().getClassFile();
            final ConstantPool constantPool = classFile.getConstantPool();
            final InvokeDynamicDescriptor invokeDynamicDescriptor = constantPool.getInvokeDynamicDescriptor(stream.nextUnsignedShort());

            final BootstrapMethod bootstrapMethod = getBootstrapMethod(classFile, invokeDynamicDescriptor.getBootstrapMethodAttributeIndex());
            final MethodHandleDescriptor bootstrapMethodHandle = constantPool.getMethodHandleDescriptor(bootstrapMethod.getBootstrapMethodRef());

            final ConstantPoolEntryDescriptor[] descriptors = resolveValidBootstrapArguments(constantPool, bootstrapMethod);
            final MethodHandleDescriptor backingMethodHandle = descriptors[1].as(MethodHandleDescriptor.class);
            final Signature functionalMethodSignature = MethodSignature.parse(descriptors[0].as(MethodTypeDescriptor.class).getDescriptor());
            final Signature parameterizedMethodSignature = MethodSignature.parse(descriptors[2].as(MethodTypeDescriptor.class).getDescriptor());


            final Optional<Expression> self;
            final MethodSignature backingMethodSignature = MethodSignature.parse(backingMethodHandle.getMethodDescriptor());
            final LocalVariableReference[] enclosedVariables = new LocalVariableReference[Math.max(0, backingMethodSignature.getParameterTypes().size() - functionalMethodSignature.getParameterTypes().size())];

            for (int i = enclosedVariables.length - 1; i >= 0; i--) {
                enclosedVariables[i] = (LocalVariableReference) context.pop();
            }

            final MethodSignature dynamicInvokeDescriptor = MethodSignature.parse(invokeDynamicDescriptor.getMethodDescriptor());
            final Stack<Expression> dynamicCallStack = new Stack<>();
            final int dynamicCallStackSize;

            if (backingMethodHandle.getReferenceKind() == ReferenceKind.INVOKE_SPECIAL) {
                dynamicCallStackSize = 1;
            } else {
                dynamicCallStackSize = dynamicInvokeDescriptor.getParameterTypes().size() - backingMethodSignature.getParameterTypes().size();
            }

            for (int i = 0; i < dynamicCallStackSize; i++) {
                final Expression callValue = context.pop();

                if (!dynamicInvokeDescriptor.getParameterTypes().get(i).equals(callValue.getType())) {
                    throw new ClassFileFormatException("");
                }

                dynamicCallStack.push(callValue);
            }

            if (dynamicCallStack.size() > 1) {
                throw new ClassFileFormatException("Weird");
            } else if (dynamicCallStack.isEmpty()) {
                self = Optional.empty();
            } else {
                self = Optional.of(dynamicCallStack.pop());
            }

            context.push(new LambdaImpl(
                    self,
                    backingMethodHandle.getReferenceKind(),
                    MethodSignature.parse(invokeDynamicDescriptor.getMethodDescriptor()).getReturnType(),
                    invokeDynamicDescriptor.getMethodName(),
                    functionalMethodSignature,
                    context.resolveType(backingMethodHandle.getClassName()),
                    backingMethodHandle.getMethodName(),
                    backingMethodSignature,
                    Arrays.asList(enclosedVariables)
            ));
        };
    }

    private static ConstantPoolEntryDescriptor[] resolveValidBootstrapArguments(ConstantPool constantPool, BootstrapMethod bootstrapMethod) {
        final ConstantPoolEntryDescriptor[] descriptors = constantPool.getDescriptors(bootstrapMethod.getBootstrapArguments());

        if (descriptors[0].getTag() != ConstantPoolEntryTag.METHOD_TYPE) {
            throw new UnsupportedOperationException();
        }

        if (descriptors[1].getTag() != ConstantPoolEntryTag.METHOD_HANDLE) {
            throw new UnsupportedOperationException();
        }

        if (descriptors[2].getTag() != ConstantPoolEntryTag.METHOD_TYPE) {
            throw new UnsupportedOperationException();
        }
        return descriptors;
    }

    private static BootstrapMethod getBootstrapMethod(ClassFile classFile, int bootstrapMethodAttributeIndex) {
        return classFile.getBootstrapMethodsAttribute()
                .orElseThrow(() -> new ClassFileFormatException("No bootstrap methods attribute is available in class " + classFile.getName()))
                .getBootstrapMethods()
                .get(bootstrapMethodAttributeIndex);
    }

}
