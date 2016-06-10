package io.recode.model;

import io.recode.classfile.ReferenceKind;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public interface Lambda extends Expression {

    ReferenceKind getReferenceKind();

    Optional<Expression> getSelf();

    Type getFunctionalInterface();

    String getFunctionalMethodName();

    Signature getInterfaceMethodSignature();

    Type getDeclaringClass();

    String getBackingMethodName();

    Signature getBackingMethodSignature();

    List<LocalVariableReference> getEnclosedVariables();

}
