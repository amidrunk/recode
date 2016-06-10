package io.recode.decompile.impl;

import io.recode.decompile.LineNumberCounter;
import io.recode.classfile.LineNumberTable;
import io.recode.classfile.LineNumberTableEntry;
import io.recode.decompile.ProgramCounter;

public final class LineNumberCounterImpl implements LineNumberCounter {

    private final ProgramCounter programCounter;

    private final LineNumberTable lineNumberTable;

    private LineNumberTableEntry[] entries;

    private int currentLineNumberTableEntryIndex = -1;

    public LineNumberCounterImpl(ProgramCounter programCounter, LineNumberTable lineNumberTable) {
        assert programCounter != null : "Program counter can't be null";
        assert lineNumberTable != null : "Line number table can't be null";

        this.programCounter = programCounter;
        this.lineNumberTable = lineNumberTable;
    }

    @Override
    public int get() {
        final int pc = programCounter.get();

        if (currentLineNumberTableEntryIndex == -1) {
            this.entries = lineNumberTable.getEntries().stream().toArray(LineNumberTableEntry[]::new);
            this.currentLineNumberTableEntryIndex = 0;
        }

        if (currentLineNumberTableEntryIndex < entries.length - 1) {
            if (pc >= entries[currentLineNumberTableEntryIndex + 1].getStartPC()) {
                currentLineNumberTableEntryIndex++;
            }
        }

        return entries[currentLineNumberTableEntryIndex].getLineNumber();
    }
}
