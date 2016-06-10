package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.ReturnValue;

public final class ReturnValueImpl extends AbstractElement implements ReturnValue {

    private final Expression expression;

    public ReturnValueImpl(Expression expression) {
        this(expression, null);
    }

    public ReturnValueImpl(Expression expression, ElementMetaData metaData) {
        super(metaData);

        assert expression != null : "Expression can't be null";

        this.expression = expression;
    }

    @Override
    public Expression getValue() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnValueImpl that = (ReturnValueImpl) o;

        if (!expression.equals(that.expression)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }

    @Override
    public String toString() {
        return "ReturnValueImpl{" +
                "expression=" + expression +
                '}';
    }
}
