package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.LocalVariableReference;

import java.lang.reflect.Type;

public class LocalVariableReferenceImpl extends AbstractElement implements LocalVariableReference {

    private final String variableName;

    private final Type variableType;

    private final int index;

    public LocalVariableReferenceImpl(String variableName, Type variableType, int index) {
        this(variableName, variableType, index, null);
    }

    public LocalVariableReferenceImpl(String variableName, Type variableType, int index, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert variableName != null && !variableName.isEmpty() : "Variable name can't be null or empty";
        assert variableType != null : "Variable type can't be null";
        assert index >= 0 : "Index must be positive";

        this.variableName = variableName;
        this.variableType = variableType;
        this.index = index;
    }

    @Override
    public String getName() {
        return variableName;
    }

    @Override
    public Type getType() {
        return variableType;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalVariableReferenceImpl that = (LocalVariableReferenceImpl) o;

        if (index != that.index) return false;
        if (!variableName.equals(that.variableName)) return false;
        if (!variableType.equals(that.variableType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = variableName.hashCode();
        result = 31 * result + variableType.hashCode();
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return "LocalVariableReferenceImpl{" +
                "variableName='" + variableName + '\'' +
                ", variableType=" + variableType +
                ", index=" + index +
                '}';
    }
}
