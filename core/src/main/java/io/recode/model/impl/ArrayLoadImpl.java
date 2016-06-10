package io.recode.model.impl;

import io.recode.model.ArrayLoad;
import io.recode.model.ElementMetaData;
import io.recode.model.Expression;

import java.lang.reflect.Type;

public final class ArrayLoadImpl extends AbstractElement implements ArrayLoad {

    private final Expression array;

    private final Expression index;

    private final Type type;

    public ArrayLoadImpl(Expression array, Expression index, Type type) {
        this(array, index, type, null);
    }

    public ArrayLoadImpl(Expression array, Expression index, Type type, ElementMetaData metaData) {
        super(metaData);

        assert array != null : "Array can't be null";
        assert index != null : "Index can't be null";
        assert type != null : "Type can't be null";

        this.array = array;
        this.index = index;
        this.type = type;
    }

    @Override
    public Expression getArray() {
        return array;
    }

    @Override
    public Expression getIndex() {
        return index;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayLoadImpl arrayLoad = (ArrayLoadImpl) o;

        if (!array.equals(arrayLoad.array)) return false;
        if (!index.equals(arrayLoad.index)) return false;
        if (!type.equals(arrayLoad.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = array.hashCode();
        result = 31 * result + index.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ArrayLoadImpl{" +
                "array=" + array +
                ", index=" + index +
                ", type=" + type +
                '}';
    }
}
