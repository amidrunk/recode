package io.recode.classfile.impl;

import io.recode.classfile.LineNumberTable;
import io.recode.classfile.LineNumberTableEntry;
import io.recode.util.Range;

import java.util.Arrays;
import java.util.List;

public final class LineNumberTableImpl implements LineNumberTable {

    private final LineNumberTableEntry[] entries;

    private final Range sourceFileRange;

    public LineNumberTableImpl(LineNumberTableEntry[] entries, Range sourceFileRange) {
        assert entries != null : "Entries can't be null";
        assert sourceFileRange != null : "Source file range can't be null";

        this.entries = entries;
        this.sourceFileRange = sourceFileRange;
    }

    @Override
    public List<LineNumberTableEntry> getEntries() {
        return Arrays.asList(entries);
    }

    @Override
    public Range getSourceFileRange() {
        return sourceFileRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineNumberTableImpl that = (LineNumberTableImpl) o;

        if (!Arrays.equals(entries, that.entries)) return false;
        if (!sourceFileRange.equals(that.sourceFileRange)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(entries);
        result = 31 * result + sourceFileRange.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LineNumberTableImpl{" +
                "entries=" + Arrays.toString(entries) +
                ", sourceFileRange=" + sourceFileRange +
                '}';
    }

}
