package io.recode.classfile;

import org.junit.Test;
import io.recode.classfile.ReferenceKind;

import static org.junit.Assert.assertEquals;

public class ReferenceKindTest {

    @Test
    public void enumValuesShouldBeMappedToCorrectIntegers() {
        assertEquals(1, ReferenceKind.GET_FIELD.getValue());
        assertEquals(2, ReferenceKind.GET_STATIC.getValue());
        assertEquals(3, ReferenceKind.PUT_FIELD.getValue());
        assertEquals(4, ReferenceKind.PUT_STATIC.getValue());
        assertEquals(5, ReferenceKind.INVOKE_VIRTUAL.getValue());
        assertEquals(6, ReferenceKind.INVOKE_STATIC.getValue());
        assertEquals(7, ReferenceKind.INVOKE_SPECIAL.getValue());
        assertEquals(8, ReferenceKind.NEW_INVOKE_SPECIAL.getValue());
        assertEquals(9, ReferenceKind.INVOKE_INTERFACE.getValue());
    }

    @Test
    public void integersShouldBeMappedToValueEnumValues() {
        assertEquals(ReferenceKind.valueOf(1), ReferenceKind.GET_FIELD);
        assertEquals(ReferenceKind.valueOf(2), ReferenceKind.GET_STATIC);
        assertEquals(ReferenceKind.valueOf(3), ReferenceKind.PUT_FIELD);
        assertEquals(ReferenceKind.valueOf(4), ReferenceKind.PUT_STATIC);
        assertEquals(ReferenceKind.valueOf(5), ReferenceKind.INVOKE_VIRTUAL);
        assertEquals(ReferenceKind.valueOf(6), ReferenceKind.INVOKE_STATIC);
        assertEquals(ReferenceKind.valueOf(7), ReferenceKind.INVOKE_SPECIAL);
        assertEquals(ReferenceKind.valueOf(8), ReferenceKind.NEW_INVOKE_SPECIAL);
        assertEquals(ReferenceKind.valueOf(9), ReferenceKind.INVOKE_INTERFACE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueOfShouldFailForInvalidValue() {
        ReferenceKind.valueOf(10);
    }

}
