package io.recode.model;

import io.recode.model.*;
import io.recode.model.impl.*;
import io.recode.annotations.DSL;

import java.lang.reflect.Type;
import java.util.Arrays;

@DSL
public final class AST {

    public static Constant constant(Class<?> clazz) {
        assert clazz != null : "Class can't be null";
        return new ConstantImpl(clazz, Class.class);
    }

    public static Constant constant(String constant) {
        assert constant != null : "String constant can't be null";
        return new ConstantImpl(constant, String.class);
    }

    public static Constant constant(boolean value) {
        return new ConstantImpl(value, boolean.class);
    }

    public static Constant constant(int value) {
        return new ConstantImpl(value, int.class);
    }

    public static Constant constant(long value) {
        return new ConstantImpl(value, long.class);
    }

    public static Constant constant(float value) {
        return new ConstantImpl(value, float.class);
    }

    public static Constant constant(double value) {
        return new ConstantImpl(value, double.class);
    }

    public static Return $return() {
        return new ReturnImpl();
    }

    public static NewInstance newInstance(Type type, Expression... arguments) {
        assert type != null : "Type can't be null";
        assert arguments != null : "Arguments can't be null";

        final Type[] parameterTypes = Arrays.stream(arguments).map(attribute -> {
            assert attribute != null : "No attribute can be null";
            return attribute.getType();
        }).toArray(Type[]::new);

        final MethodSignature signature = MethodSignature.create(parameterTypes, void.class);

        return new NewInstanceImpl(type, signature, Arrays.asList(arguments));
    }

    public static SetLocalContinuation set(LocalVariableReference local) {
        assert local != null : "Local can't be null";

        return value -> {
            assert value != null : "Value can't be null";
            return new VariableAssignmentImpl(value, local.getIndex(), local.getName(), local.getType());
        };
    }

    public interface SetLocalContinuation {
        VariableAssignment to(Expression value);
    }

    public static VariableAssignment set(int index, String variableName, Expression value) {
        assert value != null : "Value can't be null";

        return set(index, variableName, value.getType(), value);
    }

    public static VariableAssignment set(int index, String variableName, Type variableType, Expression value) {
        assert variableName != null && !variableName.isEmpty() : "Variable name can't be null or empty";
        assert value != null : "Value can't be null";
        assert variableType != null : "Variable type can't be null";

        return new VariableAssignmentImpl(value, index, variableName, variableType);
    }

    public static BinaryOperator eq(Expression leftOperand, Expression rightOperand) {
        assert leftOperand != null : "Left operand can't be null";
        assert rightOperand != null : "Right operand can't be null";

        return new BinaryOperatorImpl(leftOperand, OperatorType.EQ, rightOperand, boolean.class);
    }

    public static MethodCall call(Type targetType, String methodName, Type returnType, Expression ... parameters) {
        assert returnType != null : "Return type can't be null";

        return call(targetType, methodName, MethodSignature.create(typesOf(parameters), returnType), parameters);
    }

    public static MethodCall call(Type targetType, String methodName, Signature signature, Expression ... parameters) {
        assert targetType != null : "Target type can't be null";
        assert methodName != null : "Method name can't be null";
        assert signature != null : "Signature can't be null";

        for (Expression parameter : parameters) {
            assert parameter != null : "No parameter can be null";
        }

        return new MethodCallImpl(targetType, methodName, signature, null, parameters);
    }

    public static MethodCall call(Expression instance, String methodName, Type returnType, Expression ... parameters) {
        assert instance != null : "Instance can't be null";
        assert methodName != null && !methodName.isEmpty() : "Method name can't be null or empty";
        assert returnType != null : "Return type can't be null";
        assert parameters != null : "Parameters can't be null";

        final Type targetType = instance.getType();
        final Type[] parameterTypes = typesOf(parameters);

        final Signature signature = MethodSignature.create(parameterTypes, returnType);

        return new MethodCallImpl(targetType, methodName, signature, instance, parameters);
    }

    public static FieldReference field(Type declaringType, Type fieldType, String fieldName) {
        return new FieldReferenceImpl(null, declaringType, fieldType, fieldName);
    }

