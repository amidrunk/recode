package io.recode.model;

import io.recode.model.impl.IncrementImpl;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Predicate;

import static io.recode.model.AST.*;
import static io.recode.model.ModelQueries.*;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ModelQueriesTest {

    @Test
    public void ofTypeShouldNotAcceptNullElementType() {
        assertThrown(() -> ModelQueries.ofType((ElementType) null), AssertionError.class);
    }

    @Test
    public void ofTypeShouldNotMatchElementOfDifferentType() {
        assertFalse(ModelQueries.ofType(ElementType.METHOD_CALL).test(constant(1)));
    }

    @Test
    public void ofTypeShouldMatchElementOfEqualType() {
        assertTrue(ModelQueries.ofType(ElementType.CONSTANT).test(constant(1)));
    }

    @Test
    public void ofTypeShouldNotMatchNull() {
        assertFalse(ModelQueries.ofType(ElementType.CONSTANT).test(null));
    }

    @Test
    public void isAssignmentToShouldNotAcceptNullLocalVariableReference() {
        assertThrown(() -> ModelQueries.isAssignmentTo(null), AssertionError.class);
    }

    @Test
    public void isAssignmentToShouldNotMatchIfAssignedVariableDoesNotMatch() {
        final boolean result = ModelQueries.isAssignmentTo(AST.local("foo", String.class, 1))
                .test(AST.set(AST.local("bar", String.class, 2)).to(constant(1)));

        assertFalse(result);
    }

    @Test
    public void isAssignmentShouldMatchIfAssignedVariableMatches() {
        final boolean result = ModelQueries.isAssignmentTo(AST.local("foo", String.class, 1))
                .test(AST.set(AST.local("foo", String.class, 1)).to(constant(1)));

        assertTrue(result);
    }

    @Test
    public void isAssignmentToShouldNotMatchNull() {
        final boolean result = ModelQueries.isAssignmentTo(AST.local("foo", String.class, 1)).test(null);

        assertFalse(result);
    }

    @Test
    public void assignedValueShouldReturnValueOfVariableAssignment() {
        final Constant expectedValue = constant(1);

        final Expression value = ModelQueries.assignedValue()
                .from(AST.set(AST.local("foo", String.class, 1)).to(expectedValue))
                .get();

        assertEquals(expectedValue, value);
    }

    @Test
    public void assignedValueShouldReturnNonPresentOptionalForNullAssignment() {
        assertFalse(ModelQueries.assignedValue().from(null).isPresent());
    }

    @Test
    public void isCastToShouldNotAcceptNullType() {
        assertThrown(() -> ModelQueries.isCastTo(null), AssertionError.class);
    }

    @Test
    public void isCastToShouldMatchCastWithCorrectTargetType() {
        assertTrue(ModelQueries.isCastTo(byte.class).test(cast(constant(1)).to(byte.class)));
    }

    @Test
    public void isCastToShouldNotMatchCastWithDifferentTargetType() {
        assertFalse(ModelQueries.isCastTo(byte.class).test(cast(constant(1)).to(int.class)));
    }

    @Test
    public void castValueShouldReturnNonPresentInstanceForNullCast() {
        assertFalse(ModelQueries.castValue().from(null).isPresent());
    }

    @Test
    public void castValueShouldReturnValueOfCast() {
        final Expression value = constant(1);

        assertEquals(Optional.of(value), ModelQueries.castValue().from(cast(value).to(byte.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ofTypeShouldNotAcceptNullType() {
        assertThrown(() -> ModelQueries.ofType((Class) null), AssertionError.class);
    }

    @Test
    public void ofTypeShouldMatchInstanceOfMatchingType() {
        assertTrue(ModelQueries.ofType(String.class).test("foo"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ofTypeShouldNotMatchInstanceOfDifferentType() {
        assertFalse(((Predicate) ModelQueries.ofType(String.class)).test(1234));
    }

    @Test
    public void leftOperandShouldNotBePresentForNullInstance() {
        assertFalse(leftOperand().from(null).isPresent());
    }

    @Test
    public void leftOperandShouldReturnLeftOperand() {
        assertEquals(Optional.of(constant(1)), leftOperand().from(AST.add(constant(1), constant(2), int.class)));
    }

    @Test
    public void rightOperandShouldNotBePresentForNullInstance() {
        assertFalse(rightOperand().from(null).isPresent());
    }

    @Test
    public void rightOperandShouldReturnRightOperand() {
        assertEquals(Optional.of(constant(2)), rightOperand().from(add(constant(1), constant(2), int.class)));
    }

    @Test
    public void affixIsUndefinedShouldReturnFalseForNull() {
        assertFalse(affixIsUndefined().test(null));
    }

    @Test
    public void affixIsUndefinedShouldNotMatchIncrementWithAffix() {
        assertFalse(affixIsUndefined().test(new IncrementImpl(local("foo", int.class, 1), constant(1), int.class, Affix.PREFIX)));
        assertFalse(affixIsUndefined().test(new IncrementImpl(local("foo", int.class, 1), constant(1), int.class, Affix.POSTFIX)));
    }

    @Test
    public void affixIsUndefinedShouldMatchIncrementWithUndefinedAffix() {
        assertTrue(affixIsUndefined().test(new IncrementImpl(local("foo", int.class, 1), constant(1), int.class, Affix.UNDEFINED)));
    }

    @Test
    public void assignedVariableTypeIsShouldNotAcceptNullType() {
        assertThrown(() -> ModelQueries.assignedVariableTypeIs(null), AssertionError.class);
    }

    @Test
    public void assignedVariableTypeIsShouldNotMatchNull() {
        assertFalse(ModelQueries.assignedVariableTypeIs(String.class).test(null));
    }

    @Test
    public void assignedVariableTypeShouldNotMatchAssignmentWithDifferentVariableType() {
        final VariableAssignment variableAssignment = set(local("foo", String.class, 1)).to(constant("bar"));

        assertFalse(ModelQueries.assignedVariableTypeIs(int.class).test(variableAssignment));
    }

    @Test
    public void assignedVariableTypeShouldMatchAssignmentWithMatchingVariableType() {
        final VariableAssignment variableAssignment = set(local("foo", String.class, 1)).to(constant("bar"));

        assertTrue(ModelQueries.assignedVariableTypeIs(String.class).test(variableAssignment));
    }

    @Test
    public void ofRuntimeTypeShouldNotAcceptNullType() {
        assertThrown(() -> ModelQueries.ofRuntimeType(null), AssertionError.class);
    }

    @Test
    public void ofRuntimeTypeShouldNotMatchNullExpression() {
        assertFalse(ModelQueries.ofRuntimeType(String.class).test(null));
    }

    @Test
    public void ofRuntimeTypeShouldNotMatchExpressionWithDifferentType() {
        final Expression expression = mock(Expression.class);

        when(expression.getType()).thenReturn(String.class);

        assertFalse(ModelQueries.ofRuntimeType(Object.class).test(expression));
    }

    @Test
    public void ofRuntimeTypeShouldMatchExpressionWithEqualType() {
        final Expression expression = mock(Expression.class);

        when(expression.getType()).thenReturn(String.class);

        assertTrue(ModelQueries.ofRuntimeType(String.class).test(expression));
    }
    
    @Test
    public void leftComparativeOperandShouldReturnNonPresentOptionalForNull() {
        assertFalse(ModelQueries.leftComparativeOperand().from(null).isPresent());
    }

    @Test
    public void leftComparativeOperandShouldReturnLeftOperandOfBranch() {
        final Branch branch = mock(Branch.class);
        final Expression operand = mock(Expression.class);

        when(branch.getLeftOperand()).thenReturn(operand);

        assertEquals(Optional.of(operand), ModelQueries.leftComparativeOperand().from(branch));
    }

    @Test
    public void rightComparativeOperandShouldReturnNonPresentOptionalForNull() {
        assertFalse(ModelQueries.rightComparativeOperand().from(null).isPresent());
    }

    @Test
    public void rightComparativeOperandShouldReturnOptionalOfRightOperand() {
        final Branch branch = mock(Branch.class);
        final Expression operand = mock(Expression.class);

        when(branch.getRightOperand()).thenReturn(operand);

        assertEquals(Optional.of(operand), ModelQueries.rightComparativeOperand().from(branch));
    }

    @Test
    public void runtimeTypeShouldReturnNonPresentOptionalForNull() {
        assertFalse(ModelQueries.runtimeType().from(null).isPresent());
    }

    @Test
    public void runtimeTypeShouldReturnTypeOfElement() {
        final Expression expression = mock(Expression.class);

        when(expression.getType()).thenReturn(int.class);

        assertEquals(Optional.of(int.class), ModelQueries.runtimeType().from(expression));

        verify(expression).getType();
    }

    @Test
    public void anyShouldMatchAnyElement() {
        assertTrue(ModelQueries.any().test(AST.constant(1)));
        assertTrue(ModelQueries.any().test(mock(Element.class)));
    }

    @Test
    public void fieldRefShouldMatchFieldWithMatchingProperties() {
        final Predicate<FieldReference> predicate = ModelQueries.field(ModelQueries.any(), "aField", String.class);

        assertTrue(predicate.test(AST.field(AST.constant("foo"), String.class, "aField")));
        assertFalse(predicate.test(AST.field(AST.constant("foo"), int.class, "aField")));
        assertFalse(predicate.test(AST.field(AST.constant("foo"), String.class, "anotherField")));
    }
}