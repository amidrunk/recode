package io.recode.model.impl;

import io.recode.model.Branch;
import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.OperatorType;

public final class BranchImpl extends AbstractElement implements Branch {

    private final Expression leftOperand;

    private final OperatorType operatorType;

    private final Expression rightOperand;

    private final int targetProgramCounter;

    public BranchImpl(Expression leftOperand, OperatorType operatorType, Expression rightOperand, int targetProgramCounter) {
        this(leftOperand, operatorType, rightOperand, targetProgramCounter, null);
    }

    public BranchImpl(Expression leftOperand, OperatorType operatorType, Expression rightOperand, int targetProgramCounter, ElementMetaData metaData) {
        super(metaData);

        assert leftOperand != null : "Left operand can't be null";
        assert operatorType != null : "Operator type can't be null";
        assert rightOperand != null : "Right operand can't be null";
        assert targetProgramCounter >= 0 : "Program counter must be positive";

        this.leftOperand = leftOperand;
        this.operatorType = operatorType;
        this.rightOperand = rightOperand;
        this.targetProgramCounter = targetProgramCounter;
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
    public int getTargetProgramCounter() {
        return targetProgramCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BranchImpl branch = (BranchImpl) o;

        if (targetProgramCounter != branch.targetProgramCounter) return false;
        if (!leftOperand.equals(branch.leftOperand)) return false;
        if (operatorType != branch.operatorType) return false;
        if (!rightOperand.equals(branch.rightOperand)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = leftOperand.hashCode();
        result = 31 * result + operatorType.hashCode();
        result = 31 * result + rightOperand.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BranchImpl{" +
                "leftOperand=" + leftOperand +
                ", operatorType=" + operatorType +
                ", rightOperand=" + rightOperand +
                '}';
    }
}
