package io.recode.decompile.impl;

import io.recode.decompile.*;
import io.recode.model.FieldAssignment;
import io.recode.classfile.ByteCode;
import io.recode.classfile.ConstantPool;
import io.recode.classfile.FieldRefDescriptor;
import io.recode.model.Expression;
import io.recode.model.FieldReference;
import io.recode.model.impl.FieldAssignmentImpl;
import io.recode.model.impl.FieldReferenceImpl;
import io.recode.model.MethodSignature;

import java.io.IOException;

/**
 * The <code>FieldInstructions</code> decompiler delegation configures support for various field-related
 * instructions, such as reading and writing to/from fields.
 */
public final class FieldInstructions implements DecompilerDelegation {

    /**
     * Configures the provided configuration builder with support for byte codes related to field
     * access and assignment.
     *
     * @param configurationBuilder The configuration builder to configure.
     */
    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.putfield).then(putfield());
        configurationBuilder.on(ByteCode.putstatic).then(putstatic());
        configurationBuilder.on(ByteCode.getfield).then(getfield());
        configurationBuilder.on(ByteCode.getstatic).then(getstatic());
    }

    /**
     * Extension for the <code>putfield=181</code> byte code. Will load the field descriptor from the
     * subsequent two bytes in the code stream pop the value and target instance from the stack and
     * push a <code>{@link FieldAssignment}</code> back onto the stack.
     *
     * @return A <code>DecompilerExtension</code> that handles the <code>putfield</code> byte code.
     */
    public static DecompilerDelegate putfield() {
        return (context, codeStream, byteCode) -> {
            handlePutField(context, codeStream, false);
        };
    }

    /**
     * Extension for the <code>putstatic=179</code> byte code. Will load the descriptor from the subsequent
     * two bytes in the code stream, pop the field value onto the stack and push a
     * {@link FieldAssignment} back onto the stack.
     *
     * @return A <code>DecompilerExtension</code> that handles the <code>putstatic</code> byte code.
     */
    public static DecompilerDelegate putstatic() {
        return (context,codeStream,byteCode) -> {
            handlePutField(context, codeStream, true);
        };
    }

    public static DecompilerDelegate getfield() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final FieldRefDescriptor fieldRefDescriptor = context.getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getFieldRefDescriptor(codeStream.nextUnsignedShort());

                context.getStack().push(new FieldReferenceImpl(
                        context.getStack().pop(),
                        context.resolveType(fieldRefDescriptor.getClassName()),
                        MethodSignature.parseType(fieldRefDescriptor.getDescriptor()),
                        fieldRefDescriptor.getName()));
            }
        };
    }

    private DecompilerDelegate getstatic() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final FieldRefDescriptor fieldRefDescriptor = context
                        .getMethod()
                        .getClassFile()
                        .getConstantPool()
                        .getFieldRefDescriptor(codeStream.nextUnsignedShort());

                context.getStack().push(new FieldReferenceImpl(
                        null,
                        context.resolveType(fieldRefDescriptor.getClassName()),
                        MethodSignature.parseType(fieldRefDescriptor.getDescriptor()),
                        fieldRefDescriptor.getName()));
            }
        };
    }

    private static void handlePutField(DecompilationContext context, CodeStream codeStream, boolean isStatic) throws IOException {
        final ConstantPool constantPool = context.getMethod().getClassFile().getConstantPool();
        final FieldRefDescriptor fieldRefDescriptor = constantPool.getFieldRefDescriptor(codeStream.nextUnsignedShort());
        final Expression value = context.pop();
        final Expression targetInstance = (isStatic ? null : context.pop());
        final FieldReference fieldReference = new FieldReferenceImpl(
                targetInstance,
                context.resolveType(fieldRefDescriptor.getClassName()),
                MethodSignature.parseType(fieldRefDescriptor.getDescriptor()),
                fieldRefDescriptor.getName());

        context.enlist(new FieldAssignmentImpl(fieldReference, value));
    }

}
