package io.recode.model;

public interface ReturnValue extends Statement {

    Expression getValue();

    default ElementType getElementType() {
        return ElementType.RETURN_VALUE;
    }

}
