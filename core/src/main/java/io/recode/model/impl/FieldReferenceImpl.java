package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.FieldReference;

import java.lang.reflect.Type;
import java.util.Optional;

public final class FieldReferenceImpl extends AbstractElement implements FieldReference {

    private final Expression targetInstance;

    private final Type declaringType;

    private final Type fieldType;

    private final String fieldName;
    private boolean aStatic;

    public FieldReferenceImpl(Expression targetInstance, Type declaringType, Type fieldType, String fieldName) {
        this(targetInstance, declaringType, fieldType, fieldName, null);
    }

    public FieldReferenceImpl(Expression targetInstance, Type declaringType, Type fieldType, String fieldName, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert declaringType != null : "Declaring type can't be null";
        assert fieldType != null : "Field type can't be null";
        assert fieldName != null && !fieldName.isEmpty() : "Field name can't be null or empty";

        this.targetInstance = targetInstance;
        this.declaringType = declaringType;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
    }

    @Override
    public Optional<Expression> getTargetInstance() {
        return (targetInstance == null ? Optional.<Expression>empty() : Optional.of(targetInstance));
    }

    @Override
    public Type getDeclaringType() {
        return declaringType;
    }

    public Type getFieldType() {
        return fieldType;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Type getType() {
        return fieldType;
    }

    public boolean isStatic() {
        return (targetInstance == null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldReferenceImpl that = (FieldReferenceImpl) o;

        if (aStatic != that.aStatic) return false;
        if (!declaringType.equals(that.declaringType)) return false;
        if (!fieldName.equals(that.fieldName)) return false;
        if (!fieldType.equals(that.fieldType)) return false;
        if (targetInstance != null ? !targetInstance.equals(that.targetInstance) : that.targetInstance != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetInstance != null ? targetInstance.hashCode() : 0;
        result = 31 * result + declaringType.hashCode();
        result = 31 * result + fieldType.hashCode();
        result = 31 * result + fieldName.hashCode();
        result = 31 * result + (aStatic ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FieldReferenceImpl{" +
                "targetInstance=" + (targetInstance == null ? "<static>" : targetInstance)+
                ", declaringType=" + declaringType +
                ", fieldType=" + fieldType +
                ", fieldName='" + fieldName + '\'' +
                ", aStatic=" + aStatic +
                '}';
    }
}
