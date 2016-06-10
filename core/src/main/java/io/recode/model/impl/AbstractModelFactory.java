package io.recode.model.impl;

import io.recode.model.*;
import io.recode.classfile.ReferenceKind;
import io.recode.model.impl.*;
import io.recode.model.impl.LambdaImpl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public abstract class AbstractModelFactory implements ModelFactory {

    @Override
    public Constant constant(Object constant, Class type) {
        return new ConstantImpl(constant, type, createElementMetaData());
    }

    @Override
    public Statement returnValue(Expression value) {
        return new ReturnValueImpl(value, createElementMetaData());
    }

    @Override
    public StatementAndExpression newInstance(Type type) {
        return new InstanceAllocationImpl(type, createElementMetaData());
    }

    @Override
    public Expression get(Expression array, Expression index, Type elementType) {
        return new ArrayLoadImpl(array, index, elementType, createElementMetaData());
    }

    @Override
    public Statement set(Expression array, Expression index, Expression value) {
        return new ArrayStoreImpl(array, index, value, createElementMetaData());
    }

    @Override
    public Expression binary(Expression leftOperand, OperatorType operatorType, Expression rightOperand, Type resultType) {
        return new BinaryOperatorImpl(leftOperand, operatorType, rightOperand, resultType, createElementMetaData());
    }

    @Override
    public Statement branch(Expression leftOperand, OperatorType operatorType, Expression rightOperand, int targetProgramCounter) {
        return new BranchImpl(leftOperand, operatorType, rightOperand, targetProgramCounter, createElementMetaData());
    }

    @Override
    public Expression cast(Expression value, Type type) {
        return new TypeCastImpl(value, type, createElementMetaData());
    }

    @Override
    public Expression compare(Expression leftOperand, Expression rightOperand) {
        return new CompareImpl(leftOperand, rightOperand, createElementMetaData());
    }

    @Override
    public Statement assignField(FieldReference fieldReference, Expression value) {
        return new FieldAssignmentImpl(fieldReference, value, createElementMetaData());
    }

    @Override
    public Expression field(Expression targetInstance, Type declaringType, Type fieldType, String fieldName) {
        return new FieldReferenceImpl(targetInstance, declaringType, fieldType, fieldName, createElementMetaData());
    }

    @Override
    public Statement jump(int targetProgramCounter) {
        return new GotoImpl(targetProgramCounter, createElementMetaData());
    }

    @Override
    public StatementAndExpression increment(LocalVariableReference localVariableReference, Expression value, Type resultType, Affix affix) {
        return new IncrementImpl(localVariableReference, value, resultType, affix, createElementMetaData());
    }

    @Override
    public Lambda createLambda(Optional<Expression> self, ReferenceKind referenceKind, Type functionalInterface, String functionalMethodName, Signature interfaceMethodSignature, Type declaringClass, String backingMethodName, Signature backingMethodSignature, List<LocalVariableReference> enclosedVariables) {
        return new LambdaImpl(self, referenceKind, functionalInterface, functionalMethodName, interfaceMethodSignature, declaringClass, backingMethodName, backingMethodSignature, enclosedVariables, createElementMetaData());
    }

    @Override
    public Expression local(String variableName, Type variableType, int index) {
        return new LocalVariableReferenceImpl(variableName, variableType, index, createElementMetaData());
    }

    @Override
    public Expression call(Type targetType, String methodName, Signature signature, Expression targetInstance, Expression[] parameters, Type resultType) {
        return new MethodCallImpl(targetType, methodName, signature, targetInstance, parameters, resultType, createElementMetaData());
    }

    @Override
    public Expression newArray(Type arrayType, Type componentType, Expression length, List<ArrayInitializer> initializers) {
        return new NewArrayImpl(arrayType, componentType, length, initializers, createElementMetaData());
    }

    @Override
    public Expression newInstance(Type type, Signature constructorSignature, List<Expression> parameters) {
        return new NewInstanceImpl(type, constructorSignature, parameters, createElementMetaData());
    }

    @Override
    public Statement doReturn() {
        return new ReturnImpl(createElementMetaData());
    }

    @Override
    public Expression unary(Expression operand, OperatorType operatorType, Type type) {
        return new UnaryOperatorImpl(operand, operatorType, type, createElementMetaData());
    }

    @Override
    public Statement assignLocal(Expression value, int variableIndex, String variableName, Type variableType) {
        return new VariableAssignmentImpl(value, variableIndex, variableName, variableType, createElementMetaData());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Element> E createFrom(E element) {
        assert element != null : "Element can't be null";

        final MergedElementMetaData metaData = new MergedElementMetaData(element.getMetaData(), createElementMetaData());

        switch (element.getElementType()) {
            case CONSTANT: {
                final Constant constant = (Constant) element;

                return (E) new ConstantImpl(constant.getConstant(), constant.getType(), metaData);
            }
            case RETURN_VALUE: {
                final ReturnValue returnValue = (ReturnValue) element;

                return (E) new ReturnValueImpl(returnValue.getValue(), metaData);
            }
            case UNARY_OPERATOR: {
                final UnaryOperator unaryOperator = (UnaryOperator) element;

                return (E) new UnaryOperatorImpl(unaryOperator.getOperand(), unaryOperator.getOperatorType(), unaryOperator.getType(), metaData);
            }
            case BINARY_OPERATOR: {
                final BinaryOperator binaryOperator = (BinaryOperator) element;

                return (E) new BinaryOperatorImpl(binaryOperator.getLeftOperand(), binaryOperator.getOperatorType(),
                        binaryOperator.getRightOperand(), binaryOperator.getType(), metaData);
            }
            case RETURN: {
                return (E) new ReturnImpl(metaData);
            }
            case VARIABLE_REFERENCE: {
                final LocalVariableReference localVariableReference = (LocalVariableReference) element;

                return (E) new LocalVariableReferenceImpl(localVariableReference.getName(),
                        localVariableReference.getType(), localVariableReference.getIndex(), metaData);
            }
            case METHOD_CALL: {
                final MethodCall methodCall = (MethodCall) element;

                return (E) new MethodCallImpl(methodCall.getTargetType(), methodCall.getMethodName(),
                        methodCall.getSignature(), methodCall.getTargetInstance(),
                        methodCall.getParameters().stream().toArray(Expression[]::new), methodCall.getType(), metaData);
            }
            case FIELD_REFERENCE: {
                final FieldReference fieldReference = (FieldReference) element;

                return (E) new FieldReferenceImpl(
                        fieldReference.getTargetInstance().isPresent() ? fieldReference.getTargetInstance().get() : null,
                        fieldReference.getDeclaringType(), fieldReference.getFieldType(), fieldReference.getFieldName(), metaData);
            }
            case VARIABLE_ASSIGNMENT: {
                final VariableAssignment variableAssignment = (VariableAssignment) element;

                return (E) new VariableAssignmentImpl(variableAssignment.getValue(), variableAssignment.getVariableIndex(),
                        variableAssignment.getVariableName(), variableAssignment.getVariableType(), metaData);
            }
            case LAMBDA: {
                final Lambda lambda = (Lambda) element;

                return (E) new LambdaImpl(lambda.getSelf(), lambda.getReferenceKind(),
                        lambda.getFunctionalInterface(), lambda.getFunctionalMethodName(),
                        lambda.getInterfaceMethodSignature(), lambda.getDeclaringClass(),
                        lambda.getBackingMethodName(), lambda.getBackingMethodSignature(),
                        lambda.getEnclosedVariables(), metaData);
            }
            case BRANCH: {
                final Branch branch = (Branch) element;

                return (E) new BranchImpl(branch.getLeftOperand(), branch.getOperatorType(), branch.getRightOperand(), branch.getTargetProgramCounter(), metaData);
            }
            case NEW: {
                final NewInstance newInstance = (NewInstance) element;

                return (E) new NewInstanceImpl(newInstance.getType(), newInstance.getConstructorSignature(), newInstance.getParameters(), metaData);
            }
            case NEW_ARRAY: {
                final NewArray newArray = (NewArray) element;

                return (E) new NewArrayImpl(newArray.getType(), newArray.getComponentType(), newArray.getLength(), newArray.getInitializers(), metaData);
            }
            case ARRAY_STORE: {
                final ArrayStore arrayStore = (ArrayStore) element;

                return (E) new ArrayStoreImpl(arrayStore.getArray(), arrayStore.getIndex(), arrayStore.getValue(), metaData);
            }
            case FIELD_ASSIGNMENT: {
                final FieldAssignment fieldAssignment = (FieldAssignment) element;

                return (E) new FieldAssignmentImpl(fieldAssignment.getFieldReference(), fieldAssignment.getValue(), metaData);
            }
            case CAST: {
                final TypeCast typeCast = (TypeCast) element;

                return (E) new TypeCastImpl(typeCast.getValue(), typeCast.getType(), metaData);
            }
            case ARRAY_LOAD: {
                final ArrayLoad arrayLoad = (ArrayLoad) element;

                return (E) new ArrayLoadImpl(arrayLoad.getArray(), arrayLoad.getIndex(), arrayLoad.getType(), metaData);
            }
            case INCREMENT: {
                final Increment increment = (Increment) element;

                return (E) new IncrementImpl(increment.getLocalVariable(), increment.getValue(), increment.getType(), increment.getAffix(), metaData);
            }
            case ALLOCATE: {
                final InstanceAllocation instanceAllocation = (InstanceAllocation) element;

                return (E) new InstanceAllocationImpl(instanceAllocation.getType(), metaData);
            }
            case GOTO: {
                final Goto gotoElement = (Goto) element;

                return (E) new GotoImpl(gotoElement.getTargetProgramCounter(), metaData);
            }
            case COMPARE: {
                final Compare compare = (Compare) element;

                return (E) new CompareImpl(compare.getLeftOperand(), compare.getRightOperand(), metaData);
            }
        }

        return null;
    }

    protected abstract ElementMetaData createElementMetaData();

}
