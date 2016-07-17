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

    @Test
    public void lineNumberShouldBeNegativeIfNoLineNumberExistsForPc() {
        final LineNumberTableImpl table = new LineNumberTableImpl(new LineNumberTableEntry[]{}, Range.from(10).to(20));

        assertEquals(-1, table.getLineNumber(100));
    }

    @Test
    public void lineNumberShouldBeReturnedForMapping() {
        final LineNumberTable lineNumberTable = new LineNumberTableImpl(new LineNumberTableEntry[]{
                new LineNumberTableEntryImpl(0, 10),
                new LineNumberTableEntryImpl(12, 11),
                new LineNumberTableEntryImpl(23, 12),
                new LineNumberTableEntryImpl(40, 13)
        }, Range.from(10).to(45));

        assertEquals(10, lineNumberTable.getLineNumber(0));
        assertEquals(10, lineNumberTable.getLineNumber(1));
        assertEquals(10, lineNumberTable.getLineNumber(11));
        assertEquals(11, lineNumberTable.getLineNumber(12));
        assertEquals(11, lineNumberTable.getLineNumber(22));
        assertEquals(12, lineNumberTable.getLineNumber(23));
        assertEquals(12, lineNumberTable.getLineNumber(39));
        assertEquals(13, lineNumberTable.getLineNumber(40));
        assertEquals(13, lineNumberTable.getLineNumber(100));
    }
}
