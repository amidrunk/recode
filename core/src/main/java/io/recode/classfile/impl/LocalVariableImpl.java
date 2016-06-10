package io.recode.classfile.impl;

import io.recode.classfile.LocalVariable;

import java.lang.reflect.Type;

public class LocalVariableImpl implements LocalVariable {

    private final int startPC;

    private final int length;

    private final String variableName;

    private final Type variableType;

    private final int index;

    public LocalVariableImpl(int startPC, int length, String variableName, Type variableType, int index) {
        assert variableName != null && !variableName.isEmpty() : "Variable name can't be null or empty";
        assert variableType != null : "Variable type can't be null";

        this.startPC = startPC;
        this.length = length;
        this.variableName = variableName;
        this.variableType = variableType;
        this.index = index;
    }

    @Override
    public int getStartPC() {
        return startPC;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public String getName() {
        return variableName;
    }

    @Override
    public Type getType() {
        return variableType;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalVariableImpl that = (LocalVariableImpl) o;

        if (index != that.index) return false;
        if (length != that.length) return false;
        if (startPC != that.startPC) return false;
        if (!variableName.equals(that.variableName)) return false;
        if (!variableType.equals(that.variableType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startPC;
        result = 31 * result + length;
        result = 31 * result + variableName.hashCode();
        result = 31 * result + variableType.hashCode();
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return "LocalVariableImpl{" +
                "startPC=" + startPC +
                ", length=" + length +
                ", variableName='" + variableName + '\'' +
                ", variableType=" + variableType +
                ", index=" + index +
                '}';
    }
}
