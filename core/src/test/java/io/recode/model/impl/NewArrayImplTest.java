package io.recode.model.impl;

import io.recode.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class NewArrayImplTest {

    private final ArrayInitializer exampleInitializer = mock(ArrayInitializer.class);

    private final NewArrayImpl exampleArray = new NewArrayImpl(String[].class, String.class, AST.constant(1), Arrays.asList(exampleInitializer));

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new NewArrayImpl(null, String.class, AST.constant(1), Collections.<ArrayInitializer>emptyList()), AssertionError.class);
        assertThrown(() -> new NewArrayImpl(String[].class, null, AST.constant(1), Collections.<ArrayInitializer>emptyList()), AssertionError.class);
        assertThrown(() -> new NewArrayImpl(String[].class, String.class, null, Collections.<ArrayInitializer>emptyList()), AssertionError.class);
        assertThrown(() -> new NewArrayImpl(String[].class, String.class, AST.constant(1), null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        assertEquals(String.class, exampleArray.getComponentType());
        assertEquals(AST.constant(1), exampleArray.getLength());
        assertEquals(String[].class, exampleArray.getType());
        assertEquals(ElementType.NEW_ARRAY, exampleArray.getElementType());
        assertArrayEquals(new Object[]{exampleInitializer}, exampleArray.getInitializers().toArray());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleArray, exampleArray);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleArray.equals(null));
        assertFalse(exampleArray.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final NewArrayImpl other = new NewArrayImpl(String[].class, String.class, AST.constant(1), Arrays.asList(exampleInitializer));

        assertEquals(other, exampleArray);
        assertEquals(other.hashCode(), exampleArray.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final String it = exampleArray.toString();

        assertTrue(it.contains(String.class.getTypeName()));
        assertTrue(it.contains(AST.constant(1).toString()));
        assertTrue(it.contains(exampleInitializer.toString()));
    }

    @Test
    public void newArrayWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(exampleArray.getMetaData());
        assertEquals(metaData, new NewArrayImpl(String[].class, String.class, mock(Expression.class), Collections.emptyList(), metaData).getMetaData());
    }
}
