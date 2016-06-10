package io.recode.model;

import io.recode.model.impl.*;
import org.junit.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;

import static io.recode.model.AST.*;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ASTTest {

    private final Expression operand1 = mock(Expression.class, "operand1");

    private final Expression operand2 = mock(Expression.class, "operand2");

    @Test
    public void constantStringCannotBeNull() {
        assertThrown(() -> constant((String) null), AssertionError.class);
    }

    @Test
    public void stringConstantCanBeCreated() {
        assertEquals(new ConstantImpl("foo", String.class), constant("foo"));
    }

    @Test
    public void intConstantCanBeCreated() {
        assertEquals(new ConstantImpl(1, int.class), constant(1));
    }

    @Test
    public void newInstanceShouldNotAcceptInvalidArguments() {
        assertThrown(() -> AST.newInstance(null), AssertionError.class);
        assertThrown(() -> AST.newInstance(String.class, mock(Expression.class), null), AssertionError.class);
    }

    @Test
    public void newInstanceShouldReturnNewInstanceExpressionWithResolvedConstructor() {
        final NewInstance it = AST.newInstance(String.class, AST.constant("Hello World!"));

        assertEquals(new NewInstanceImpl(String.class, MethodSignature.parse("(Ljava/lang/String;)V"),
                Arrays.asList(new ConstantImpl("Hello World!", String.class))), it);
    }

    @Test
    public void setShouldNotAcceptInvalidParameters() {
        assertThrown(() -> AST.set(-1, "foo", constant("foo")), AssertionError.class);
        assertThrown(() -> AST.set(0, null, constant("foo")), AssertionError.class);
        assertThrown(() -> AST.set(0, "", constant("foo")), AssertionError.class);
        assertThrown(() -> AST.set(0, "foo", null), AssertionError.class);
    }

    @Test
    public void setShouldCreateNewVariableAssignment() {
        final VariableAssignment va = AST.set(0, "str", constant("foo"));

        assertEquals("str", va.getVariableName());
        assertEquals(constant("foo"), va.getValue());
        assertEquals(String.class, va.getVariableType());
    }

    @Test
    public void setShouldNotAcceptNullVariableType() {
        assertThrown(() -> set(0, "foo", null, constant(1234)), AssertionError.class);
    }

    @Test
    public void variableAssignmentCanBeCreatedWithExplicitVariableType() {
        final VariableAssignment expr = set(0, "str", boolean.class, constant(1234));

        assertEquals("str", expr.getVariableName());
        assertEquals(boolean.class, expr.getVariableType());
        assertEquals(constant(1234), expr.getValue());
    }

    @Test
    public void longConstantCanBeCreated() {
        final Constant constant = constant(100L);

        assertEquals(long.class, constant.getType());
        assertEquals(100L, constant.getConstant());
    }

    @Test
    public void floatConstantCanBeCreated() {
        final Constant constant = constant(1234f);

        assertEquals(float.class, constant.getType());
        assertEquals(1234f, constant.getConstant());
    }

    @Test
    public void doubleConstantCanBeCreated() {
        final Constant constant = constant(1234d);

        assertEquals(double.class, constant.getType());
        assertEquals(1234d, constant.getConstant());
    }

    @Test
    public void returnStatementCanBeCreated() {
        final Return ret = $return();

        assertEquals(ElementType.RETURN, ret.getElementType());
    }

    @Test
    public void eqShouldNotAcceptInvalidParameters() {
        assertThrown(() -> AST.eq(null, constant("foo")), AssertionError.class);
        assertThrown(() -> AST.eq(constant("foo"), null), AssertionError.class);
    }

    @Test
    public void eqShouldCreateBinaryExpression() {
        final BinaryOperator e = AST.eq(constant("foo"), constant("bar"));

        assertEquals(constant("foo"), e.getLeftOperand());
        assertEquals(constant("bar"), e.getRightOperand());
        assertEquals(ElementType.BINARY_OPERATOR, e.getElementType());
        assertEquals(OperatorType.EQ, e.getOperatorType());
        assertEquals(boolean.class, e.getType());
    }

    @Test
    public void callShouldNotAcceptInvalidParameters() {
        assertThrown(() -> call((Expression) null, "foo", String.class, constant(1)), AssertionError.class);
        assertThrown(() -> call(constant("str"), null, String.class, constant(1)), AssertionError.class);
        assertThrown(() -> call(constant("str"), "", String.class, constant(1)), AssertionError.class);
        assertThrown(() -> call(constant("str"), "foo", null, constant(1)), AssertionError.class);
        assertThrown(() -> call(constant("str"), "foo", String.class, (Expression[]) null), AssertionError.class);
        assertThrown(() -> call(constant("str"), "foo", String.class, constant(1), null), AssertionError.class);
    }

    @Test
    public void callShouldCreateNewMethodCall() {
        final MethodCall e = call(constant("str"), "substring", String.class, constant(0), constant(1));

        assertEquals(constant("str"), e.getTargetInstance());
        assertEquals("substring", e.getMethodName());
        assertEquals(String.class, e.getTargetType());
        assertEquals("(II)Ljava/lang/String;", e.getSignature().toString());
        assertArrayEquals(new Object[]{constant(0), constant(1)}, e.getParameters().toArray());
    }

    @Test
    public void getShouldNotAcceptInvalidParameters() {
        assertThrown(() -> field((Type) null, String.class, "foo"), AssertionError.class);
        assertThrown(() -> field(String.class, null, "foo"), AssertionError.class);
        assertThrown(() -> field(String.class, String.class, null), AssertionError.class);
        assertThrown(() -> field(String.class, String.class, ""), AssertionError.class);
    }

    @Test
    public void getShouldCreateNewFieldReference() {
        final FieldReference field = field(String.class, BigDecimal.class, "foo");

        assertFalse(field.getTargetInstance().isPresent());
        assertEquals(String.class, field.getDeclaringType());
        assertEquals(BigDecimal.class, field.getType());
        assertEquals("foo", field.getFieldName());
    }

    @Test
    public void localShouldNotAcceptInvalidArguments() {
        assertThrown(() -> local(null, String.class, 0), AssertionError.class);
        assertThrown(() -> local("", String.class, 0), AssertionError.class);
        assertThrown(() -> local("foo", null, 0), AssertionError.class);
        assertThrown(() -> local("foo", String.class, -1), AssertionError.class);
    }

    @Test
    public void localShouldCreateNotLocalVariableReference() {
        final LocalVariableReference local = local("str", String.class, 1);

        assertEquals("str", local.getName());
        assertEquals(String.class, local.getType());
        assertEquals(1, local.getIndex());
    }

    @Test
    public void getInstanceFieldShouldNotAcceptInvalidArguments() {
        assertThrown(() -> AST.field((Expression) null, String.class, "foo"), AssertionError.class);
        assertThrown(() -> AST.field(constant("str"), null, "foo"), AssertionError.class);
        assertThrown(() -> AST.field(constant("str"), String.class, null), AssertionError.class);
        assertThrown(() -> AST.field(constant("str"), String.class, ""), AssertionError.class);
    }

    @Test
    public void getInstanceFieldShouldCreateFieldReference() {
        final FieldReference str = AST.field(constant("str"), int.class, "length");

        assertTrue(str.getTargetInstance().isPresent());
        assertEquals(AST.constant("str"), str.getTargetInstance().get());
        assertEquals(String.class, str.getDeclaringType());
        assertEquals("length", str.getFieldName());
        assertEquals(int.class, str.getFieldType());
    }

    @Test
    public void returnValueShouldNotAcceptNullValue() {
        assertThrown(() -> $return(null), AssertionError.class);
    }

    @Test
    public void returnValueShouldCreateNewReturnValue() {
        final ReturnValue e = $return(constant(100));

        assertEquals(constant(100), e.getValue());
    }

    @Test
    public void plusShouldNotAcceptInvalidArguments() {
        assertThrown(() -> AST.plus(null, constant(1)), AssertionError.class);
        assertThrown(() -> AST.plus(constant(1), null), AssertionError.class);
    }

    @Test
    public void plusShouldNotAcceptOperandsOfDifferentTypes() {
        assertThrown(() -> AST.plus(constant(1d), constant(1)), AssertionError.class);
    }

    @Test
    public void plusShouldCreateBinaryOperator() {
        final BinaryOperator operator = AST.plus(constant(1), constant(2));

        assertEquals(constant(1), operator.getLeftOperand());
        assertEquals(constant(2), operator.getRightOperand());
        assertEquals(OperatorType.PLUS, operator.getOperatorType());
        assertEquals(int.class, operator.getType());
    }

    @Test
    public void staticMethodCallShouldNotAcceptInvalidParameters() {
        assertThrown(() -> AST.call((Type) null, "foo", String.class), AssertionError.class);
        assertThrown(() -> AST.call(String.class, null, String.class), AssertionError.class);
        assertThrown(() -> AST.call(String.class, "", String.class), AssertionError.class);
        assertThrown(() -> AST.call(String.class, "foo", (Type) null), AssertionError.class);
        assertThrown(() -> AST.call(String.class, "foo", String.class, (Expression) null), AssertionError.class);
    }

    @Test
    public void staticMethodCallShouldCreateSyntaxTreeForCall() {
        final MethodCall actualMethodCall = AST.call(String.class, "valueOf", String.class, constant(1));
        final MethodCallImpl expectedMethodCall = new MethodCallImpl(String.class, "valueOf",
                MethodSignature.parse("(I)Ljava/lang/String;"), null, new Expression[]{new ConstantImpl(1, int.class)});

        assertEquals(expectedMethodCall, actualMethodCall);
    }

    @Test
    public void callStaticWithSignatureShouldNotAcceptInvalidParameters() {
        final Signature signature = MethodSignature.parse("(I)I");

        assertThrown(() -> AST.call((Type) null, "foo", signature), AssertionError.class);
        assertThrown(() -> AST.call(String.class, "", signature), AssertionError.class);
        assertThrown(() -> AST.call(String.class, null, signature), AssertionError.class);
        assertThrown(() -> AST.call(String.class, "foo", (Signature) null), AssertionError.class);
        assertThrown(() -> AST.call(String.class, "foo", signature, (Expression) null), AssertionError.class);
    }

    @Test
    public void callStaticWithSignatureShouldCreateMethodCall() {
        final MethodSignature signature = MethodSignature.parse("(Z)Ljava/lang/Boolean;");
        final MethodCall methodCall = AST.call(Boolean.class, "valueOf", signature, constant(1));

        assertEquals(new MethodCallImpl(Boolean.class, "valueOf", signature, null, new Expression[]{constant(1)}), methodCall);
    }

    @Test
    public void castShouldNotAcceptNullExpressionOrType() {
        assertThrown(() -> AST.cast(null), AssertionError.class);
        assertThrown(() -> AST.cast(constant("foo")).to(null), AssertionError.class);
    }

    @Test
    public void castShouldCreateNewCast() {
        final TypeCast cast = cast(constant("foo")).to(String.class);

        assertEquals(constant("foo"), cast.getValue());
        assertEquals(String.class, cast.getType());
    }

    @Test
    public void classConstantCannotHaveNullClass() {
        assertThrown(() -> AST.constant((Class) null), AssertionError.class);
    }

    @Test
    public void classConstantCanBeCreated() {
        assertEquals(new ConstantImpl(String.class, Class.class), AST.constant(String.class));
    }

    @Test
    public void addShouldNotAcceptAnyNullOperandOrResultType() {
        assertThrown(() -> AST.add(null, mock(Expression.class), Integer.class), AssertionError.class);
        assertThrown(() -> AST.add(mock(Expression.class), null, Integer.class), AssertionError.class);
        assertThrown(() -> AST.add(mock(Expression.class), mock(Expression.class), null), AssertionError.class);
    }

    @Test
    public void addShouldCreateBinaryOperatorWithOperands() {
        final BinaryOperator it = AST.add(operand1, operand2, Integer.class);

        assertEquals(operand1, it.getLeftOperand());
        assertEquals(operand2, it.getRightOperand());
        assertEquals(Integer.class, it.getType());
        assertEquals(OperatorType.PLUS, it.getOperatorType());
    }

    @Test
    public void subShouldCreateBinaryOperatorWithOperands() {
        final BinaryOperator it = AST.sub(operand1, operand2, Integer.class);

        assertEquals(operand1, it.getLeftOperand());
        assertEquals(operand2, it.getRightOperand());
        assertEquals(Integer.class, it.getType());
        assertEquals(OperatorType.MINUS, it.getOperatorType());
    }

    @Test
    public void binaryOperatorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> AST.binaryOperator(null, OperatorType.PLUS, operand1, Integer.class), AssertionError.class);
        assertThrown(() -> AST.binaryOperator(operand1, null, operand2, Integer.class), AssertionError.class);
        assertThrown(() -> AST.binaryOperator(operand1, OperatorType.PLUS, null, Integer.class), AssertionError.class);
        assertThrown(() -> AST.binaryOperator(operand1, OperatorType.PLUS, operand2, null), AssertionError.class);
    }

    @Test
    public void mulShouldCreateBinaryOperatorWithOperands() {
        final BinaryOperator it = AST.mul(operand1, operand2, Integer.class);

        assertEquals(operand1, it.getLeftOperand());
        assertEquals(operand2, it.getRightOperand());
        assertEquals(Integer.class, it.getType());
        assertEquals(OperatorType.MULTIPLY, it.getOperatorType());
    }

    @Test
    public void divShouldCreateBinaryOperatorWithOperands() {
        final BinaryOperator it = AST.div(operand1, operand2, Integer.class);

        assertEquals(operand1, it.getLeftOperand());
        assertEquals(operand2, it.getRightOperand());
        assertEquals(Integer.class, it.getType());
        assertEquals(OperatorType.DIVIDE, it.getOperatorType());
    }

    @Test
    public void setLocalShouldNotAcceptNullVariable() {
        assertThrown(() -> AST.set(null), AssertionError.class);
    }

    @Test
    public void setLocalShouldNotAcceptNullValue() {
        assertThrown(() -> AST.set(mock(LocalVariableReference.class)).to(null), AssertionError.class);
    }

    @Test
    public void setLocalShouldCreateVariableAssignment() {
        final LocalVariableReference local = new LocalVariableReferenceImpl("foo", String.class, 1234);
        final Expression expression = mock(Expression.class);
        final VariableAssignment variableAssignment = AST.set(local).to(expression);

        assertEquals(expression, variableAssignment.getValue());
        assertEquals(1234, variableAssignment.getVariableIndex());
        assertEquals("foo", variableAssignment.getVariableName());
        assertEquals(String.class, variableAssignment.getVariableType());
    }

    @Test
    public void booleanConstantCanBeCreated() {
        assertEquals(new ConstantImpl(true, boolean.class), constant(true));
        assertEquals(new ConstantImpl(false, boolean.class), constant(false));
    }

    @Test
    public void modShouldReturnModuloOperation() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.MODULO,
                constant(2),
                int.class), AST.mod(constant(1), constant(2), int.class));
    }

    @Test
    public void lshiftShouldReturnBinaryLeftShiftOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.LSHIFT,
                constant(2),
                int.class), AST.lshift(constant(1), constant(2), int.class));
    }

    @Test
    public void rshiftShouldReturnBinaryRightShiftOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.RSHIFT,
                constant(2),
                int.class), AST.rshift(constant(1), constant(2), int.class));
    }

    @Test
    public void unsignedRshiftShouldReturnBinaryRightShiftOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.UNSIGNED_RSHIFT,
                constant(2),
                int.class), AST.unsignedRightShift(constant(1), constant(2), int.class));
    }

    @Test
    public void neShouldCreateBinaryOperatorWithNEOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.NE,
                constant(2),
                boolean.class), AST.ne(constant(1), constant(2)));
    }

    @Test
    public void eqShouldCreateBinaryOperatorWithEQOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.EQ,
                constant(2),
                boolean.class), AST.eq(constant(1), constant(2)));
    }

    @Test
    public void geShouldCreateBinaryOperatorWithGEOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.GE,
                constant(2),
                boolean.class), AST.ge(constant(1), constant(2)));
    }

    @Test
    public void gtShouldCreateBinaryOperatorWithGTOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.GT,
                constant(2),
                boolean.class), AST.gt(constant(1), constant(2)));
    }

    @Test
    public void leShouldCreateBinaryOperatorWithLEOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.LE,
                constant(2),
                boolean.class), AST.le(constant(1), constant(2)));
    }

    @Test
    public void ltShouldCreateBinaryOperatorWithLTOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.LT,
                constant(2),
                boolean.class), AST.lt(constant(1), constant(2)));
    }

    @Test
    public void andShouldCreateBinaryOperatorWithAndOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.AND,
                constant(2),
                boolean.class), AST.and(constant(1), constant(2)));
    }

    @Test
    public void orShouldCreateBinaryOperatorWithOrOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.OR,
                constant(2),
                boolean.class), AST.or(constant(1), constant(2)));
    }

    @Test
    public void bitwiseAndShouldCreateBinaryOperatorWithAndOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.BITWISE_AND,
                constant(2),
                int.class), AST.bitwiseAnd(constant(1), constant(2), int.class));
    }

    @Test
    public void bitwiseOrShouldCreateBinaryOperatorWithOrOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.BITWISE_OR,
                constant(2),
                int.class), AST.bitwiseOr(constant(1), constant(2), int.class));
    }

    @Test
    public void xorShouldCreateBinaryOperatorWithXorOperator() {
        assertEquals(new BinaryOperatorImpl(
                constant(1),
                OperatorType.XOR,
                constant(2),
                int.class), AST.xor(constant(1), constant(2), int.class));
    }

    @Test
    public void newArrayShouldCreateArray() {
        final NewArray array = AST.newArray(int[].class, constant(1), constant(2));

        assertEquals(int[].class, array.getType());
        assertEquals(int.class, array.getComponentType());
        assertEquals(constant(2), array.getLength());
        assertEquals(constant(1), array.getInitializers().get(0).getValue());
        assertEquals(constant(2), array.getInitializers().get(1).getValue());
    }

}
