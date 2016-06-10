package io.recode.model.impl;

import io.recode.model.*;
import org.junit.Test;
import org.mockito.Mockito;

import static io.recode.model.AST.constant;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VariableAssignmentImplTest {

    private final Expression exampleValue = Mockito.mock(Expression.class);
    private final VariableAssignmentImpl assignment = new VariableAssignmentImpl(exampleValue, 123, "foo", String.class);

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new VariableAssignmentImpl(null, 0, "foo", String.class), AssertionError.class);
        assertThrown(() -> new VariableAssignmentImpl(exampleValue, -1, "foo", String.class), AssertionError.class);
        assertThrown(() -> new VariableAssignmentImpl(exampleValue, 0, null, String.class), AssertionError.class);
        assertThrown(() -> new VariableAssignmentImpl(exampleValue, 0, "", String.class), AssertionError.class);
        assertThrown(() -> new VariableAssignmentImpl(exampleValue, 0, "foo", null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        when(exampleValue.getType()).thenReturn(Integer.class);

        assertEquals(ElementType.VARIABLE_ASSIGNMENT, assignment.getElementType());
        assertEquals(exampleValue, assignment.getValue());
        assertEquals(123, assignment.getVariableIndex());
        assertEquals("foo", assignment.getVariableName());
        assertEquals(String.class, assignment.getVariableType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(assignment, assignment);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(assignment.equals(null));
        assertFalse(assignment.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final VariableAssignmentImpl other = new VariableAssignmentImpl(exampleValue, 123, "foo", String.class);

        assertEquals(other, assignment);
        assertEquals(other.hashCode(), assignment.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final String it = assignment.toString();

        assertTrue(it.contains(exampleValue.toString()));
        assertTrue(it.contains("123"));
        assertTrue(it.contains("foo"));
        assertTrue(it.contains(String.class.toString()));
    }

    @Test
    public void withValueShouldNotAcceptInvalidArg() {
        assertThrown(() -> assignment.withValue(null), AssertionError.class);
    }

    @Test
    public void withValueShouldFailIfValueIsNotAssignableToType() {
        assertThrown(() -> assignment.withValue(constant(1)), IncompatibleTypeException.class);
    }

    @Test
    public void withValueShouldReturnNewAssignmentWithNewValue() {
        final VariableAssignment newAssignment = assignment.withValue(constant("foobar"));

        assertEquals(assignment.getVariableName(), newAssignment.getVariableName());
        assertEquals(assignment.getVariableIndex(), newAssignment.getVariableIndex());
        assertEquals(assignment.getVariableType(), newAssignment.getVariableType());
        assertEquals(constant("foobar"), newAssignment.getValue());
        assertEquals(exampleValue, assignment.getValue());
    }

    @Test
    public void variableAssignmentWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(assignment.getMetaData());
        assertEquals(metaData, new VariableAssignmentImpl(exampleValue, 1, "foo", String.class, metaData).getMetaData());
    }

}
