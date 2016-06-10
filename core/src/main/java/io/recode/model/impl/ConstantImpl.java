package io.recode.model.impl;

import io.recode.util.Types;
import io.recode.model.Constant;
import io.recode.model.ElementMetaData;

import java.lang.reflect.Type;
import java.util.Objects;

public final class ConstantImpl extends AbstractElement implements Constant {

    private final Object constant;

    private final Type type;

    public ConstantImpl(Object constant, Type type) {
        this(constant, type, null);
    }

    public ConstantImpl(Object constant, Type type, ElementMetaData metaData) {
        super(metaData);

        assert type != null : "Type can't be null";
        assert !Types.isPrimitive(type) || constant != null : "Constant can't be null";

        this.constant = constant;
        this.type = type;
    }

    @Override
    public Object getConstant() {
        return constant;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConstantImpl that = (ConstantImpl) o;

        if (!Objects.equals(constant, that.constant)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return constant.hashCode();
    }

    @Override
    public String toString() {
        return "ConstantImpl{" +
                "constant=" + constant + ", " +
                "type=" + type +
                '}';
    }
}
