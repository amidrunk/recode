package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.FieldAssignment;
import io.recode.model.FieldReference;

public final class FieldAssignmentImpl extends AbstractElement implements FieldAssignment {

    private final FieldReference fieldReference;

    private final Expression value;

    public FieldAssignmentImpl(FieldReference fieldReference, Expression value) {
        this(fieldReference, value, null);
    }

    public FieldAssignmentImpl(FieldReference fieldReference, Expression value, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert fieldReference != null : "Field reference can't be null";
        assert value != null : "Value can't be null";

        this.fieldReference = fieldReference;
        this.value = value;
    }

    @Override
    public FieldReference getFieldReference() {
        return fieldReference;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldAssignmentImpl that = (FieldAssignmentImpl) o;

        if (!fieldReference.equals(that.fieldReference)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldReference.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldAssignmentImpl{" +
                "fieldReference=" + fieldReference +
                ", value=" + value +
                '}';
    }
}
