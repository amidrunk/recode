package io.recode.model;

public interface LocalVariableReference extends Expression {

    String getName();

    int getIndex();

    default ElementType getElementType() {
        return ElementType.VARIABLE_REFERENCE;
    }

}
