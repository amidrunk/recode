package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import io.recode.model.FieldReference;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FieldAssignmentImplTest {

    private final Expression expression = mock(Expression.class);
    private final FieldReference fieldReference = mock(FieldReference.class);
    private final FieldAssignmentImpl exampleAssignment = new FieldAssignmentImpl(fieldReference, expression);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new FieldAssignmentImpl(null, expression), AssertionError.class);
        assertThrown(() -> new FieldAssignmentImpl(fieldReference, null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        assertEquals(fieldReference, exampleAssignment.getFieldReference());
        assertEquals(expression, exampleAssignment.getValue());
        assertEquals(ElementType.FIELD_ASSIGNMENT, exampleAssignment.getElementType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleAssignment, exampleAssignment);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleAssignment.equals(null));
        assertFalse(exampleAssignment.equals("foo"));
    }

    @Test
    public void instanceWithEqualPropertiesShouldBeEqual() {
        final FieldAssignmentImpl other = new FieldAssignmentImpl(fieldReference, expression);

        assertEquals(other, exampleAssignment);
        assertEquals(other.hashCode(), exampleAssignment.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final String it = exampleAssignment.toString();

        assertTrue(it.contains(fieldReference.toString()));
        assertTrue(it.contains(expression.toString()));
    }

    @Test
    public void fieldAssignmentWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(exampleAssignment.getMetaData());
        assertEquals(metaData, new FieldAssignmentImpl(fieldReference, expression, metaData).getMetaData());
    }
}
