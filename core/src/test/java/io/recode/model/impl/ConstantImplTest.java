package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ConstantImplTest {

    @Test
    public void constructorShouldNotAcceptNullConstantIfTypeIsPrimitive() {
        assertThrown(() -> new ConstantImpl(null, int.class), AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullType() {
        assertThrown(() -> new ConstantImpl(1234, null), AssertionError.class);
    }

    @Test
    public void objectConstantCanBeNull() {
        final ConstantImpl c = new ConstantImpl(null, Object.class);

        assertEquals(Object.class, c.getType());
        assertNull(c.getConstant());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void constructorShouldCreateValidInstance() {
        final ConstantImpl constant = new ConstantImpl("foobar", String.class);

        assertEquals("foobar", constant.getConstant());
        assertEquals(String.class, constant.getType());
        assertEquals(ElementType.CONSTANT, constant.getElementType());
    }

    @Test
    public void constantWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new ConstantImpl("foo", String.class).getMetaData());
        assertEquals(metaData, new ConstantImpl("foo", String.class, metaData).getMetaData());
    }
}
