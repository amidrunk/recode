package io.recode.classfile;

import io.recode.model.Signature;

import java.lang.reflect.Type;

public interface MethodReference {

    Type getTargetType();

    String getName();

    Signature getSignature();

}
