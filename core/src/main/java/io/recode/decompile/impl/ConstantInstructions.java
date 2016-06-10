package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.classfile.ConstantPool;
import io.recode.classfile.ConstantPoolEntry;
import io.recode.decompile.*;

import java.io.IOException;
import java.lang.reflect.Type;

public final class ConstantInstructions implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfigurationBuilder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompiler configuration builder can't be null";

        decompilerConfigurationBuilder.on(ByteCode.aconst_null).then(aconst_null());
        decompilerConfigurationBuilder.on(ByteCode.lconst_0).then(lconst_0());
        decompilerConfigurationBuilder.on(ByteCode.lconst_1).then(lconst_1());
        decompilerConfigurationBuilder.on(ByteCode.dconst_0).then(dconst_0());
        decompilerConfigurationBuilder.on(ByteCode.dconst_1).then(dconst_1());
        decompilerConfigurationBuilder.on(ByteCode.fconst_0).then(fconst_0());
        decompilerConfigurationBuilder.on(ByteCode.fconst_1).then(fconst_1());
        decompilerConfigurationBuilder.on(ByteCode.fconst_2).then(fconst_2());
        decompilerConfigurationBuilder.on(ByteCode.iconst_m1).then(iconst_m1());
        decompilerConfigurationBuilder.on(ByteCode.iconst_0).then(iconst_0());
        decompilerConfigurationBuilder.on(ByteCode.iconst_1).then(iconst_1());
        decompilerConfigurationBuilder.on(ByteCode.iconst_2).then(iconst_2());
        decompilerConfigurationBuilder.on(ByteCode.iconst_3).then(iconst_3());
        decompilerConfigurationBuilder.on(ByteCode.iconst_4).then(iconst_4());
        decompilerConfigurationBuilder.on(ByteCode.iconst_5).then(iconst_5());
        decompilerConfigurationBuilder.on(ByteCode.bipush).then(bipush());
        decompilerConfigurationBuilder.on(ByteCode.sipush).then(sipush());
        decompilerConfigurationBuilder.on(ByteCode.ldc).then(ldc());
        decompilerConfigurationBuilder.on(ByteCode.ldcw).then(ldcw());
        decompilerConfigurationBuilder.on(ByteCode.ldc2w).then(ldc2w());
    }

    public static DecompilerDelegate bipush() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                context.push(context.getModelFactory().constant(codeStream.nextByte(), int.class));
            }
        };
    }

    public static DecompilerDelegate sipush() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                context.push(context.getModelFactory().constant(codeStream.nextSignedShort(), int.class));
            }
        };
    }

    public static DecompilerDelegate lconst_0() {
        return xconst(0L, long.class);
    }

    public static DecompilerDelegate lconst_1() {
        return xconst(1L, long.class);
    }

    public static DecompilerDelegate iconst_m1() {
        return xconst(-1, int.class);
    }

    public static DecompilerDelegate iconst_0() {
        return xconst(0, int.class);
    }

    public static DecompilerDelegate iconst_1() {
        return xconst(1, int.class);
    }

    public static DecompilerDelegate iconst_2() {
        return xconst(2, int.class);
    }

    public static DecompilerDelegate iconst_3() {
        return xconst(3, int.class);
    }

    public static DecompilerDelegate iconst_4() {
        return xconst(4, int.class);
    }

    public static DecompilerDelegate iconst_5() {
        return xconst(5, int.class);
    }

    public static DecompilerDelegate fconst_0() {
        return xconst(0f, float.class);
    }

    public static DecompilerDelegate fconst_1() {
        return xconst(1f, float.class);
    }

    public static DecompilerDelegate fconst_2() {
        return xconst(2f, float.class);
    }

    public static DecompilerDelegate dconst_0() {
        return xconst(0d, double.class);
    }

    public static DecompilerDelegate dconst_1() {
        return xconst(1d, double.class);
    }

    public static DecompilerDelegate aconst_null() {
        return xconst(null, Object.class);
    }

    public static DecompilerDelegate ldc() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                ConstantInstructions.ldc(context, codeStream.nextUnsignedByte());
            }
        };
    }

    public static DecompilerDelegate ldcw() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                ConstantInstructions.ldc(context, codeStream.nextUnsignedShort());
            }
        };
    }

    public static DecompilerDelegate ldc2w() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                ldc2(context, codeStream.nextUnsignedShort());
            }
        };
    }

    private static void ldc(DecompilationContext context, int index) {
        final ConstantPool constantPool = context.getMethod().getClassFile().getConstantPool();
        final ConstantPoolEntry entry = constantPool.getEntry(index);

        switch (entry.getTag()) {
            case INTEGER:
                context.push(context.getModelFactory().constant(((ConstantPoolEntry.IntegerEntry) entry).getValue(), int.class));
                break;
            case FLOAT:
                context.push(context.getModelFactory().constant(((ConstantPoolEntry.FloatEntry) entry).getValue(), float.class));
                break;
            case STRING:
                context.push(context.getModelFactory().constant(constantPool.getString(((ConstantPoolEntry.StringEntry) entry).getStringIndex()), String.class));
                break;
            case CLASS:
                final Type type = context.resolveType(constantPool.getString(((ConstantPoolEntry.ClassEntry) entry).getNameIndex()));
                context.push(context.getModelFactory().constant(type, Class.class));
                break;
            default:
                throw new ClassFileFormatException("Unsupported constant pool entry: " + entry);
        }
    }

    private static void ldc2(DecompilationContext context, int index) {
        final ConstantPoolEntry entry = context.getMethod().getClassFile().getConstantPool().getEntry(index);

        switch (entry.getTag()) {
            case LONG:
                context.push(context.getModelFactory().constant(((ConstantPoolEntry.LongEntry) entry).getValue(), long.class));
                break;
            case DOUBLE:
                context.push(context.getModelFactory().constant(((ConstantPoolEntry.DoubleEntry) entry).getValue(), double.class));
                break;
            default:
                throw new ClassFileFormatException("Invalid constant pool entry at "
                        + index + ". Expected long or double, but was " + entry);
        }
    }

    private static DecompilerDelegate xconst(Object value, Class type) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                context.push(context.getModelFactory().constant(value, type));
            }
        };
    }
}