    public static FieldReference field(Expression instance, Type fieldType, String fieldName) {
        assert instance != null : "Instance can't be null";

        return new FieldReferenceImpl(instance, instance.getType(), fieldType, fieldName);
    }

    public static LocalVariableReference local(String variableName, Type variableType, int index) {
        return new LocalVariableReferenceImpl(variableName, variableType, index);
    }

    public static BinaryOperator add(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.PLUS, right, resultType);
    }

    public static BinaryOperator sub(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.MINUS, right, resultType);
    }

    public static BinaryOperator mul(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.MULTIPLY, right, resultType);
    }

    public static BinaryOperator div(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.DIVIDE, right, resultType);
    }

    public static BinaryOperator mod(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.MODULO, right, resultType);
    }

    public static BinaryOperator lshift(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.LSHIFT, right, resultType);
    }

    public static BinaryOperator rshift(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.RSHIFT, right, resultType);
    }

    public static BinaryOperator unsignedRightShift(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.UNSIGNED_RSHIFT, right, resultType);
    }

    public static BinaryOperator ne(Expression left, Expression right) {
        return binaryOperator(left, OperatorType.NE, right, boolean.class);
    }

    public static BinaryOperator ge(Expression left, Expression right) {
        return binaryOperator(left, OperatorType.GE, right, boolean.class);
    }

    public static BinaryOperator gt(Expression left, Expression right) {
        return binaryOperator(left, OperatorType.GT, right, boolean.class);
    }

    public static BinaryOperator le(Expression left, Expression right) {
        return binaryOperator(left, OperatorType.LE, right, boolean.class);
    }

    public static BinaryOperator lt(Expression left, Expression right) {
        return binaryOperator(left, OperatorType.LT, right, boolean.class);
    }

    public static BinaryOperator and(Expression left, Expression right) {
        return binaryOperator(left, OperatorType.AND, right, boolean.class);
    }

    public static BinaryOperator or(Expression left, Expression right) {
        return binaryOperator(left, OperatorType.OR, right, boolean.class);
    }

    public static BinaryOperator bitwiseAnd(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.BITWISE_AND, right, resultType);
    }

    public static BinaryOperator bitwiseOr(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.BITWISE_OR, right, resultType);
    }

    public static BinaryOperator xor(Expression left, Expression right, Type resultType) {
        return binaryOperator(left, OperatorType.XOR, right, resultType);
    }

    public static BinaryOperator binaryOperator(Expression left, OperatorType operatorType, Expression right, Type resultType) {
        assert left != null : "Left operand can't be null";
        assert operatorType != null : "Operator type can't be null";
        assert right != null : "Right operand can't be null";
        assert resultType != null : "Result type can't be null";

        return new BinaryOperatorImpl(left, operatorType, right, resultType);
    }

    public static ReturnValue $return(Expression value) {
        return new ReturnValueImpl(value);
    }

    public static BinaryOperator plus(Expression leftOperand, Expression rightOperand) {
        assert leftOperand != null : "Left operand can't be null";
        assert rightOperand != null : "Right operand can't be null";
        assert leftOperand.getType().equals(rightOperand.getType()) : "Operands must be of the same type";

        return new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, leftOperand.getType());
    }

    public static CastContinuation cast(Expression value) {
        assert value != null : "Value can't be null";

        return type -> new TypeCastImpl(value, type);
    }

    public static NewArray newArray(Class arrayType, Expression ... elements) {
        final ArrayInitializer[] initializers = new ArrayInitializer[elements.length];

        for (int i = 0; i < elements.length; i++) {
            initializers[i] = new ArrayInitializerImpl(i, elements[i]);
        }


        return new NewArrayImpl(arrayType, arrayType.getComponentType(), constant(elements.length), Arrays.asList(initializers));
    }

    public interface CastContinuation {

        TypeCast to(Type type);

    }

    private static Type[] typesOf(Expression[] parameters) {
        return Arrays.stream(parameters).map(e -> {
            assert e != null : "No parameter can be null";
            return e.getType();
        }).toArray(Type[]::new);
    }

}
