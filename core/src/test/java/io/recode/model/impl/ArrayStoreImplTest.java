package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ArrayStoreImplTest {

    private final Expression array = mock(Expression.class, "array");
    private final Expression index = mock(Expression.class, "index");
    private final Expression value = mock(Expression.class, "value");
    private final ArrayStoreImpl exampleArrayStore = new ArrayStoreImpl(array, index, value);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new ArrayStoreImpl(null, index, value), AssertionError.class);
        assertThrown(() -> new ArrayStoreImpl(array, null, value), AssertionError.class);
        assertThrown(() -> new ArrayStoreImpl(array, null, null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        assertEquals(array, exampleArrayStore.getArray());
        assertEquals(index, exampleArrayStore.getIndex());
        assertEquals(value, exampleArrayStore.getValue());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleArrayStore, exampleArrayStore);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleArrayStore.equals(null));
        assertFalse(exampleArrayStore.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final ArrayStoreImpl other = new ArrayStoreImpl(array, index, value);

        assertEquals(other, exampleArrayStore);
        assertEquals(other.hashCode(), exampleArrayStore.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final String it = exampleArrayStore.toString();

        assertTrue(it.contains(array.toString()));
        assertTrue(it.contains(index.toString()));
        assertTrue(it.contains(value.toString()));
    }

    @Test
    public void arrayStoreWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new ArrayStoreImpl(array, index, value).getMetaData());
        assertEquals(metaData, new ArrayStoreImpl(array, index, value, metaData).getMetaData());
    }
}
