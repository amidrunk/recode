package io.recode.model;

import io.recode.classfile.ReferenceKind;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public interface ModelFactory {

    Expression constant(Object constant, Class type);

    Statement returnValue(Expression value);

    StatementAndExpression newInstance(Type type);

    Expression get(Expression array, Expression index, Type elementType);

    Statement set(Expression array, Expression index, Expression value);

    Expression binary(Expression leftOperand, OperatorType operatorType, Expression rightOperand, Type resultType);

    Statement branch(Expression leftOperand, OperatorType operatorType, Expression rightOperand, int targetProgramCounter);

    Expression cast(Expression value, Type type);

    Expression compare(Expression leftOperand, Expression rightOperand);

    Statement assignField(FieldReference fieldReference, Expression value);

    Expression field(Expression targetInstance, Type declaringType, Type fieldType, String fieldName);

    Statement jump(int targetProgramCounter);

    StatementAndExpression increment(LocalVariableReference localVariableReference, Expression value, Type resultType, Affix affix);

    <E extends Element> E createFrom(E element);

    Lambda createLambda(Optional<Expression> self,
                        ReferenceKind referenceKind,
                        Type functionalInterface,
                        String functionalMethodName,
                        Signature interfaceMethodSignature,
                        Type declaringClass,
                        String backingMethodName,
                        Signature backingMethodSignature,
                        List<LocalVariableReference> enclosedVariables);


    Expression local(String variableName, Type variableType, int index);

    Expression call(Type targetType, String methodName, Signature signature, Expression targetInstance, Expression[] parameters, Type resultType);

    Expression newArray(Type arrayType, Type componentType, Expression length, List<ArrayInitializer> initializers);

    Expression newInstance(Type type, Signature constructorSignature, List<Expression> parameters);

    Statement doReturn();

    Expression unary(Expression operand, OperatorType operatorType, Type type);

    Statement assignLocal(Expression value, int variableIndex, String variableName, Type variableType);

}
