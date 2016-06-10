package io.recode.model;

public final class EmptyElementMetaData implements ElementMetaData {

    public static final EmptyElementMetaData EMPTY = new EmptyElementMetaData();

    @Override
    public boolean hasProgramCounter() {
        return false;
    }

    @Override
    public int getProgramCounter() {
        throw new IllegalStateException("Program counter not available");
    }

    @Override
    public boolean hasLineNumber() {
        return false;
    }

    @Override
    public int getLineNumber() {
        throw new IllegalStateException("Line number not available");
    }
}
