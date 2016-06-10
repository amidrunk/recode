package io.recode.model;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class ElementContextMetaDataTest {

    private final ElementContextMetaData exampleMetaData = new ElementContextMetaData(1234, 2345);

    @Test
    public void programCounterCannotBeNegative() {
        assertThrown(() -> new ElementContextMetaData(-1, 100), AssertionError.class);
    }

    @Test
    public void instanceShouldHaveLineNumberAndProgramCounterIfValid() {
        final ElementContextMetaData it = new ElementContextMetaData(1234, 2345);

        assertTrue(it.hasProgramCounter());
        assertEquals(1234, it.getProgramCounter());
        assertTrue(it.hasLineNumber());
        assertEquals(2345, it.getLineNumber());
    }

    @Test
    public void lineNumberShouldNotBeDefinedIfNegative() {
        final ElementContextMetaData it = new ElementContextMetaData(1234, -1);

        assertTrue(it.hasProgramCounter());
        assertEquals(1234, it.getProgramCounter());
        assertFalse(it.hasLineNumber());
        assertThrown(it::getLineNumber, IllegalStateException.class);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleMetaData, exampleMetaData);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(exampleMetaData, null);
        assertNotEquals(exampleMetaData, "foo");
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final ElementContextMetaData other = new ElementContextMetaData(1234, 2345);

        assertEquals(other, exampleMetaData);
        assertEquals(other.hashCode(), exampleMetaData.hashCode());
    }
}