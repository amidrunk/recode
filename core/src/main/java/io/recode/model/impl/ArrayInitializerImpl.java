package io.recode.model.impl;

import io.recode.model.ArrayInitializer;
import io.recode.model.Expression;

public final class ArrayInitializerImpl implements ArrayInitializer {

    private final int index;

    private final Expression value;

    public ArrayInitializerImpl(int index, Expression value) {
        assert index >= 0: "Index must be positive";
        assert value != null : "Value can't be null";

        this.index = index;
        this.value = value;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayInitializerImpl that = (ArrayInitializerImpl) o;

        if (index != that.index) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ArrayInitializerImpl{" +
                "index=" + index +
                ", value=" + value +
                '}';
    }
}
