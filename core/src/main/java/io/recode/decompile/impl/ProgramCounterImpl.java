package io.recode.decompile.impl;

import io.recode.decompile.ProgramCounter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ProgramCounterImpl implements ProgramCounter {

    private final List<LookAhead> lookAheadList = new LinkedList<>();

    private int value;

    public ProgramCounterImpl() {
        this(0);
    }

    public ProgramCounterImpl(int value) {
        this.value = value;
    }

    @Override
    public void lookAhead(int targetPc, Runnable procedure) {
        assert targetPc > value : "Target PC must be > current PC (" + value + ")";
        assert procedure != null : "Procedure can't be null";

        lookAheadList.add(new LookAhead(targetPc, procedure));
    }

    @Override
    public void advance() {
        value++;

        for (Iterator<LookAhead> i = lookAheadList.iterator(); i.hasNext(); ) {
            final LookAhead lookAhead = i.next();

            if (lookAhead.getPc() == value) {
                lookAhead.getProcedure().run();
                i.remove();
            }
        }
    }

    @Override
    public int get() {
        return value;
    }

    private static final class LookAhead {

        private final int pc;

        private final Runnable procedure;

        private LookAhead(int pc, Runnable procedure) {
            this.pc = pc;
            this.procedure = procedure;
        }

        public int getPc() {
            return pc;
        }

        public Runnable getProcedure() {
            return procedure;
        }
    }

}
