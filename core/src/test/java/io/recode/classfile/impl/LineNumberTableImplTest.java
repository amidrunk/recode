package io.recode.classfile.impl;

import io.recode.classfile.LineNumberTable;
import io.recode.classfile.LineNumberTableEntry;
import io.recode.util.Range;
import org.junit.Test;

import java.io.IOException;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class LineNumberTableImplTest {

    private final LineNumberTableEntry exampleEntry = mock(LineNumberTableEntry.class);

    private final LineNumberTable exampleTable = new LineNumberTableImpl(new LineNumberTableEntry[]{exampleEntry}, new Range(1, 2));

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new LineNumberTableImpl(null, new Range(0, 1)), AssertionError.class);
        assertThrown(() -> new LineNumberTableImpl(new LineNumberTableEntry[]{mock(LineNumberTableEntry.class)}, null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() throws IOException {
        assertArrayEquals(new Object[]{exampleEntry}, exampleTable.getEntries().toArray());
        assertEquals(new Range(1, 2), exampleTable.getSourceFileRange());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleTable, exampleTable);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleTable.equals(null));
        assertFalse(exampleTable.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final LineNumberTable other = new LineNumberTableImpl(new LineNumberTableEntry[]{exampleEntry}, new Range(1, 2));

        assertEquals(other, exampleTable);
        assertEquals(other.hashCode(), exampleTable.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertTrue(exampleTable.toString().contains(exampleEntry.toString()));
        assertTrue(exampleTable.toString().contains(new Range(1, 2).toString()));
    }
}
