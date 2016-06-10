package io.recode.classfile.impl;

import io.recode.classfile.MethodReference;
import io.recode.model.Signature;

import java.lang.reflect.Type;

public final class MethodReferenceImpl implements MethodReference {

    private final Type targetType;

    private final String name;

    private final Signature signature;

    public MethodReferenceImpl(Type targetType, String name, Signature signature) {
        assert targetType != null : "Target type can't be null";
        assert name != null && !name.isEmpty() : "Name can't be null or empty";
        assert signature != null : "Signature can't be null";

        this.targetType = targetType;
        this.name = name;
        this.signature = signature;
    }

    @Override
    public Type getTargetType() {
        return targetType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodReferenceImpl that = (MethodReferenceImpl) o;

        if (!name.equals(that.name)) return false;
        if (!signature.equals(that.signature)) return false;
        if (!targetType.equals(that.targetType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetType.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + signature.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MethodReferenceImpl{" +
                "targetType=" + targetType +
                ", name='" + name + '\'' +
                ", signature=" + signature +
                '}';
    }
}
