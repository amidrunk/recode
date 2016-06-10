package io.recode.model;

public interface TypeCast extends Expression {

    Expression getValue();

    default ElementType getElementType() {
        return ElementType.CAST;
    }

}
