package io.recode.decompile;

public interface ProgramCounter {

    void lookAhead(int targetPc, Runnable procedure);

    void advance();

    int get();

}
