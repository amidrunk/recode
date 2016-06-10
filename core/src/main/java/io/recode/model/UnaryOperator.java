package io.recode.model;

public interface UnaryOperator extends Expression {

    Expression getOperand();

    OperatorType getOperatorType();

}
