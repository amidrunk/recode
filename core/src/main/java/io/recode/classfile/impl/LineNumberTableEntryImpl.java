package io.recode.classfile.impl;

import io.recode.classfile.LineNumberTableEntry;

public final class LineNumberTableEntryImpl implements LineNumberTableEntry {

    private final int startPC;

    private final int lineNumber;

    public LineNumberTableEntryImpl(int startPC, int lineNumber) {
        this.startPC = startPC;
        this.lineNumber = lineNumber;
    }

    @Override
    public int getStartPC() {
        return startPC;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineNumberTableEntryImpl that = (LineNumberTableEntryImpl) o;

        if (lineNumber != that.lineNumber) return false;
        if (startPC != that.startPC) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startPC;
        result = 31 * result + lineNumber;
        return result;
    }

    @Override
    public String toString() {
        return "LineNumberTableEntryImpl{" +
                "startPC=" + startPC +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
