package io.recode.model;

public interface Constant extends Expression {

    Object getConstant();

    default ElementType getElementType() {
        return ElementType.CONSTANT;
    }

}
