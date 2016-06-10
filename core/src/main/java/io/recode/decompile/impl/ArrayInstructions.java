package io.recode.decompile.impl;

import io.recode.decompile.*;
import io.recode.model.*;
import io.recode.model.impl.*;
import io.recode.util.Types;
import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.util.Priority;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The <code>ArrayInstructions</code> decompiler delegation deals with instructions related to arrays, such
 * as
 * <dir>
 *     <li>Instantiating arrays through e.g. <code>anewarray</code>, <code>newarray</code> etc</li>
 *     <li>Retrieving the length of an array through <code>arraylength</code></li>
 *     <li>Storing elements in an array through e.g. <code>iastore</code>, <code>aastore</code> etc</li>
 *     <li>Loading elements from an array through e.g. <code>aaload</code>, <code>iaload</code> etc</li>
 * </dir>
 *
 * Further, this decompiler delegation provides
 *
 * <dir>
 *     <li>Configuration to ensure that array instantiation is mapped to a single expression in the syntax tree.</li>
 * </dir>
 */
public final class ArrayInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.dup)
                .withPriority(Priority.HIGH)
                .when((context, byteCode) -> context.peek().getElementType() == ElementType.NEW_ARRAY)
                .then(DecompilerDelegate.NOP);

        configurationBuilder.on(ByteCode.aaload).then(aaload());
        configurationBuilder.on(ByteCode.anewarray).then(anewarray());
        configurationBuilder.on(ByteCode.aastore).then(aastore());
        configurationBuilder.on(ByteCode.newarray).then(newarray());
        configurationBuilder.on(ByteCode.iastore).then(iastore());
        configurationBuilder.on(ByteCode.arraylength).then(arraylength());
        configurationBuilder.on(ByteCode.iaload).then(iaload());
        configurationBuilder.on(ByteCode.laload).then(laload());
        configurationBuilder.on(ByteCode.faload).then(faload());
        configurationBuilder.on(ByteCode.daload).then(daload());
        configurationBuilder.on(ByteCode.baload).then(baload());
        configurationBuilder.on(ByteCode.caload).then(caload());
        configurationBuilder.on(ByteCode.saload).then(saload());
    }

    /**
     * Loads an element from an object array.
     *
     * <p>
     * <pre>[..., index=Expression&lt;int&gt;,array=Expression&lt;Object[]&gt;] => [..., ArrayLoad(array=array,index=index)]</pre>
     * </p>
     *
     * @return A delegate that handles the <code>aaload</code> instruction.
     * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.aaload">aaload</a>.
     */
    public static DecompilerDelegate aaload() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression index = context.pop();
                final Expression array = context.pop();
                final Type arrayType = array.getType();
                final Type componentType;

                if (!(arrayType instanceof Class)) {
                    final String typeName = arrayType.getTypeName();

                    if (typeName.charAt(0) != '[') {
                        throw new ClassFileFormatException("Can't execute 'aaload' on non-array type: " + typeName);
                    }

                    componentType = context.resolveType(typeName.substring(1));
                } else {
                    componentType = ((Class) arrayType).getComponentType();

                    if (componentType == null) {
                        throw new ClassFileFormatException("Can't execute 'aaload' on non-array type: " + arrayType.getTypeName());
                    }
                }

                context.push(new ArrayLoadImpl(array, index, componentType));
            }
        };
    }

    public static DecompilerDelegate anewarray() {
        return (context, code, byteCode) -> {
            final String componentTypeName = context.getMethod().getClassFile().getConstantPool().getClassName(code.nextUnsignedShort());
            final Type componentType = context.resolveType(componentTypeName);
            final Type arrayType = context.resolveType("[L" + componentTypeName + ";");
            final Expression length = context.pop();

            context.push(new NewArrayImpl(arrayType, componentType, length, Collections.emptyList()));

            // Ignore dup since element initialization push the array back to the stack
            if (code.peekByte() == ByteCode.dup) {
                code.commit();
            }
        };
    }

    public static DecompilerDelegate aastore() {
        return (context, code, byteCode) -> arrayStore(context);
    }

    public static DecompilerDelegate newarray() {
        return (context,codeStream,byteCode) -> {
            final int type = codeStream.nextUnsignedByte();
            final Class arrayType;

            switch (type) {
                case 4:
                    arrayType = boolean[].class;
                    break;
                case 5:
                    arrayType = char[].class;
                    break;
                case 6:
                    arrayType = float[].class;
                    break;
                case 7:
                    arrayType = double[].class;
                    break;
                case 8:
                    arrayType = byte[].class;
                    break;
                case 9:
                    arrayType = short[].class;
                    break;
                case 10:
                    arrayType = int[].class;
                    break;
                case 11:
                    arrayType = long[].class;
                    break;
                default:
                    throw new ClassFileFormatException("Invalid type code for primitive array: " + type);
            }

            final Expression length = context.pop();

            context.push(new NewArrayImpl(arrayType, arrayType.getComponentType(), length, Collections.emptyList()));
        };
    }

    public static DecompilerDelegate iastore() {
        return (context,codeStream,byteCode) -> arrayStore(context);
    }

    public static DecompilerDelegate arraylength() {
        return (context,codeStream,byteCode) -> {
            final Expression array = context.pop();

            if (!Types.isArray(array.getType())) {
                throw new ClassFileFormatException("Stacked element is not an array: " + String.valueOf(array));
            }

            context.push(new FieldReferenceImpl(array, array.getType(), int.class, "length"));
        };
    }

    public static DecompilerDelegate iaload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, int.class);
        };
    }

    public static DecompilerDelegate laload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, long.class);
        };
    }

    public static DecompilerDelegate faload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, float.class);
        };
    }

    public static DecompilerDelegate daload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, double.class);
        };
    }

    public static DecompilerDelegate baload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, boolean.class);
        };
    }

    public static DecompilerDelegate caload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, char.class);
        };
    }

    public static DecompilerDelegate saload() {
        return (context,codeStream,byteCode) -> {
            arrayLoad(context, short.class);
        };
    }

    private static void arrayLoad(DecompilationContext context, Class<?> type) {
        final Expression index = context.pop();
        final Expression array = context.pop();

        context.push(new ArrayLoadImpl(array, index, type));
    }

    private static boolean arrayStore(DecompilationContext context) {
        Expression value = context.pop();
        Expression index = context.pop();
        Expression array = context.pop();

        if (array.getElementType() == ElementType.NEW_ARRAY) {
            final NewArray newArray = (NewArray) array;
            final ArrayList<ArrayInitializer> initializers = new ArrayList<>(newArray.getInitializers());

            initializers.add(new ArrayInitializerImpl((Integer) ((Constant) index).getConstant(), value));

            context.push(new NewArrayImpl(newArray.getType(), newArray.getComponentType(), newArray.getLength(), initializers));
        } else {
            context.enlist(new ArrayStoreImpl(array, index, value));
        }

        return true;
    }
}
