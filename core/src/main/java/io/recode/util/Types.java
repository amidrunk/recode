package io.recode.util;

import java.lang.reflect.Type;

public final class Types {

    public static boolean isArray(Type type) {
        assert type != null : "Type can't be null";

        return type.getTypeName().endsWith("[]");
    }

    public static boolean isPrimitive(Type type) {
        assert type != null : "Type can't be null";

        switch (type.getTypeName()) {
            case "boolean":
            case "byte":
            case "short":
            case "char":
            case "int":
            case "long":
            case "float":
            case "double":
                return true;
        }

        return false;
    }

    public static int getComputationalCategory(Type type) {
        assert type != null : "Type can't be null";

        switch (type.getTypeName()) {
            case "double":
            case "long":
                return 2;
            default:
                return 1;
        }
    }

    public static Type getBoxType(Type primitiveType) {
        assert primitiveType != null : "Primitive type can't be null";

        switch (primitiveType.getTypeName()) {
            case "int":
                return Integer.class;
            case "boolean":
                return Boolean.class;
            case "long":
                return Long.class;
            case "double":
                return Double.class;
            case "char":
                return Character.class;
            case "float":
                return Float.class;
            case "short":
                return Short.class;
            case "byte":
                return Byte.class;
            default:
                throw new IllegalArgumentException("Type is not a primitive: " + primitiveType.getTypeName());
        }
    }

    /**
     * Returns whether or not a value of the provided value type is <i>potentially</i> assignable to
     * the specified expected type. This method checks the compatibility between the types without
     * requiring knowledge of the class structure. Only Object-Object, primitive-primitive, primitive-boxtype
     * and boxtype-primitive is checked, i.e. the following types are considered potentially assignable:
     * <dir>
     *     <li>int-int, short-short etc</li>
     *     <li>int-Integer, short-Short etc</li>
     *     <li>Integer-int, Short-short etc</li>
     *     <li>Object-Object, Object-String, String-Object etc</li>
     * </dir>
     *
     * @param valueType The type of the value.
     * @param expectedType The type of the expected type.
     * @return Whether or not the specified value type is potentially assignable to the expected value. Will return
     * false positives, but not false negatives.
     */
    public static boolean isValueTypePotentiallyAssignableTo(Type valueType, Type expectedType) {
        assert valueType != null : "Value type can't be null";
        assert expectedType != null : "Expected type can't be null";

        if (valueType.equals(expectedType)) {
            return true;
        }

        if (Types.isPrimitive(expectedType)) {
            if (getBoxType(expectedType).equals(valueType)) {
                return true;
            }

            return false;
        }

        if (Types.isPrimitive(valueType)) {
            if (getBoxType(valueType).equals(expectedType)) {
                return true;
            }

            return false;
        }

        return true;
    }

}
