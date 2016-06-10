package io.recode.decompile;

/**
 * An interface that provides information about the byte code currently being processed.
 */
public interface InstructionContext {

    /**
     * Returns the byte code currently being processed.
     *
     * @return The current byte code.
     */
    int getByteCode();

    /**
     * Returns the program counter value of the byte code currently being processed.
     *
     * @return The program counter of the current byte code.
     */
    int getProgramCounter();

    /**
     * Returns the line number of the byte code being processed.
     *
     * @return The line number of the byte code.
     */
    int getLineNumber();

}
