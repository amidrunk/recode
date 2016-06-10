package io.recode.decompile.impl;

import io.recode.decompile.CodeStream;
import io.recode.decompile.ProgramCounter;
import io.recode.decompile.impl.ProgramCounterImpl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class InputStreamCodeStream implements CodeStream, AutoCloseable {

    public static final int PEEK_LIMIT = 128;

    private final DataInputStream inputStream;

    private final ProgramCounter programCounter;

    private boolean peeking = false;

    private int peekCount = -1;

    public InputStreamCodeStream(InputStream inputStream) {
        this(inputStream, new ProgramCounterImpl(-1));
    }

    public InputStreamCodeStream(InputStream inputStream, ProgramCounter programCounter) {
        assert inputStream != null : "Input stream can't be null";
        assert programCounter != null : "Program counter can't be null";

        this.inputStream = new DataInputStream(new BufferedInputStream(inputStream));
        this.programCounter = programCounter;
    }

    @Override
    public int nextInstruction() throws IOException {
        unpeek();

        final int instruction = inputStream.readUnsignedByte();

        programCounter.advance();

        return instruction;
    }

    @Override
    public int peekInstruction() throws IOException {
        peek();
        peekCount += 1;
        return inputStream.readUnsignedByte();
    }

    @Override
    public int nextByte() throws IOException {
        unpeek();

        final byte nextByte = inputStream.readByte();

        programCounter.advance();

        return nextByte;
    }

    @Override
    public int peekByte() throws IOException {
        peek();
        peekCount += 1;
        return inputStream.readByte();
    }

    @Override
    public int nextUnsignedByte() throws IOException {
        unpeek();

        final int nextByte = inputStream.readUnsignedByte();

        programCounter.advance();

        return nextByte;
    }

    @Override
    public int peekUnsignedByte() throws IOException {
        peek();
        peekCount += 1;
        return inputStream.readUnsignedByte();
    }

    @Override
    public int peekUnsignedShort() throws IOException {
        peek();
        peekCount += 2;
        return inputStream.readUnsignedShort();
    }

    @Override
    public int nextUnsignedShort() throws IOException {
        unpeek();

        final int nextUnsignedShort = inputStream.readUnsignedShort();

        programCounter.advance();
        programCounter.advance();

        return nextUnsignedShort;
    }

    @Override
    public int peekSignedShort() throws IOException {
        peek();
        peekCount += 2;
        return inputStream.readShort();
    }

    @Override
    public int nextSignedShort() throws IOException {
        unpeek();

        final int nextUnsignedShort = inputStream.readShort();

        programCounter.advance();
        programCounter.advance();

        return nextUnsignedShort;
    }

    @Override
    public void commit() {
        if (peeking) {
            peeking = false;

            advanceProgramCounter(peekCount);
        }
    }

    @Override
    public int skip(int count) throws IOException {
        assert count >= 0 : "Count must be greater than zero";

        if (count == 0) {
            return 0;
        }

        final int skippedBytes = (int) inputStream.skip(count);

        advanceProgramCounter(skippedBytes);

        return skippedBytes;
    }

    @Override
    public ProgramCounter pc() {
        return programCounter;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    private void advanceProgramCounter(int count) {
        for (int i = 0; i < count; i++) {
            programCounter.advance();
        }
    }

    private void peek() {
        if (!peeking) {
            inputStream.mark(PEEK_LIMIT);
            peeking = true;
            peekCount = 0;
        }
    }

    private void unpeek() throws IOException {
        if (peeking) {
            peeking = false;
            peekCount = -1;

            inputStream.reset();
        }
    }
}
