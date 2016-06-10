package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class ReturnValueImplTest {

    @Test
    public void constructorShouldNotAcceptNullReturnValue() {
        assertThrown(() -> new ReturnValueImpl(null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeReturnValue() {
        final Expression expression = mock(Expression.class);
        final ReturnValueImpl returnValue = new ReturnValueImpl(expression);

        assertEquals(expression, returnValue.getValue());
        assertEquals(ElementType.RETURN_VALUE, returnValue.getElementType());
    }

    @Test
    public void returnValueWithMetaDataCanBeCreated() {
        final Expression value = mock(Expression.class);
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new ReturnValueImpl(value, metaData));
        assertEquals(metaData, new ReturnValueImpl(value, metaData).getMetaData());
    }
}
