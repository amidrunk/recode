package io.recode.model;

public interface ArrayLoad extends Expression {

    Expression getArray();

    Expression getIndex();

    default ElementType getElementType() {
        return ElementType.ARRAY_LOAD;
    }

}
