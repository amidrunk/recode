package io.recode.classfile.impl;

import io.recode.classfile.impl.ExceptionTableEntryImpl;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExceptionTableEntryImplTest {

    @Test
    public void constructorShouldRetainParameters() {
        final Type catchType = Mockito.mock(Type.class);
        final ExceptionTableEntryImpl entry = new ExceptionTableEntryImpl(1, 2, 3, catchType);

        assertEquals(1, entry.getStartPC());
        assertEquals(2, entry.getEndPC());
        assertEquals(3, entry.getHandlerPC());
        assertEquals(catchType, entry.getCatchType());
    }

    @Test
    public void tableEntryRepresentingFinallyClauseCanBeCreated() {
        final ExceptionTableEntryImpl finallyEntry = new ExceptionTableEntryImpl(0, 1, 2, null);

        assertNull(finallyEntry.getCatchType());
        assertEquals(0, finallyEntry.getStartPC());
        assertEquals(1, finallyEntry.getEndPC());
        assertEquals(2, finallyEntry.getHandlerPC());
    }
}
