package io.recode.model.impl;

import io.recode.model.AST;
import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ArrayLoadImplTest {

    private final ArrayLoadImpl exampleArrayLoad = new ArrayLoadImpl(AST.local("foo", String[].class, 1), AST.constant(1), String.class);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new ArrayLoadImpl(null, AST.constant(1), String.class), AssertionError.class);
        assertThrown(() -> new ArrayLoadImpl(AST.local("foo", String[].class, 1), null, String.class), AssertionError.class);
        assertThrown(() -> new ArrayLoadImpl(AST.local("foo", String[].class, 1), AST.constant(1), null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        assertEquals(AST.local("foo", String[].class, 1), exampleArrayLoad.getArray());
        assertEquals(AST.constant(1), exampleArrayLoad.getIndex());
        assertEquals(String.class, exampleArrayLoad.getType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleArrayLoad, exampleArrayLoad);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleArrayLoad.equals(null));
        assertFalse(exampleArrayLoad.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final ArrayLoadImpl other = new ArrayLoadImpl(exampleArrayLoad.getArray(), exampleArrayLoad.getIndex(), exampleArrayLoad.getType());

        assertEquals(other, exampleArrayLoad);
        assertEquals(other.hashCode(), exampleArrayLoad.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final String str = exampleArrayLoad.toString();

        assertTrue(str.contains(exampleArrayLoad.getArray().toString()));
        assertTrue(str.contains(exampleArrayLoad.getIndex().toString()));
        assertTrue(str.contains(exampleArrayLoad.getType().toString()));
    }

    @Test
    public void elementWithMetaDataAndDefaultMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        final ArrayLoadImpl arrayLoad1 = new ArrayLoadImpl(mock(Expression.class), mock(Expression.class), String.class);
        final ArrayLoadImpl arrayLoad2 = new ArrayLoadImpl(mock(Expression.class), mock(Expression.class), String.class, metaData);

        assertFalse(arrayLoad1.getMetaData().equals(null));
        assertEquals(metaData, arrayLoad2.getMetaData());
    }

}
