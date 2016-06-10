package io.recode.model;

import java.lang.reflect.Type;
import java.util.Optional;

public interface FieldReference extends Expression {

    Optional<Expression> getTargetInstance();

    Type getDeclaringType();

    Type getFieldType();

    String getFieldName();

    default ElementType getElementType() {
        return ElementType.FIELD_REFERENCE;
    }

}
