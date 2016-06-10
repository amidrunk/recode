package io.recode.model;

public interface Return extends Statement {

    default ElementType getElementType() {
        return ElementType.RETURN;
    }

}
