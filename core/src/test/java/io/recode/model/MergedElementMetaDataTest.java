package io.recode.model;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MergedElementMetaDataTest {

    private final ElementMetaData candidate1 = mock(ElementMetaData.class, "candidate1");

    private final ElementMetaData candidate2 = mock(ElementMetaData.class, "candidate2");

    private final MergedElementMetaData mergedElementMetaData = new MergedElementMetaData(candidate1, candidate2);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new MergedElementMetaData(null, candidate2), AssertionError.class);
        assertThrown(() -> new MergedElementMetaData(candidate1, null), AssertionError.class);
    }

    @Test
    public void constructorShouldReturnCandidates() {
        assertEquals(candidate1, mergedElementMetaData.getFirstCandidate());
        assertEquals(candidate2, mergedElementMetaData.getSecondCandidate());
    }

    @Test
    public void propertiesShouldBeReturnedFromFirstCandidateIfAvailable() {
        when(candidate1.hasLineNumber()).thenReturn(true);
        when(candidate1.getLineNumber()).thenReturn(1234);
        when(candidate1.hasProgramCounter()).thenReturn(true);
        when(candidate1.getProgramCounter()).thenReturn(2345);

        assertTrue(mergedElementMetaData.hasLineNumber());
        assertEquals(1234, mergedElementMetaData.getLineNumber());
        assertTrue(mergedElementMetaData.hasProgramCounter());
        assertEquals(2345, mergedElementMetaData.getProgramCounter());

        verifyZeroInteractions(candidate2);
    }

    @Test
    public void propertiesShouldBeReturnedFromSecondCandidateIfNotAvailableInFirst() {
        when(candidate1.hasLineNumber()).thenReturn(false);
        when(candidate1.hasProgramCounter()).thenReturn(false);
        when(candidate2.hasLineNumber()).thenReturn(true);
        when(candidate2.getLineNumber()).thenReturn(1234);
        when(candidate2.hasProgramCounter()).thenReturn(true);
        when(candidate2.getProgramCounter()).thenReturn(2345);

        assertTrue(mergedElementMetaData.hasLineNumber());
        assertEquals(1234, mergedElementMetaData.getLineNumber());
        assertTrue(mergedElementMetaData.hasProgramCounter());
        assertEquals(2345, mergedElementMetaData.getProgramCounter());

        verify(candidate2).getLineNumber();
        verify(candidate2).getProgramCounter();
    }

    @Test
    public void propertiesShouldNotBeAvailableIfNotAvailableInAnyCandidate() {
        when(candidate1.hasLineNumber()).thenReturn(false);
        when(candidate1.hasProgramCounter()).thenReturn(false);
        when(candidate2.hasLineNumber()).thenReturn(false);
        when(candidate2.hasProgramCounter()).thenReturn(false);

        assertFalse(mergedElementMetaData.hasLineNumber());
        assertThrown(() -> mergedElementMetaData.getLineNumber(), IllegalStateException.class);
        assertFalse(mergedElementMetaData.hasProgramCounter());
        assertThrown(() -> mergedElementMetaData.getProgramCounter(), IllegalStateException.class);
    }

}