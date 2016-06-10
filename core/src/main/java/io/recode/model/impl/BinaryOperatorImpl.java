package io.recode.model.impl;

import io.recode.model.BinaryOperator;
import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.OperatorType;

import java.lang.reflect.Type;

public final class BinaryOperatorImpl extends AbstractElement implements BinaryOperator {

    private final Expression leftOperand;

    private final OperatorType operatorType;

    private final Expression rightOperand;

    private final Type resultType;

    public BinaryOperatorImpl(Expression leftOperand, OperatorType operatorType, Expression rightOperand, Type resultType) {
        this(leftOperand, operatorType, rightOperand, resultType, null);
    }

    public BinaryOperatorImpl(Expression leftOperand, OperatorType operatorType, Expression rightOperand, Type resultType, ElementMetaData metaData) {
        super(metaData);

        assert leftOperand != null : "Left operand can't be null";
        assert operatorType != null : "Operator type can't be null";
        assert rightOperand != null : "Right operand can't be null";
        assert resultType != null : "Result type can't be null";

        this.leftOperand = leftOperand;
        this.operatorType = operatorType;
        this.rightOperand = rightOperand;
        this.resultType = resultType;
    }

    @Override
    public Expression getLeftOperand() {
        return leftOperand;
    }

    @Override
    public OperatorType getOperatorType() {
        return operatorType;
    }

    @Override
    public Expression getRightOperand() {
        return rightOperand;
    }

    @Override
    public Type getType() {
        return resultType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryOperatorImpl that = (BinaryOperatorImpl) o;

        if (!leftOperand.equals(that.leftOperand)) return false;
        if (operatorType != that.operatorType) return false;
        if (!resultType.equals(that.resultType)) return false;
        if (!rightOperand.equals(that.rightOperand)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = leftOperand.hashCode();
        result = 31 * result + operatorType.hashCode();
        result = 31 * result + rightOperand.hashCode();
        result = 31 * result + resultType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BinaryOperatorImpl{" +
                "leftOperand=" + leftOperand +
                ", operatorType=" + operatorType +
                ", rightOperand=" + rightOperand +
                ", resultType=" + resultType +
                '}';
    }
}
