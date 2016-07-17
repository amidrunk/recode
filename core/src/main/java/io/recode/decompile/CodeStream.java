package io.recode.decompile;

import java.io.IOException;

public interface CodeStream extends AutoCloseable {

    int nextInstruction() throws IOException;

    int peekInstruction() throws IOException;

    int nextByte() throws IOException;

    int peekByte() throws IOException;

    int nextUnsignedByte() throws IOException;

    int peekUnsignedByte() throws IOException;

    int peekUnsignedShort() throws IOException;

    int nextUnsignedShort() throws IOException;

    int peekSignedShort() throws IOException;

    int nextSignedShort() throws IOException;

    /**
     * Commit to the peeked result. The buffered data accumulated to enable reset will be discarded and
     * the PC will be advanced to the new location. Note that the PC will be forwarded through all
     * instructions, i.e. if the PC is advanced n instructions, it will be called n times.
     */
    void commit();

    /**
     * Skips data in the code stream. The code stream value will be updated and <code>count</code> bytes
     * will be discarded.
     *
     * @param count The number of bytes to skip.
     */
    int skip(int count) throws IOException;

    /**
     * Returns the "program counter", i.e. an instance that tracks the pc value. The program counter is
     * advanced whenever an instruction or byte code data is read from the stream. It is also advanced
     * when data is peeked and subsequently committed. When committing peaked data, the program counter
     * will be advanced so each peeked byte will be visited by the pc.
     *
     * @return The program counter.
     */
    ProgramCounter pc();

    @Override
    void close() throws IOException;
}
