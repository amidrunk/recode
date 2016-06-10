package io.recode.util;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TypesTest {

    @Test
    public void isPrimitiveShouldNotAcceptNullType() {
        boolean failed = false;

        try {
            Types.isPrimitive(null);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void isPrimitiveShouldReturnTrueForAllPrimitives() {
        assertTrue(Types.isPrimitive(boolean.class));
        assertTrue(Types.isPrimitive(byte.class));
        assertTrue(Types.isPrimitive(short.class));
        assertTrue(Types.isPrimitive(char.class));
        assertTrue(Types.isPrimitive(int.class));
        assertTrue(Types.isPrimitive(long.class));
        assertTrue(Types.isPrimitive(float.class));
        assertTrue(Types.isPrimitive(double.class));
    }

    @Test
    public void isPrimitiveShouldReturnFalseForNonPrimitives() {
        assertFalse(Types.isPrimitive(Object.class));
        assertFalse(Types.isPrimitive(Integer.class));
        assertFalse(Types.isPrimitive(String.class));
    }

    @Test
    public void getComputationalCategoryShouldNotAcceptNullType() {
        boolean failed = false;

        try {
            Types.getComputationalCategory(null);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void getComputationalCategoryShouldReturn1ForNonLongOrDouble() {
        assertEquals(1, Types.getComputationalCategory(boolean.class));
        assertEquals(1, Types.getComputationalCategory(byte.class));
        assertEquals(1, Types.getComputationalCategory(short.class));
        assertEquals(1, Types.getComputationalCategory(char.class));
        assertEquals(1, Types.getComputationalCategory(short.class));
        assertEquals(1, Types.getComputationalCategory(float.class));
        assertEquals(1, Types.getComputationalCategory(Object.class));
    }

    @Test
    public void getComputationalCategoryShouldReturn2ForLongAndDouble() {
        assertEquals(2, Types.getComputationalCategory(long.class));
        assertEquals(2, Types.getComputationalCategory(double.class));
    }

    @Test
    public void getBoxTypeShouldNotAcceptNullArg() {
        boolean failed = false;

        try {
            Types.getBoxType(null);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void getBoxTypeShouldFailForNonPrimitiveType() {
        boolean failed = false;

        try {
            Types.getBoxType(String.class);
        } catch (IllegalArgumentException e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void getBoxTypeShouldReturnBoxTypeForPrimitive() {
        assertEquals(Boolean.class, Types.getBoxType(boolean.class));
        assertEquals(Byte.class, Types.getBoxType(byte.class));
        assertEquals(Short.class, Types.getBoxType(short.class));
        assertEquals(Character.class, Types.getBoxType(char.class));
        assertEquals(Integer.class, Types.getBoxType(int.class));
        assertEquals(Long.class, Types.getBoxType(long.class));
        assertEquals(Float.class, Types.getBoxType(float.class));
        assertEquals(Double.class, Types.getBoxType(double.class));
    }

    @Test
    public void isValueTypeAssignableToShouldNotAcceptInvalidArguments() {
        boolean failed1 = false;

        try {
            Types.isValueTypePotentiallyAssignableTo(null, String.class);
        } catch (AssertionError e) {
            failed1 = true;
        }

        assertTrue(failed1);

        boolean failed2 = false;

        try {
            Types.isValueTypePotentiallyAssignableTo(String.class, null);
        } catch (AssertionError e) {
            failed2 = true;
        }

        assertTrue(failed2);
    }

    @Test
    public void isValueTypeAssignableToShouldBeTrueForEqualTypes() {
        assertTrue(Types.isValueTypePotentiallyAssignableTo(boolean.class, boolean.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(byte.class, byte.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(short.class, short.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(char.class, char.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(int.class, int.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(float.class, float.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(double.class, double.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Object.class, Object.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(String.class, String.class));
    }

    @Test
    public void boxTypeShouldBeAssignableToCorrespondingPrimitive() {
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Boolean.class, boolean.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Byte.class, byte.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Short.class, short.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Character.class, char.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Integer.class, int.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Long.class, long.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Float.class, float.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(Double.class, double.class));
    }

    @Test
    public void primitiveTypeShouldBeAssignableToCorrespondingBoxType() {
        assertTrue(Types.isValueTypePotentiallyAssignableTo(boolean.class, Boolean.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(byte.class, Byte.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(short.class, Short.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(char.class, Character.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(int.class, Integer.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(long.class, Long.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(float.class, Float.class));
        assertTrue(Types.isValueTypePotentiallyAssignableTo(double.class, Double.class));
    }

    @Test
    public void isArrayShouldNotAcceptNullType() {
        boolean failed = false;

        try {
            Types.isArray(null);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void isArrayShouldReturnFalseForNonArrayType() {
        assertFalse(Types.isArray(String.class));
        assertFalse(Types.isArray(Collection.class));
        assertFalse(Types.isArray(boolean.class));
        assertFalse(Types.isArray(byte.class));
        assertFalse(Types.isArray(short.class));
        assertFalse(Types.isArray(char.class));
        assertFalse(Types.isArray(int.class));
        assertFalse(Types.isArray(long.class));
        assertFalse(Types.isArray(float.class));
        assertFalse(Types.isArray(double.class));
    }

    @Test
    public void isArrayShouldReturnTrueForArrayType() {
        assertTrue(Types.isArray(String[].class));
        assertTrue(Types.isArray(Collection[].class));
        assertTrue(Types.isArray(boolean[].class));
        assertTrue(Types.isArray(byte[].class));
        assertTrue(Types.isArray(short[].class));
        assertTrue(Types.isArray(char[].class));
        assertTrue(Types.isArray(int[].class));
        assertTrue(Types.isArray(long[].class));
        assertTrue(Types.isArray(float[].class));
        assertTrue(Types.isArray(double[].class));
    }
}