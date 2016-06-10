package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.NewInstance;
import io.recode.model.Signature;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public final class NewInstanceImpl extends AbstractElement implements NewInstance {

    private final Type type;

    private final Signature constructorSignature;

    private final List<Expression> parameters;

    public NewInstanceImpl(Type type, Signature constructorSignature, List<Expression> parameters) {
        this(type, constructorSignature, parameters, null);
    }

    public NewInstanceImpl(Type type, Signature constructorSignature, List<Expression> parameters, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert type != null : "Type can't be null";
        assert constructorSignature != null : "Constructor signature can't be null";
        assert parameters != null : "Parameters can't be null";

        this.type = type;
        this.constructorSignature = constructorSignature;
        this.parameters = parameters;
    }

    @Override
    public Signature getConstructorSignature() {
        return constructorSignature;
    }

    @Override
    public List<Expression> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewInstanceImpl that = (NewInstanceImpl) o;

        if (!constructorSignature.equals(that.constructorSignature)) return false;
        if (!parameters.equals(that.parameters)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + constructorSignature.hashCode();
        result = 31 * result + parameters.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NewInstanceImpl{" +
                "type=" + type +
                ", constructorSignature=" + constructorSignature +
                ", parameters=" + parameters +
                '}';
    }
}
