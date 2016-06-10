package io.recode.model.impl;

import io.recode.model.ArrayInitializer;
import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.NewArray;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public final class NewArrayImpl extends AbstractElement implements NewArray {

    private final Type arrayType;

    private final Type componentType;

    private final Expression length;

    private final List<ArrayInitializer> initializers;

    public NewArrayImpl(Type arrayType, Type componentType, Expression length, List<ArrayInitializer> initializers) {
        this(arrayType, componentType, length, initializers, null);
    }

    public NewArrayImpl(Type arrayType, Type componentType, Expression length, List<ArrayInitializer> initializers, ElementMetaData metaData) {
        super(metaData);

        assert arrayType != null : "Array type can't be null";
        assert componentType != null : "Component type can't be null";
        assert length != null : "Length can't be null";
        assert initializers != null : "Initializers can't be null";

        this.arrayType = arrayType;
        this.componentType = componentType;
        this.length = length;
        this.initializers = initializers;
    }

    @Override
    public Type getComponentType() {
        return componentType;
    }

    @Override
    public Expression getLength() {
        return length;
    }

    @Override
    public List<ArrayInitializer> getInitializers() {
        return Collections.unmodifiableList(initializers);
    }

    @Override
    public Type getType() {
        return arrayType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewArrayImpl newArray = (NewArrayImpl) o;

        if (!arrayType.equals(newArray.arrayType)) return false;
        if (!componentType.equals(newArray.componentType)) return false;
        if (!initializers.equals(newArray.initializers)) return false;
        if (!length.equals(newArray.length)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arrayType.hashCode();
        result = 31 * result + componentType.hashCode();
        result = 31 * result + length.hashCode();
        result = 31 * result + initializers.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NewArrayImpl{" +
                "arrayType=" + arrayType +
                ", componentType=" + componentType +
                ", length=" + length +
                ", initializers=" + initializers +
                '}';
    }
}
