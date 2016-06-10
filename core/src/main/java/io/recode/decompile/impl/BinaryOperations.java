package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.decompile.*;
import io.recode.model.Expression;
import io.recode.model.OperatorType;
import io.recode.model.impl.BinaryOperatorImpl;

import java.io.IOException;

/**
 * The <code>BinaryOperationsDecompilerDelegation</code> provides handing of all binary operations
 * during decompilation, from iadd=96 to lxor=131.
 */
public final class BinaryOperations implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfigurationBuilder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompiler configuration builder can't be null";

        decompilerConfigurationBuilder.on(ByteCode.iadd).then(iadd());
        decompilerConfigurationBuilder.on(ByteCode.isub).then(isub());
        decompilerConfigurationBuilder.on(ByteCode.imul).then(imul());
        decompilerConfigurationBuilder.on(ByteCode.idiv).then(idiv());

        decompilerConfigurationBuilder.on(ByteCode.fadd).then(fadd());
        decompilerConfigurationBuilder.on(ByteCode.fsub).then(fsub());
        decompilerConfigurationBuilder.on(ByteCode.fmul).then(fmul());
        decompilerConfigurationBuilder.on(ByteCode.fdiv).then(fdiv());

        decompilerConfigurationBuilder.on(ByteCode.dadd).then(dadd());
        decompilerConfigurationBuilder.on(ByteCode.dsub).then(dsub());
        decompilerConfigurationBuilder.on(ByteCode.dmul).then(dmul());
        decompilerConfigurationBuilder.on(ByteCode.ddiv).then(ddiv());

        decompilerConfigurationBuilder.on(ByteCode.ladd).then(ladd());
        decompilerConfigurationBuilder.on(ByteCode.lsub).then(lsub());
        decompilerConfigurationBuilder.on(ByteCode.lmul).then(lmul());
        decompilerConfigurationBuilder.on(ByteCode.ldiv).then(ldiv());
    }

    public static DecompilerDelegate iadd() {
        return binaryOperator(OperatorType.PLUS, int.class);
    }

    public static DecompilerDelegate isub() {
        return binaryOperator(OperatorType.MINUS, int.class);
    }

    public static DecompilerDelegate imul() {
        return binaryOperator(OperatorType.MULTIPLY, int.class);
    }

    public static DecompilerDelegate idiv() {
        return binaryOperator(OperatorType.DIVIDE, int.class);
    }

    public static DecompilerDelegate fadd() {
        return binaryOperator(OperatorType.PLUS, float.class);
    }

    public static DecompilerDelegate fsub() {
        return binaryOperator(OperatorType.MINUS, float.class);
    }

    public static DecompilerDelegate fmul() {
        return binaryOperator(OperatorType.MULTIPLY, float.class);
    }

    public static DecompilerDelegate fdiv() {
        return binaryOperator(OperatorType.DIVIDE, float.class);
    }

    public static DecompilerDelegate dadd() {
        return binaryOperator(OperatorType.PLUS, double.class);
    }

    public static DecompilerDelegate dsub() {
        return binaryOperator(OperatorType.MINUS, double.class);
    }

    public static DecompilerDelegate dmul() {
        return binaryOperator(OperatorType.MULTIPLY, double.class);
    }

    public static DecompilerDelegate ddiv() {
        return binaryOperator(OperatorType.DIVIDE, double.class);
    }

    public static DecompilerDelegate ladd() {
        return binaryOperator(OperatorType.PLUS, long.class);
    }

    public static DecompilerDelegate lsub() {
        return binaryOperator(OperatorType.MINUS, long.class);
    }

    public static DecompilerDelegate lmul() {
        return binaryOperator(OperatorType.MULTIPLY, long.class);
    }

    public static DecompilerDelegate ldiv() {
        return binaryOperator(OperatorType.DIVIDE, long.class);
    }

    private static DecompilerDelegate binaryOperator(final OperatorType operatorType, final Class<?> resultType) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression right = context.getStack().pop();
                final Expression left = context.getStack().pop();

                context.getStack().push(new BinaryOperatorImpl(left, operatorType, right, resultType));
            }
        };
    }

}
