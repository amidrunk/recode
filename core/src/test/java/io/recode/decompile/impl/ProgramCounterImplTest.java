package io.recode.decompile.impl;

import io.recode.decompile.ProgramCounter;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ProgramCounterImplTest {

    @Test
    public void defaultConstructorShouldInitializePCToZero() {
        assertEquals(0, new ProgramCounterImpl().get());
    }

    @Test
    public void programCounterShouldRetainPC() {
        final ProgramCounter pc = new ProgramCounterImpl(1234);

        assertEquals(1234, pc.get());
    }

    @Test
    public void advanceShouldIncreaseProgramCounterValue() {
        final ProgramCounter programCounter = new ProgramCounterImpl();

        programCounter.advance();

        assertEquals(1, programCounter.get());
    }

    @Test
    public void lookAheadProcedureShouldBeCalledWhenPCReachesValue() throws Exception {
        final Runnable procedure1 = mock(Runnable.class);
        final Runnable procedure2 = mock(Runnable.class);
        final Runnable procedure3 = mock(Runnable.class);
        final Runnable procedure4 = mock(Runnable.class);

        final ProgramCounter programCounter = new ProgramCounterImpl();

        programCounter.lookAhead(1, procedure1);
        programCounter.lookAhead(1, procedure2);
        programCounter.lookAhead(3, procedure3);
        programCounter.lookAhead(4, procedure4);

        programCounter.advance();

        verify(procedure1).run();
        verify(procedure2).run();
        verifyZeroInteractions(procedure3, procedure4);

        programCounter.advance();

        verifyNoMoreInteractions(procedure1, procedure2);
        verifyZeroInteractions(procedure3, procedure4);

        programCounter.advance();
        verifyNoMoreInteractions(procedure1, procedure2);
        verify(procedure3).run();
        verifyZeroInteractions(procedure4);

        programCounter.advance();

        verifyNoMoreInteractions(procedure1, procedure2, procedure3);
        verify(procedure4).run();
    }

    @Test
    public void lookAheadShouldFailIfProvidedPCIsLessThanOrEqualToCurrentPC() {
        final ProgramCounter pc = new ProgramCounterImpl(1);

        assertThrown(() -> pc.lookAhead(0, mock(Runnable.class)), AssertionError.class);
        assertThrown(() -> pc.lookAhead(1, mock(Runnable.class)), AssertionError.class);
    }

    @Test
    public void lookAheadShouldNotAcceptNullProcedure() {
        final ProgramCounter pc = new ProgramCounterImpl();

        assertThrown(() -> pc.lookAhead(1, null), AssertionError.class);
    }
}
