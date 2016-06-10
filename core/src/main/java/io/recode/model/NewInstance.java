package io.recode.model;

import java.util.List;

public interface NewInstance extends StatementAndExpression {

    Signature getConstructorSignature();

    List<Expression> getParameters();

    default ElementType getElementType() {
        return ElementType.NEW;
    }

}
