package io.recode.model;

import java.lang.reflect.Type;
import java.util.List;

public interface MethodCall extends StatementAndExpression {

    Type getTargetType();

    String getMethodName();

    Signature getSignature();

    Expression getTargetInstance();

    List<Expression> getParameters();

    MethodCall withParameters(List<Expression> parameters);

    boolean isStatic();

    default ElementType getElementType() {
        return ElementType.METHOD_CALL;
    }

}
