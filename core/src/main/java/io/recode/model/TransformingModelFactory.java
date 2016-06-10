package io.recode.model;

import io.recode.classfile.ReferenceKind;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class TransformingModelFactory implements ModelFactory {

    private final ModelFactory targetModelFactory;

    private final Function<? extends Element, ? extends Element> transformer;

    public TransformingModelFactory(ModelFactory targetModelFactory, Function<? extends Element, ? extends Element> transformer) {
        this.targetModelFactory = targetModelFactory;
        this.transformer = transformer;
    }

    @Override
    public Expression constant(Object constant, Class type) {
        return transform(targetModelFactory.constant(constant, type));
    }

    @Override
    public Statement returnValue(Expression value) {
        return transform(targetModelFactory.returnValue(value));
    }

    @Override
    public StatementAndExpression newInstance(Type type) {
        return transform(targetModelFactory.newInstance(type));
    }

    @Override
    public Expression get(Expression array, Expression index, Type elementType) {
        return transform(targetModelFactory.get(array, index, elementType));
    }

    @Override
    public Statement set(Expression array, Expression index, Expression value) {
        return transform(targetModelFactory.set(array, index, value));
    }

    @Override
    public Expression binary(Expression leftOperand, OperatorType operatorType, Expression rightOperand, Type resultType) {
        return transform(targetModelFactory.binary(leftOperand, operatorType, rightOperand, resultType));
    }

    @Override
    public Statement branch(Expression leftOperand, OperatorType operatorType, Expression rightOperand, int targetProgramCounter) {
        return transform(targetModelFactory.branch(leftOperand, operatorType, rightOperand, targetProgramCounter));
    }

    @Override
    public Expression cast(Expression value, Type type) {
        return transform(targetModelFactory.cast(value, type));
    }

    @Override
    public Expression compare(Expression leftOperand, Expression rightOperand) {
        return transform(targetModelFactory.compare(leftOperand, rightOperand));
    }

    @Override
    public Statement assignField(FieldReference fieldReference, Expression value) {
        return transform(targetModelFactory.assignField(fieldReference, value));
    }

    @Override
    public Expression field(Expression targetInstance, Type declaringType, Type fieldType, String fieldName) {
        return transform(targetModelFactory.field(targetInstance, declaringType, fieldType, fieldName));
    }

    @Override
    public Statement jump(int targetProgramCounter) {
        return transform(targetModelFactory.jump(targetProgramCounter));
    }

    @Override
    public StatementAndExpression increment(LocalVariableReference localVariableReference, Expression value, Type resultType, Affix affix) {
        return transform(targetModelFactory.increment(localVariableReference, value, resultType, affix));
    }

    @Override
    public <E extends Element> E createFrom(E element) {
        return transform(targetModelFactory.createFrom(element));
    }

    @Override
    public Lambda createLambda(Optional<Expression> self, ReferenceKind referenceKind, Type functionalInterface, String functionalMethodName, Signature interfaceMethodSignature, Type declaringClass, String backingMethodName, Signature backingMethodSignature, List<LocalVariableReference> enclosedVariables) {
        return transform(targetModelFactory.createLambda(self, referenceKind, functionalInterface, functionalMethodName, interfaceMethodSignature, declaringClass, backingMethodName, backingMethodSignature, enclosedVariables));
    }

    @Override
    public Expression local(String variableName, Type variableType, int index) {
        return transform(targetModelFactory.local(variableName, variableType, index));
    }

    @Override
    public Expression call(Type targetType, String methodName, Signature signature, Expression targetInstance, Expression[] parameters, Type resultType) {
        return transform(targetModelFactory.call(targetType, methodName, signature, targetInstance, parameters, resultType));
    }

    @Override
    public Expression newArray(Type arrayType, Type componentType, Expression length, List<ArrayInitializer> initializers) {
        return transform(targetModelFactory.newArray(arrayType, componentType, length, initializers));
    }

    @Override
    public Expression newInstance(Type type, Signature constructorSignature, List<Expression> parameters) {
        return transform(targetModelFactory.newInstance(type, constructorSignature, parameters));
    }

    @Override
    public Statement doReturn() {
        return transform(targetModelFactory.doReturn());
    }

    @Override
    public Expression unary(Expression operand, OperatorType operatorType, Type type) {
        return transform(targetModelFactory.unary(operand, operatorType, type));
    }

    @Override
    public Statement assignLocal(Expression value, int variableIndex, String variableName, Type variableType) {
        return transform(targetModelFactory.assignLocal(value, variableIndex, variableName, variableType));
    }

    @SuppressWarnings("unchecked")
    private <E extends Element> E transform(E e) {
        return (E) ((Function) transformer).apply(e);
    }
}
