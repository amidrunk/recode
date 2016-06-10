package io.recode.classfile.impl;

import org.junit.Test;

import java.lang.reflect.Type;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class LocalVariableImplTest {

    private final Type exampleType = mock(Type.class);

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new LocalVariableImpl(1, 2, null, exampleType, 3), AssertionError.class);
        assertThrown(() -> new LocalVariableImpl(1, 2, "", exampleType, 3), AssertionError.class);
        assertThrown(() -> new LocalVariableImpl(1, 2, "foo", null, 3), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() throws Exception {
        final LocalVariableImpl variableTable = new LocalVariableImpl(1, 2, "foo", String.class, 3);

        assertEquals(1, variableTable.getStartPC());
        assertEquals(2, variableTable.getLength());
        assertEquals("foo", variableTable.getName());
        assertEquals(String.class, variableTable.getType());
        assertEquals(3, variableTable.getIndex());
    }
}
