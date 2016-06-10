package io.recode.model;

public interface FieldAssignment extends Statement {

    FieldReference getFieldReference();

    Expression getValue();

    default ElementType getElementType() {
        return ElementType.FIELD_ASSIGNMENT;
    }

}
