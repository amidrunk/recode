package io.recode.model.impl;

import io.recode.model.*;

import java.lang.reflect.Type;

public final class UnaryOperatorImpl extends AbstractElement implements UnaryOperator {

    private final Expression operand;

    private final OperatorType operatorType;

    private final Type type;

    public UnaryOperatorImpl(Expression operand, OperatorType operatorType, Type type) {
        this(operand, operatorType, type, null);
    }

    public UnaryOperatorImpl(Expression operand, OperatorType operatorType, Type type, ElementMetaData elementMetaData) {
        super(elementMetaData);;

        assert operand != null : "Operand can't be null";
        assert operatorType != null : "Operator type can't be null";
        assert type != null : "Type can't be null";

        this.operand = operand;
        this.operatorType = operatorType;
        this.type = type;
    }

    @Override
    public Expression getOperand() {
        return operand;
    }

    @Override
    public OperatorType getOperatorType() {
        return operatorType;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.UNARY_OPERATOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnaryOperatorImpl that = (UnaryOperatorImpl) o;

        if (!operand.equals(that.operand)) return false;
        if (operatorType != that.operatorType) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = operand.hashCode();
        result = 31 * result + operatorType.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UnaryOperatorImpl{" +
                "operand=" + operand +
                ", operatorType=" + operatorType +
                ", type=" + type +
                '}';
    }
}
