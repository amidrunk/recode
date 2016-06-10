package io.recode.model.impl;

import io.recode.classfile.ReferenceKind;
import io.recode.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AbstractModelFactoryTest {

    private final ElementMetaData metaData = mock(ElementMetaData.class);

    private final AbstractModelFactory modelFactory = new AbstractModelFactory() {
        @Override
        protected ElementMetaData createElementMetaData() {
            return metaData;
        }
    };

    @Test
    public void createConstantShouldCreateConstantWithParametersAndMetaData() {
        final Constant it = modelFactory.constant("foo", CharSequence.class);

        assertEquals("foo", it.getConstant());
        assertEquals(CharSequence.class, it.getType());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createReturnValueShouldCreateElementWithParametersAndMetaData() {
        final Expression expectedValue = mock(Expression.class);
        final ReturnValue it = modelFactory.returnValue(expectedValue).as(ReturnValue.class);

        assertEquals(expectedValue, it.getValue());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createInstanceAllocationShouldCreateElementWithParametersAndMetaData() {
        final StatementAndExpression it = modelFactory.newInstance(String.class);

        assertEquals(String.class, it.getType());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createArrayLoadShouldCreateElementWithParametersAndMetaData() {
        final Expression array = mock(Expression.class, "array");
        final Expression index = mock(Expression.class, "index");

        final ArrayLoad it = modelFactory.get(array, index, String.class).as(ArrayLoad.class);

        assertEquals(array, it.getArray());
        assertEquals(index, it.getIndex());
        assertEquals(String.class, it.getType());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createArrayStoreShouldCreateElementWithParametersAndMetaData() {
        final Expression array = mock(Expression.class, "array");
        final Expression index = mock(Expression.class, "index");
        final Expression value = mock(Expression.class, "value");
        final ArrayStore it = modelFactory.set(array, index, value).as(ArrayStore.class);

        assertEquals(array, it.getArray());
        assertEquals(index, it.getIndex());
        assertEquals(value, it.getValue());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createBinaryOperatorShouldCreateElementWithParametersAndMetaData() {
        final Expression leftOperand = mock(Expression.class, "leftOperand");
        final Expression rightOperand = mock(Expression.class, "rightOperand");
        final BinaryOperator it = modelFactory.binary(leftOperand, OperatorType.AND, rightOperand, int.class).as(BinaryOperator.class);

        assertEquals(leftOperand, it.getLeftOperand());
        assertEquals(OperatorType.AND, it.getOperatorType());
        assertEquals(rightOperand, it.getRightOperand());
        assertEquals(int.class, it.getType());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createBranchShouldCreateElementWithMetaData() {
        final Expression leftOperand = mock(Expression.class, "leftOperand");
        final Expression rightOperand = mock(Expression.class, "rightOperand");
        final Branch it = modelFactory.branch(leftOperand, OperatorType.EQ, rightOperand, 1234).as(Branch.class);

        assertEquals(leftOperand, it.getLeftOperand());
        assertEquals(OperatorType.EQ, it.getOperatorType());
        assertEquals(rightOperand, it.getRightOperand());
        assertEquals(1234, it.getTargetProgramCounter());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createTypeCastShouldCreateElementWithMetaData() {
        final Expression value = mock(Expression.class, "value");
        final TypeCast it = modelFactory.cast(value, String.class).as(TypeCast.class);

        assertEquals(value, it.getValue());
        assertEquals(String.class, it.getType());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createCompareShouldCreateElementWithMetaData() {
        final Expression leftOperand = mock(Expression.class, "leftOperand");
        final Expression rightOperand = mock(Expression.class, "rightOperand");

        final Compare it = modelFactory.compare(leftOperand, rightOperand).as(Compare.class);

        assertEquals(leftOperand, it.getLeftOperand());
        assertEquals(rightOperand, it.getRightOperand());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createFieldAssignmentShouldCreateElementWithMetaData() {
        final FieldReference fieldReference = mock(FieldReference.class, "fieldReference");
        final Expression value = mock(Expression.class, "value");

        final FieldAssignment it = modelFactory.assignField(fieldReference, value).as(FieldAssignment.class);

        assertEquals(fieldReference, it.getFieldReference());
        assertEquals(value, it.getValue());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createFieldReferenceShouldCreateElementWithMetaData() {
        final Expression targetInstance = mock(Expression.class, "targetInstance");

        final FieldReference it = modelFactory.field(targetInstance, String.class, int.class, "foo").as(FieldReference.class);

        assertEquals(targetInstance, it.getTargetInstance().get());
        assertEquals(String.class, it.getDeclaringType());
        assertEquals(int.class, it.getFieldType());
        assertEquals("foo", it.getFieldName());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createGotoShouldCreateElementWithMetaData() {
        final Goto it = modelFactory.jump(1234).as(Goto.class);

        assertEquals(1234, it.getTargetProgramCounter());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createIncrementShouldCreateElementWithMetaData() {
        final LocalVariableReference localVariableReference = mock(LocalVariableReference.class, "localVariableReference");
        final Expression value = mock(Expression.class, "value");

        final Increment it = modelFactory.increment(localVariableReference, value, int.class, Affix.POSTFIX).as(Increment.class);

        assertEquals(localVariableReference, it.getLocalVariable());
        assertEquals(value, it.getValue());
        assertEquals(int.class, it.getType());
        assertEquals(Affix.POSTFIX, it.getAffix());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createLambdaShouldCreateLambdaWithMetaData() {
        final Expression self = mock(Expression.class, "self");
        final ReferenceKind referenceKind = ReferenceKind.INVOKE_VIRTUAL;
        final Class<Runnable> functionalInterface = Runnable.class;
        final String functionalMethodName = "run";
        final MethodSignature interfaceMethodSignature = MethodSignature.parse("()V");
        final String backingMethodName = "some$lambda";
        final MethodSignature backingMethodSignature = MethodSignature.parse("()V");
        final List enclosedVariables = Collections.emptyList();

        final Lambda it = modelFactory.createLambda(Optional.of(self), referenceKind, functionalInterface, functionalMethodName, interfaceMethodSignature, String.class, backingMethodName, backingMethodSignature, enclosedVariables);

        assertEquals(Optional.of(self), it.getSelf());
        assertEquals(referenceKind, it.getReferenceKind());
        assertEquals(functionalInterface, it.getFunctionalInterface());
        assertEquals(functionalMethodName, it.getFunctionalMethodName());
        assertEquals(interfaceMethodSignature, it.getInterfaceMethodSignature());
        assertEquals(String.class, it.getDeclaringClass());
        assertEquals(backingMethodName, it.getBackingMethodName());
        assertEquals(backingMethodSignature, it.getBackingMethodSignature());
        assertEquals(enclosedVariables, it.getEnclosedVariables());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createLocalVariableReferenceShouldCreateElementWithMetaData() {
        final LocalVariableReference it = modelFactory.local("foo", String.class, 1234).as(LocalVariableReference.class);

        assertEquals("foo", it.getName());
        assertEquals(String.class, it.getType());
        assertEquals(1234, it.getIndex());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createMethodCallShouldCreateElementWithMetaData() {
        final MethodSignature methodSignature = MethodSignature.parse("()V");
        final Expression targetInstance = mock(Expression.class, "targetInstance");
        final Expression param1 = mock(Expression.class, "param1");

        final MethodCall it = modelFactory.call(String.class, "foo", methodSignature, targetInstance, new Expression[]{param1}, int.class).as(MethodCall.class);

        assertEquals(String.class, it.getTargetType());
        assertEquals("foo", it.getMethodName());
        assertEquals(methodSignature, it.getSignature());
        assertEquals(targetInstance, it.getTargetInstance());
        assertEquals(Arrays.asList(param1), it.getParameters());
        assertEquals(int.class, it.getType());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createNewArrayShouldCreateElementWithMetaData() {
        final Expression length = mock(Expression.class, "length");

        final NewArray it = modelFactory.newArray(String[].class, String.class, length, Collections.emptyList()).as(NewArray.class);

        assertEquals(String[].class, it.getType());
        assertEquals(String.class, it.getComponentType());
        assertEquals(length, it.getLength());
        assertEquals(Collections.emptyList(), it.getInitializers());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createNewInstanceShouldCreateElementWithMetaData() {
        final MethodSignature signature = MethodSignature.parse("()Ljava/lang/String;");
        final Expression param1 = mock(Expression.class, "param1");

        final NewInstance it = modelFactory.newInstance(String.class, signature, Arrays.asList(param1)).as(NewInstance.class);

        assertEquals(String.class, it.getType());
        assertEquals(signature, it.getConstructorSignature());
        assertEquals(Arrays.asList(param1), it.getParameters());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createReturnShouldCreateElementWithMetaData() {
        final Return it = modelFactory.doReturn().as(Return.class);

        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createUnaryOperatorShouldCreateElementWithMetaData() {
        final Expression operand = mock(Expression.class, "operand");
        final UnaryOperator it = modelFactory.unary(operand, OperatorType.NOT, int.class).as(UnaryOperator.class);

        assertEquals(operand, it.getOperand());
        assertEquals(OperatorType.NOT, it.getOperatorType());
        assertEquals(int.class, it.getType());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createVariableAssignmentShouldCreateElementWithMetaData() {
        final Expression value = mock(Expression.class, "value");

        final VariableAssignment it = modelFactory.assignLocal(value, 1234, "foo", String.class).as(VariableAssignment.class);

        assertEquals(value, it.getValue());
        assertEquals(1234, it.getVariableIndex());
        assertEquals("foo", it.getVariableName());
        assertEquals(String.class, it.getVariableType());
        assertEquals(metaData, it.getMetaData());
    }

    @Test
    public void createFromShouldNotAcceptNullOriginal() {
        assertThrown(() -> modelFactory.createFrom(null), AssertionError.class);
    }

    @Test
    public void constantCanBeRecreated() {
        assertCreateFromValid(new ConstantImpl("foo", String.class));
    }

    @Test
    public void returnValueCanBeRecreated() {
        assertCreateFromValid(new ReturnValueImpl(mock(Expression.class)));
    }

    @Test
    public void unaryOperatorCanBeRecreated() {
        assertCreateFromValid(new UnaryOperatorImpl(mock(Expression.class), OperatorType.EQ, String.class));
    }

    @Test
    public void binaryOperatorCanBeRecreated() {
        assertCreateFromValid(new BinaryOperatorImpl(mock(Expression.class), OperatorType.EQ, mock(Expression.class), String.class));
    }

    @Test
    public void returnCanBeRecreated() {
        assertCreateFromValid(new ReturnImpl());
    }

    @Test
    public void variableReferenceCanBeRecreated() {
        assertCreateFromValid(new LocalVariableReferenceImpl("foo", String.class, 1234));
    }

    @Test
    public void methodCallCanBeRecreated() {
        assertCreateFromValid(new MethodCallImpl(String.class, "foo", mock(Signature.class), mock(Expression.class), new Expression[]{mock(Expression.class)}, int.class));
    }

    @Test
    public void fieldReferenceCanBeRecreated() {
        assertCreateFromValid(new FieldReferenceImpl(mock(Expression.class), String.class, int.class, "foo"));
    }

    @Test
    public void variableAssignmentCanBeRecreated() {
        assertCreateFromValid(new VariableAssignmentImpl(mock(Expression.class), 1234, "foo", String.class));
    }

    @Test
    public void lambdaCanBeRecreated() {
        assertCreateFromValid(new LambdaImpl(Optional.of(mock(Expression.class)), ReferenceKind.INVOKE_VIRTUAL,
                Runnable.class, "run", mock(Signature.class), String.class, "run$lambda", mock(Signature.class), Arrays.asList(mock(LocalVariableReference.class))));
    }

    @Test
    public void branchCanBeRecreated() {
        assertCreateFromValid(new BranchImpl(mock(Expression.class), OperatorType.EQ, mock(Expression.class), 1234));
    }

    @Test
    public void newCanBeRecreated() {
        assertCreateFromValid(new NewInstanceImpl(String.class, mock(Signature.class), Arrays.asList(mock(Expression.class))));
    }

    @Test
    public void newArrayCanBeRecreated() {
        assertCreateFromValid(new NewArrayImpl(String[].class, String.class, mock(Expression.class), Arrays.asList(mock(ArrayInitializer.class))));
    }

    @Test
    public void arrayStoreCanBeRecreated() {
        assertCreateFromValid(new ArrayStoreImpl(mock(Expression.class), mock(Expression.class), mock(Expression.class)));
    }

    @Test
    public void fieldAssignmentCanBeRecreated() {
        assertCreateFromValid(new FieldAssignmentImpl(mock(FieldReference.class), mock(Expression.class)));
    }

    @Test
    public void typeCastCanBeRecreated() {
        assertCreateFromValid(new TypeCastImpl(mock(Expression.class), String.class));
    }

    @Test
    public void arrayLoadCanBeRecreated() {
        assertCreateFromValid(new ArrayLoadImpl(mock(Expression.class), mock(Expression.class), String.class));
    }

    @Test
    public void incrementCanBeRecreated() {
        assertCreateFromValid(new IncrementImpl(mock(LocalVariableReference.class), mock(Expression.class), String.class, Affix.POSTFIX));
    }

    @Test
    public void allocationCanBeRecreated() {
        assertCreateFromValid(new InstanceAllocationImpl(String.class));
    }

    @Test
    public void gotoCanBeRecreated() {
        assertCreateFromValid(new GotoImpl(1234));
    }

    @Test
    public void compareCanBeRecreated() {
        assertCreateFromValid(new CompareImpl(mock(Expression.class), mock(Expression.class)));
    }

    private void assertCreateFromValid(Element element) {
        final Element copy = modelFactory.createFrom(element);

        assertEquals(element, copy);

        final MergedElementMetaData mergedElementMetaData = (MergedElementMetaData) copy.getMetaData();

        assertEquals(element.getMetaData(), mergedElementMetaData.getFirstCandidate());
        assertEquals(metaData, mergedElementMetaData.getSecondCandidate());
    }
}