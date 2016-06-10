package io.recode.model.impl;

import io.recode.model.Compare;
import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Expression;

import java.lang.reflect.Type;

public final class CompareImpl extends AbstractElement implements Compare {

    private final Expression leftOperand;

    private final Expression rightOperand;

    public CompareImpl(Expression leftOperand, Expression rightOperand) {
        this(leftOperand, rightOperand, null);
    }

    public CompareImpl(Expression leftOperand, Expression rightOperand, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert leftOperand != null : "Left operand can't be null";
        assert rightOperand != null : "Right operand can't be null";

        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    @Override
    public Expression getLeftOperand() {
        return leftOperand;
    }

    @Override
    public Expression getRightOperand() {
        return rightOperand;
    }

    @Override
    public Type getType() {
        return int.class;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.COMPARE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompareImpl compare = (CompareImpl) o;

        if (!leftOperand.equals(compare.leftOperand)) return false;
        if (!rightOperand.equals(compare.rightOperand)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = leftOperand.hashCode();
        result = 31 * result + rightOperand.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CompareImpl{" +
                "leftOperand=" + leftOperand +
                ", rightOperand=" + rightOperand +
                '}';
    }
}
