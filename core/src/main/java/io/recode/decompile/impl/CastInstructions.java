package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.decompile.*;
import io.recode.model.TypeCast;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import io.recode.model.impl.TypeCastImpl;
import io.recode.util.Priority;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * The <code>CastInstructions</code> decompilation delegation provides support for all available cast
 * instructions in the class file format. This includes support for non identity primitive cast instructions
 * (i2b, f2i etc) as well as type safe, loss-less casts through check casts. Further the delegation handles
 * various intricacies of the java compiler.
 */
public final class CastInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.pop)
                .withPriority(Priority.HIGH)
                .when((context, byteCode) -> context.peek().getElementType() == ElementType.CAST)
                .then(discardImplicitCast());

        configurationBuilder.on(ByteCode.checkcast).then(checkcast());
        configurationBuilder.on(ByteCode.i2b).then(i2b());
        configurationBuilder.on(ByteCode.i2c).then(i2c());
        configurationBuilder.on(ByteCode.i2s).then(i2s());
        configurationBuilder.on(ByteCode.i2l).then(i2l());
        configurationBuilder.on(ByteCode.i2f).then(i2f());
        configurationBuilder.on(ByteCode.i2d).then(i2d());
        configurationBuilder.on(ByteCode.l2i).then(l2i());
        configurationBuilder.on(ByteCode.l2f).then(l2f());
        configurationBuilder.on(ByteCode.l2d).then(l2d());
        configurationBuilder.on(ByteCode.f2i).then(f2i());
        configurationBuilder.on(ByteCode.f2l).then(f2l());
    }

    /**
     * This handles a special case where a checkcast instruction is inserted when a generic method is
     * called e.g. in a void-method. The return value of the method will be discarded, which will be reduced
     * to a statement by the decompiler. However, a cast is not a valid statement, so it need to be
     * discarded to be reducable. It also has the advantage of matching the user's actual code.
     *
     * @return An extension for discarding implicit casts when
     */
    public static DecompilerDelegate discardImplicitCast() {
        return (context,codeStream,byteCode) -> {
            final TypeCast typeCast = (TypeCast) context.pop();

            context.push(typeCast.getValue());
        };
    }

    public static DecompilerDelegate checkcast() {
        return (context,codeStream,byteCode) -> {
            final String targetTypeName = context.getMethod().getClassFile().getConstantPool().getClassName(codeStream.nextUnsignedShort());
            final Type targetType = context.resolveType(targetTypeName);
            final Expression castExpression = context.pop();

            context.push(new TypeCastImpl(castExpression, targetType));
        };
    }

    public static DecompilerDelegate i2b() {
        return value2primitive(int.class, byte.class);
    }

    public static DecompilerDelegate i2c() {
        return value2primitive(int.class, char.class);
    }

    public static DecompilerDelegate i2s() {
        return value2primitive(int.class, short.class);
    }

    public static DecompilerDelegate i2l() {
        return value2primitive(int.class, long.class);
    }

    public static DecompilerDelegate i2f() {
        return value2primitive(int.class, float.class);
    }

    public static DecompilerDelegate i2d() {
        return value2primitive(int.class, double.class);
    }

    public static DecompilerDelegate l2i() {
        return value2primitive(long.class, int.class);
    }

    public static DecompilerDelegate l2f() {
        return value2primitive(long.class, float.class);
    }

    public static DecompilerDelegate l2d() {
        return value2primitive(long.class, double.class);
    }

    public static DecompilerDelegate f2i() {
        return value2primitive(float.class, int.class);
    }

    public static DecompilerDelegate f2l() {
        return value2primitive(float.class, long.class);
    }

    private static DecompilerDelegate value2primitive(final Class<?> sourceType, final Class<?> targetType) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression sourceValue = context.pop();

                if (!sourceType.equals(sourceValue.getType())) {
                    throw new ClassFileFormatException("Invalid cast " + sourceType.getSimpleName()
                            + "->" + targetType.getSimpleName() + "; value is of incorrect source type: " + sourceValue);
                }

                context.push(new TypeCastImpl(sourceValue, targetType));
            }
        };
    }


}
