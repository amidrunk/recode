package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.decompile.DecompilerConfigurationBuilder;
import io.recode.decompile.DecompilerDelegate;
import io.recode.decompile.DecompilerDelegation;
import io.recode.model.InstanceAllocation;
import io.recode.model.ElementType;
import io.recode.model.MethodCall;
import io.recode.model.impl.InstanceAllocationImpl;
import io.recode.model.impl.NewInstanceImpl;

public final class InstantiationInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.new_).then(newInstance());
        configurationBuilder.after(ByteCode.invokespecial).then((context, code, byteCode) -> {
            final MethodCall methodCall = (MethodCall) context.peek();

            if (methodCall.getMethodName().equals("<init>") && methodCall.getTargetInstance().getElementType() == ElementType.ALLOCATE) {
                final InstanceAllocation instanceAllocation = (InstanceAllocation) methodCall.getTargetInstance();

                context.pop();
                context.push(new NewInstanceImpl(instanceAllocation.getType(), methodCall.getSignature(), methodCall.getParameters()));
            }
        });
    }

    public static DecompilerDelegate newInstance() {
        return (context, code, byteCode) -> {
            final String className = context.getMethod().getClassFile().getConstantPool().getClassName(code.nextUnsignedShort());

            context.push(new InstanceAllocationImpl(context.resolveType(className)));

            // Ignore the dup and model the constructor as returning an initialized instance instead
            if (code.nextInstruction() != ByteCode.dup) {
                throw new ClassFileFormatException("New byte code should always be dup:ed");
            }

            // TODO Would like to install a listener on the stack here... like this

        };
    }

}
