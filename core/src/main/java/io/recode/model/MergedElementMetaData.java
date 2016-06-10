package io.recode.model;

public final class MergedElementMetaData implements ElementMetaData {

    private final ElementMetaData firstCandidate;

    private final ElementMetaData secondCandidate;

    public MergedElementMetaData(ElementMetaData firstCandidate, ElementMetaData secondCandidate) {
        assert firstCandidate != null : "First candidate can't be null";
        assert secondCandidate != null : "Second candidate can't be null";

        this.firstCandidate = firstCandidate;
        this.secondCandidate = secondCandidate;
    }

    @Override
    public boolean hasProgramCounter() {
        return firstCandidate.hasProgramCounter() || secondCandidate.hasProgramCounter();
    }

    @Override
    public int getProgramCounter() {
        if (firstCandidate.hasProgramCounter()) {
            return firstCandidate.getProgramCounter();
        }

        if (secondCandidate.hasProgramCounter()) {
            return secondCandidate.getProgramCounter();
        }

        throw new IllegalStateException("Program counter not available");
    }

    @Override
    public boolean hasLineNumber() {
        return firstCandidate.hasLineNumber() || secondCandidate.hasLineNumber();
    }

    @Override
    public int getLineNumber() {
        if (firstCandidate.hasLineNumber()) {
            return firstCandidate.getLineNumber();
        }

        if (secondCandidate.hasLineNumber()) {
            return secondCandidate.getLineNumber();
        }

        throw new IllegalStateException("Line number not available");
    }

    public ElementMetaData getFirstCandidate() {
        return firstCandidate;
    }

    public ElementMetaData getSecondCandidate() {
        return secondCandidate;
    }
}
