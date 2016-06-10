package io.recode.classfile.impl;

import io.recode.classfile.ExceptionTableEntry;

import java.lang.reflect.Type;

public class ExceptionTableEntryImpl implements ExceptionTableEntry {

    private final int startPC;

    private final int endPC;

    private final int handlerPC;

    private final Type catchType;

    public ExceptionTableEntryImpl(int startPC, int endPC, int handlerPC, Type catchType) {
        this.startPC = startPC;
        this.endPC = endPC;
        this.handlerPC = handlerPC;
        this.catchType = catchType;
    }

    @Override
    public int getStartPC() {
        return startPC;
    }

    @Override
    public int getEndPC() {
        return endPC;
    }

    @Override
    public int getHandlerPC() {
        return handlerPC;
    }

    @Override
    public Type getCatchType() {
        return catchType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionTableEntryImpl that = (ExceptionTableEntryImpl) o;

        if (endPC != that.endPC) return false;
        if (handlerPC != that.handlerPC) return false;
        if (startPC != that.startPC) return false;
        if (!catchType.equals(that.catchType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startPC;
        result = 31 * result + endPC;
        result = 31 * result + handlerPC;
        result = 31 * result + catchType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ExceptionTableEntryImpl{" +
                "startPC=" + startPC +
                ", endPC=" + endPC +
                ", handlerPC=" + handlerPC +
                ", catchType=" + catchType +
                '}';
    }
}
