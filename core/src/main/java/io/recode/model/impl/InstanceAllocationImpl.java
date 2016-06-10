package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.InstanceAllocation;

import java.lang.reflect.Type;

public final class InstanceAllocationImpl extends AbstractElement implements InstanceAllocation {

    private final Type type;

    public InstanceAllocationImpl(Type type) {
        this(type, null);
    }

    public InstanceAllocationImpl(Type type, ElementMetaData metaData) {
        super(metaData);

        assert type != null : "Type can't be null";

        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InstanceAllocationImpl that = (InstanceAllocationImpl) o;

        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "AllocateInstanceImpl{" +
                "type=" + type +
                '}';
    }
}
