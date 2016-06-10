package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import io.recode.model.OperatorType;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class BinaryOperatorImplTest {

    private final Expression rightOperand = mock(Expression.class);
    private final Expression leftOperand = mock(Expression.class);
    private final BinaryOperatorImpl exampleOperator = new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, int.class);

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new BinaryOperatorImpl(null, OperatorType.PLUS, rightOperand, int.class), AssertionError.class);
        assertThrown(() -> new BinaryOperatorImpl(leftOperand, null, rightOperand, int.class), AssertionError.class);
        assertThrown(() -> new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, null, int.class), AssertionError.class);
        assertThrown(() -> new BinaryOperatorImpl(leftOperand, OperatorType.PLUS, rightOperand, null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeOperatorsAndOperatorType() {
        assertEquals(leftOperand, exampleOperator.getLeftOperand());
        assertEquals(OperatorType.PLUS, exampleOperator.getOperatorType());
        assertEquals(rightOperand, exampleOperator.getRightOperand());
        assertEquals(int.class, exampleOperator.getType());
    }

    @Test
    public void elementTypeShouldBeSpecified() {
        assertEquals(ElementType.BINARY_OPERATOR, exampleOperator.getElementType());
    }

    @Test
    public void elementWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new BinaryOperatorImpl(leftOperand, OperatorType.AND, rightOperand, int.class).getMetaData());
        assertEquals(metaData, new BinaryOperatorImpl(leftOperand, OperatorType.AND, rightOperand, int.class, metaData).getMetaData());
    }

}
