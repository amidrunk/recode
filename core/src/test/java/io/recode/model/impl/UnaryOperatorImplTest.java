package io.recode.model.impl;

import io.recode.model.*;
import org.junit.Test;

import static io.recode.model.AST.constant;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UnaryOperatorImplTest {

    private final UnaryOperator exampleOperator = new UnaryOperatorImpl(constant(true), OperatorType.NOT, boolean.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new UnaryOperatorImpl(null, OperatorType.NOT, String.class), AssertionError.class);
        assertThrown(() -> new UnaryOperatorImpl(constant(true), null, String.class), AssertionError.class);
        assertThrown(() -> new UnaryOperatorImpl(constant(true), OperatorType.NOT, null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        assertEquals(constant(true), exampleOperator.getOperand());
        assertEquals(OperatorType.NOT, exampleOperator.getOperatorType());
        assertEquals(ElementType.UNARY_OPERATOR, exampleOperator.getElementType());
        assertEquals(boolean.class, exampleOperator.getType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleOperator, exampleOperator);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleOperator.equals(null));
        assertFalse(exampleOperator.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final UnaryOperator other = new UnaryOperatorImpl(constant(true), OperatorType.NOT, boolean.class);

        assertEquals(other, exampleOperator);
        assertEquals(other.hashCode(), exampleOperator.hashCode());
    }

    @Test
    public void elementWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new UnaryOperatorImpl(mock(Expression.class), OperatorType.NOT, String.class).getMetaData());
        assertEquals(metaData, new UnaryOperatorImpl(mock(Expression.class), OperatorType.NOT, String.class, metaData).getMetaData());
    }

}