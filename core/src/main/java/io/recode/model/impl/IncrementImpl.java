package io.recode.model.impl;

import io.recode.model.*;

import java.lang.reflect.Type;

public class IncrementImpl extends AbstractElement implements Increment {

    private final LocalVariableReference localVariableReference;

    private final Expression value;

    private final Type resultType;

    private final Affix affix;

    public IncrementImpl(LocalVariableReference localVariableReference, Expression value, Type resultType, Affix affix) {
        this(localVariableReference, value, resultType, affix, null);
    }

    public IncrementImpl(LocalVariableReference localVariableReference, Expression value, Type resultType, Affix affix, ElementMetaData metaData) {
        super(metaData);

        assert localVariableReference != null : "Local variable reference can't be null";
        assert value != null : "Value can't be null";
        assert resultType != null : "Result type can't be null";
        assert affix != null : "Affix can't be null";

        this.localVariableReference = localVariableReference;
        this.value = value;
        this.resultType = resultType;
        this.affix = affix;
    }

    @Override
    public LocalVariableReference getLocalVariable() {
        return localVariableReference;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public Affix getAffix() {
        return affix;
    }

    @Override
    public Type getType() {
        return resultType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IncrementImpl increment = (IncrementImpl) o;

        if (!localVariableReference.equals(increment.localVariableReference)) return false;
        if (!value.equals(increment.value)) return false;
        if (!affix.equals(increment.affix)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = localVariableReference.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + affix.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "IncrementImpl{" +
                "localVariableReference=" + localVariableReference +
                ", value=" + value +
                ", affix=" + affix +
                '}';
    }
}
