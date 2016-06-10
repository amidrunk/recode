package io.recode.model;

public interface ArrayStore extends Statement {

    Expression getArray();

    Expression getIndex();

    Expression getValue();

    default ElementType getElementType() {
        return ElementType.ARRAY_STORE;
    }

}
