package io.recode.model;

public interface Branch extends Statement {

    Expression getLeftOperand();

    OperatorType getOperatorType();

    Expression getRightOperand();

    int getTargetProgramCounter();

    default ElementType getElementType() {
        return ElementType.BRANCH;
    }

}
