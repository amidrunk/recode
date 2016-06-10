package io.recode.model;

import java.lang.reflect.Type;

public interface VariableAssignment extends Statement {

    int getVariableIndex();

    Expression getValue();

    String getVariableName();

    Type getVariableType();

    VariableAssignment withValue(Expression value);

    default ElementType getElementType() {
        return ElementType.VARIABLE_ASSIGNMENT;
    }

}
