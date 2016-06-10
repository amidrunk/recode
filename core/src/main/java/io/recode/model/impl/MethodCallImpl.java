package io.recode.model.impl;

import io.recode.model.*;
import io.recode.util.Types;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class MethodCallImpl extends AbstractElement implements MethodCall {

    private final Type targetType;

    private final String methodName;

    private final Signature signature;

    private final Expression targetInstance;

    private final Expression[] parameters;

    private final Type expressionType;

    public MethodCallImpl(Type targetType, String methodName, Signature signature, Expression targetInstance, Expression[] parameters) {
        this(targetType, methodName, signature, targetInstance, parameters, signature == null ? null : signature.getReturnType());
    }

    public MethodCallImpl(Type targetType, String methodName, Signature signature, Expression targetInstance, Expression[] parameters, Type expressionType) {
        this(targetType, methodName, signature, targetInstance, parameters, expressionType, null);
    }

    public MethodCallImpl(Type targetType, String methodName, Signature signature, Expression targetInstance, Expression[] parameters, Type expressionType, ElementMetaData elementMetaData) {
        super(elementMetaData);

        assert targetType != null : "Target type can't be null";
        assert methodName != null && !methodName.isEmpty() : "Method name can't be null or empty";
        assert signature != null : "Signature can't be null";
        assert parameters != null : "Parameters can't be null";
        assert expressionType != null : "Expression type can't be null";

        this.targetType = targetType;
        this.methodName = methodName;
        this.signature = signature;
        this.targetInstance = targetInstance;
        this.parameters = parameters;
        this.expressionType = expressionType;
    }

    @Override
    public Type getTargetType() {
        return targetType;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public Expression getTargetInstance() {
        return targetInstance;
    }

    @Override
    public List<Expression> getParameters() {
        return Arrays.asList(parameters);
    }

    @Override
    public MethodCall withParameters(List<Expression> parameters) {
        assert parameters != null : "Parameter can't be null";

        final List<Type> parameterTypes = getSignature().getParameterTypes();

        if (parameters.size() != parameterTypes.size()) {
            throw new IncompatibleTypeException("Method call requires " + parameterTypes.size() + " parameters, got " + parameters);
        }

        final Iterator<Type> typeIterator = parameterTypes.iterator();
        final Iterator<Expression> valueIterator = parameters.iterator();

        int index = 0;

        while (typeIterator.hasNext()) {
            final Type type = typeIterator.next();
            final Expression value = valueIterator.next();

            if (!Types.isValueTypePotentiallyAssignableTo(value.getType(), type)) {
                if (!type.getTypeName().equals(value.getType().getTypeName())) {
                    throw new IncompatibleTypeException("Invalid parameter value at index " + index
                            + "; expected " + type.getTypeName() + ", was " + value);
                }
            }

            index++;
        }

        return new MethodCallImpl(targetType, methodName, signature, targetInstance, parameters.toArray(new Expression[parameters.size()]), expressionType);
    }

    @Override
    public boolean isStatic() {
        return (targetInstance == null);
    }

    @Override
    public Type getType() {
        return expressionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodCallImpl that = (MethodCallImpl) o;

        if (!expressionType.equals(that.expressionType)) return false;
        if (!methodName.equals(that.methodName)) return false;
        if (!Arrays.equals(parameters, that.parameters)) return false;
        if (!signature.equals(that.signature)) return false;
        if (targetInstance != null ? !targetInstance.equals(that.targetInstance) : that.targetInstance != null)
            return false;
        if (!targetType.equals(that.targetType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetType.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + signature.hashCode();
        result = 31 * result + (targetInstance != null ? targetInstance.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(parameters);
        result = 31 * result + expressionType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MethodCallImpl{" +
                "targetType=" + targetType +
                ", methodName='" + methodName + '\'' +
                ", signature=" + signature +
                ", targetInstance=" + targetInstance +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
