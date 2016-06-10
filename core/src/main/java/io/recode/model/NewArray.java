package io.recode.model;

import java.lang.reflect.Type;
import java.util.List;

public interface NewArray extends Expression {

    Type getComponentType();

    Expression getLength();

    List<ArrayInitializer> getInitializers();

    default ElementType getElementType() {
        return ElementType.NEW_ARRAY;
    }

}
