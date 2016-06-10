package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.TypeCast;
import io.recode.model.Expression;

import java.lang.reflect.Type;

public final class TypeCastImpl extends AbstractElement implements TypeCast {

    private final Expression value;

    private final Type type;

    public TypeCastImpl(Expression value, Type type) {
        this(value, type, null);
    }

    public TypeCastImpl(Expression value, Type type, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert value != null : "Value can't be null";
        assert type != null : "Type can't be null";

        this.value = value;
        this.type = type;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeCastImpl cast = (TypeCastImpl) o;

        if (!type.equals(cast.type)) return false;
        if (!value.equals(cast.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CastImpl{" +
                "value=" + value +
                ", type=" + type +
                '}';
    }
}
