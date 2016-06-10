package io.recode.decompile.impl;

import io.recode.decompile.CodeStream;
import io.recode.decompile.ProgramCounter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class InputStreamCodeStreamTest {

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new InputStreamCodeStream(null, new ProgramCounterImpl()), AssertionError.class);
        assertThrown(() -> new InputStreamCodeStream(mock(InputStream.class), null), AssertionError.class);
    }

    @Test
    public void nextInstructionShouldReturnNextByteInStream() throws Exception {
        final InputStreamCodeStream in = new InputStreamCodeStream(in(1, 2, 3), new ProgramCounterImpl());

        assertEquals(1, in.nextInstruction());
        assertEquals(2, in.nextInstruction());
        assertEquals(3, in.nextInstruction());
    }

    @Test
    public void nextByteShouldReturnNextByteInStream() throws Exception {
        final InputStreamCodeStream in = new InputStreamCodeStream(in(-1, 0, 1), new ProgramCounterImpl());

        assertEquals(-1, in.nextByte());
        assertEquals(in.nextByte(), 0);
        assertEquals(in.nextByte(), 1);
    }

    @Test
    public void allNextMethodsShouldFailIfEOFHasBeenReached() {
        final InputStreamCodeStream in = new InputStreamCodeStream(in(), new ProgramCounterImpl());

        assertThrown(in::nextByte, EOFException.class);
        assertThrown(in::nextInstruction, EOFException.class);
        assertThrown(in::nextUnsignedShort, EOFException.class);
    }

    @Test
    public void nextMethodsShouldAdvancePC() throws IOException {
        final ProgramCounter pc = new ProgramCounterImpl(-1);
        final InputStreamCodeStream stream = new InputStreamCodeStream(in(1, 2, 3, 4, 5), pc);

        stream.nextByte();
        assertEquals(0, pc.get());

        stream.nextInstruction();
        assertEquals(1, pc.get());

        stream.nextUnsignedShort();
        assertEquals(3, pc.get());
    }

    @Test
    public void peekMethodsShouldReturnNextDataInStreamWithoutAdvancingPC() throws IOException {
        final ProgramCounter pc = new ProgramCounterImpl();
        final InputStreamCodeStream in = new InputStreamCodeStream(in(1, 2, 3, 4, 5), pc);

        assertEquals(1, in.peekInstruction());
        assertEquals(2, in.peekByte());
        assertEquals(3 << 8 | 4, in.peekUnsignedShort());
        assertEquals(0, pc.get());
    }

    @Test
    public void commitShouldDiscardPeekBufferAndAdvancePC() throws Exception {
        final ProgramCounter pc = mock(ProgramCounter.class);
        final CodeStream cs = new InputStreamCodeStream(in(1, 2, 3, 4, 5), pc);

        assertEquals(1, cs.peekInstruction());
        assertEquals(2, cs.peekByte());
        assertEquals(3 << 8 | 4, cs.peekUnsignedShort());

        cs.commit();

        verify(pc, times(4)).advance();

        assertEquals(5, cs.nextInstruction());
    }

    @Test
    public void readMethodsShouldResetPeek() throws IOException {
        final ProgramCounter pc = mock(ProgramCounter.class);
        final InputStreamCodeStream stream = new InputStreamCodeStream(in(1, 2, 3, 4), pc);

        assertEquals(1, stream.peekInstruction());
        assertEquals(1, stream.nextInstruction());
        verify(pc, times(1)).advance();

        assertEquals(2, stream.peekByte());
        assertEquals(2, stream.nextByte());
        verify(pc, times(2)).advance();

        assertEquals(3 << 8 | 4, stream.peekUnsignedShort());
        assertEquals(3 << 8 | 4, stream.nextUnsignedShort());
        verify(pc, times(4)).advance();
    }

    @Test
    public void pcShouldReturnProvidedProgramCounter() {
        final ProgramCounter pc = mock(ProgramCounter.class);
        final InputStreamCodeStream in = new InputStreamCodeStream(in(1, 2), pc);

        assertEquals(pc, in.pc());
    }

    @Test
    public void skipShouldNotAcceptInvalidCount() {
        final InputStreamCodeStream in = new InputStreamCodeStream(in(1, 2), mock(ProgramCounter.class));

        assertThrown(() -> in.skip(-1), AssertionError.class);
    }

    @Test
    public void skipShouldDiscardBytesAndAdvancePC() throws IOException {
        final ProgramCounter pc = mock(ProgramCounter.class);
        final InputStreamCodeStream in = new InputStreamCodeStream(in(1, 2, 3, 4), pc);

        assertEquals(2, in.skip(2));
        verify(pc, times(2)).advance();
        assertEquals(3, in.nextByte());
        assertEquals(4, in.nextByte());
    }

    @Test
    public void skipShouldSkipAllBytesAndAdvanceIfCountIsGreaterThanAvailable() throws IOException {
        final ProgramCounter pc = mock(ProgramCounter.class);
        final InputStreamCodeStream in = new InputStreamCodeStream(in(1, 2, 3, 4), pc);

        assertEquals(4, in.skip(10));
        verify(pc, times(4)).advance();

        assertThrown(() -> in.nextByte(), EOFException.class);
    }

    @Test
    public void skipShouldIgnoreZeroCount() throws IOException {
        final ProgramCounter pc = mock(ProgramCounter.class);
        final InputStreamCodeStream in = new InputStreamCodeStream(in(1, 2, 3, 4), pc);

        assertEquals(0, in.skip(0));
        verifyZeroInteractions(pc);
        assertEquals(1, in.nextByte());
    }

    private InputStream in(int ... data) {
        final byte[] buf = new byte[data.length];

        for (int i = 0;i < data.length; i++) {
            buf[i] = (byte) data[i];
        }

        return new ByteArrayInputStream(buf);
    }
}
