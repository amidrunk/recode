package io.recode.model.impl;

import io.recode.util.Types;
import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.IncompatibleTypeException;
import io.recode.model.VariableAssignment;

import java.lang.reflect.Type;

public final class VariableAssignmentImpl extends AbstractElement implements VariableAssignment {

    private final Expression value;

    private final int variableIndex;

    private final String variableName;

    private final Type variableType;

    public VariableAssignmentImpl(Expression value, int variableIndex, String variableName, Type variableType) {
        this(value, variableIndex, variableName, variableType, null);
    }
    public VariableAssignmentImpl(Expression value, int variableIndex, String variableName, Type variableType, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert value != null : "Value can't be null";
        assert variableIndex >= 0 : "Variable must be positive";
        assert variableName != null && !variableName.isEmpty() : "Variable name can't be null or empty";
        assert variableType != null : "Variable type can't be null";

        this.value = value;
        this.variableIndex = variableIndex;
        this.variableName = variableName;
        this.variableType = variableType;
    }

    @Override
    public int getVariableIndex() {
        return variableIndex;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public Type getVariableType() {
        return variableType;
    }

    @Override
    public VariableAssignment withValue(Expression value) {
        assert value != null : "Value can't be null";

        if (!Types.isValueTypePotentiallyAssignableTo(value.getType(), variableType)) {
            throw new IncompatibleTypeException("Value can't be assigned to variable '"
                    + variableName + ":" + variableType.getTypeName() + "': " + value);
        }

        return new VariableAssignmentImpl(value, variableIndex, variableName, variableType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableAssignmentImpl that = (VariableAssignmentImpl) o;

        if (variableIndex != that.variableIndex) return false;
        if (!value.equals(that.value)) return false;
        if (!variableName.equals(that.variableName)) return false;
        if (!variableType.equals(that.variableType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + variableIndex;
        result = 31 * result + variableName.hashCode();
        result = 31 * result + variableType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "VariableAssignmentImpl{" +
                "value=" + value +
                ", variableIndex=" + variableIndex +
                ", variableName='" + variableName + '\'' +
                ", variableType=" + variableType +
                '}';
    }
}
