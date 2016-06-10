package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Goto;

public final class GotoImpl extends AbstractElement implements Goto {

    private final int targetProgramCounter;

    public GotoImpl(int targetProgramCounter) {
        this(targetProgramCounter, null);
    }

    public GotoImpl(int targetProgramCounter, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert targetProgramCounter >= 0 : "Target program counter must be positive";

        this.targetProgramCounter = targetProgramCounter;
    }

    @Override
    public int getTargetProgramCounter() {
        return targetProgramCounter;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.GOTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GotoImpl aGoto = (GotoImpl) o;

        if (targetProgramCounter != aGoto.targetProgramCounter) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetProgramCounter;
        return result;
    }

    @Override
    public String toString() {
        return "GotoImpl{" +
                "targetProgramCounter=" + targetProgramCounter +
                '}';
    }
}
