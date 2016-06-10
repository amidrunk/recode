package io.recode.model;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public interface Signature {

    List<Type> getParameterTypes();

    Type getReturnType();

    boolean test(Method method);

}
