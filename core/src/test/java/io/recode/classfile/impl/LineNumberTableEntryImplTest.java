package io.recode.classfile.impl;

import io.recode.classfile.impl.LineNumberTableEntryImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineNumberTableEntryImplTest {

    @Test
    public void constructorShouldRetainParameters() {
        final LineNumberTableEntryImpl entry = new LineNumberTableEntryImpl(1, 2);

        assertEquals(1, entry.getStartPC());
        assertEquals(2, entry.getLineNumber());
    }
}
