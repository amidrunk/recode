package io.recode.model;

public final class ElementContextMetaData implements ElementMetaData {

    private final int programCounter;

    private final int lineNumber;

    public ElementContextMetaData(int programCounter, int lineNumber) {
        assert programCounter >= 0 : "Program counter must be positive";

        this.programCounter = programCounter;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean hasProgramCounter() {
        return true;
    }

    @Override
    public int getProgramCounter() {
        return programCounter;
    }

    @Override
    public boolean hasLineNumber() {
        return (lineNumber != -1);
    }

    @Override
    public int getLineNumber() {
        if (!hasLineNumber()) {
            throw new IllegalStateException("Line number is not available");
        }

        return lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementContextMetaData metaData = (ElementContextMetaData) o;

        if (lineNumber != metaData.lineNumber) return false;
        if (programCounter != metaData.programCounter) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = programCounter;
        result = 31 * result + lineNumber;
        return result;
    }

    @Override
    public String toString() {
        return "ElementContextMetaData{" +
                "programCounter=" + programCounter +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
