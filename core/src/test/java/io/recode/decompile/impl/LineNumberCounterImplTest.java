package io.recode.decompile.impl;

import io.recode.classfile.LineNumberTable;
import io.recode.classfile.impl.LineNumberTableEntryImpl;
import io.recode.decompile.ProgramCounter;
import org.junit.Test;

import java.util.Arrays;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LineNumberCounterImplTest {

    private final LineNumberTable lineNumberTable = mock(LineNumberTable.class);

    private final ProgramCounter programCounter = mock(ProgramCounter.class);

    private final LineNumberCounterImpl lineNumberCounter = new LineNumberCounterImpl(programCounter, lineNumberTable);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new LineNumberCounterImpl(null, lineNumberTable), AssertionError.class);
        assertThrown(() -> new LineNumberCounterImpl(programCounter, null), AssertionError.class);
    }

    @Test
    public void getLineNumberShouldResolveLineNumberFromLineNumberTable() {
        when(lineNumberTable.getEntries()).thenReturn(Arrays.asList(
                new LineNumberTableEntryImpl(0, 1),
                new LineNumberTableEntryImpl(3, 2),
                new LineNumberTableEntryImpl(5, 3)
        ));

        when(programCounter.get()).thenReturn(0, 1, 2, 3, 4, 5, 6, 7, 8);

        assertEquals(1, lineNumberCounter.get());
        assertEquals(1, lineNumberCounter.get());
        assertEquals(1, lineNumberCounter.get());
        assertEquals(2, lineNumberCounter.get());
        assertEquals(2, lineNumberCounter.get());
        assertEquals(3, lineNumberCounter.get());
        assertEquals(3, lineNumberCounter.get());
        assertEquals(3, lineNumberCounter.get());
        assertEquals(3, lineNumberCounter.get());
    }
}
