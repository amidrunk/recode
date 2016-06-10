package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class InstanceAllocationImplTest {

    @Test
    public void constructorShouldNotAcceptNullArg() {
        assertThrown(() -> new InstanceAllocationImpl(null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainType() {
        final InstanceAllocationImpl e = new InstanceAllocationImpl(String.class);

        assertEquals(String.class, e.getType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        final InstanceAllocationImpl e = new InstanceAllocationImpl(String.class);

        assertEquals(e, e);
        assertEquals(e.hashCode(), e.hashCode());
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        final InstanceAllocationImpl e = new InstanceAllocationImpl(String.class);

        assertFalse(e.equals(null));
        assertFalse(e.equals("foo"));
    }

    @Test
    public void instancesWithEqualTypesShouldBeEqual() {
        final InstanceAllocationImpl e1 = new InstanceAllocationImpl(String.class);
        final InstanceAllocationImpl e2 = new InstanceAllocationImpl(String.class);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    public void instancesWithDifferentTypesShouldNotBeEqual() {
        final InstanceAllocationImpl e1 = new InstanceAllocationImpl(String.class);
        final InstanceAllocationImpl e2 = new InstanceAllocationImpl(Integer.class);

        assertFalse(e1.equals(e2));
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    public void toStringValueShouldContainTypeName() {
        final InstanceAllocationImpl e = new InstanceAllocationImpl(String.class);

        assertTrue(e.toString().contains(String.class.getName()));
    }

    @Test
    public void instanceWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new InstanceAllocationImpl(String.class).getMetaData());
        assertEquals(metaData, new InstanceAllocationImpl(String.class, metaData).getMetaData());
    }
}
