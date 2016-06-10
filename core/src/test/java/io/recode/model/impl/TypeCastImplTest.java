package io.recode.model.impl;

import io.recode.model.AST;
import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TypeCastImplTest {

    private final TypeCastImpl exampleCast = new TypeCastImpl(AST.constant("foo"), String.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new TypeCastImpl(null, String.class), AssertionError.class);
        assertThrown(() -> new TypeCastImpl(AST.constant("foo"), null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        assertEquals(AST.constant("foo"), exampleCast.getValue());
        assertEquals(String.class, exampleCast.getType());
        assertEquals(ElementType.CAST, exampleCast.getElementType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleCast, exampleCast);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleCast.equals(null));
        assertFalse(exampleCast.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final TypeCastImpl other = new TypeCastImpl(AST.constant("foo"), String.class);

        assertEquals(other, exampleCast);
        assertEquals(other.hashCode(), exampleCast.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final String it = exampleCast.toString();

        assertTrue(it.contains(AST.constant("foo").toString()));
        assertTrue(it.contains(String.class.getName()));
    }

    @Test
    public void typeCastWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(exampleCast.getMetaData());
        assertEquals(metaData, new TypeCastImpl(mock(Expression.class), String.class, metaData).getMetaData());
    }
}
