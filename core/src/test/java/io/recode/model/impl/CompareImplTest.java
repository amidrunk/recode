package io.recode.model.impl;

import io.recode.model.Compare;
import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class CompareImplTest {

    private final Expression rightOperand = mock(Expression.class);
    private final Expression leftOperand = mock(Expression.class);
    private final Compare exampleCompare = new CompareImpl(leftOperand, rightOperand);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new CompareImpl(null, rightOperand), AssertionError.class);
        assertThrown(() -> new CompareImpl(leftOperand, null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        assertEquals(leftOperand, exampleCompare.getLeftOperand());
        assertEquals(rightOperand, exampleCompare.getRightOperand());
        assertEquals(ElementType.COMPARE, exampleCompare.getElementType());
        assertEquals(int.class, exampleCompare.getType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleCompare, exampleCompare);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleCompare.equals(null));
        assertFalse(exampleCompare.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Compare other = new CompareImpl(leftOperand, rightOperand);

        assertEquals(other, exampleCompare);
        assertEquals(other.hashCode(), exampleCompare.hashCode());
    }

    @Test
    public void compareWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(exampleCompare.getMetaData());
        assertEquals(metaData, new CompareImpl(leftOperand, rightOperand, metaData).getMetaData());
    }
}