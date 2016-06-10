package io.recode.model.impl;

import io.recode.model.*;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class IncrementImplTest {

    private final Increment exampleIncrement = new IncrementImpl(AST.local("foo", int.class, 1), AST.constant(1), int.class, Affix.UNDEFINED);

    @Test
    public void constructorShouldNotAcceptNullLocalVariableOrValue() {
        assertThrown(() -> new IncrementImpl(null, AST.constant(1), int.class, Affix.UNDEFINED), AssertionError.class);
        assertThrown(() -> new IncrementImpl(AST.local("foo", int.class, 1), null, int.class, Affix.UNDEFINED), AssertionError.class);
        assertThrown(() -> new IncrementImpl(AST.local("foo", int.class, 1), AST.constant(1), null, Affix.UNDEFINED), AssertionError.class);
        assertThrown(() -> new IncrementImpl(AST.local("foo", int.class, 1), AST.constant(1), int.class, null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        assertEquals(ElementType.INCREMENT, exampleIncrement.getElementType());
        assertEquals(int.class, exampleIncrement.getType());
        assertEquals(AST.local("foo", int.class, 1), exampleIncrement.getLocalVariable());
        assertEquals(AST.constant(1), exampleIncrement.getValue());
        assertEquals(Affix.UNDEFINED, exampleIncrement.getAffix());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleIncrement, exampleIncrement);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleIncrement.equals(null));
        assertFalse(exampleIncrement.equals("foo"));
    }

    @Test
    public void instancesWithEqualOperandsShouldBeEqual() {
        final Increment other = new IncrementImpl(AST.local("foo", int.class, 1), AST.constant(1), int.class, Affix.UNDEFINED);

        assertEquals(other, exampleIncrement);
        assertEquals(other.hashCode(), exampleIncrement.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertTrue(exampleIncrement.toString().contains(AST.local("foo", int.class, 1).toString()));
        assertTrue(exampleIncrement.toString().contains(AST.constant(1).toString()));
        assertTrue(exampleIncrement.toString().contains(Affix.UNDEFINED.toString()));
    }

    @Test
    public void incrementWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertEquals(metaData, new IncrementImpl(mock(LocalVariableReference.class), mock(Expression.class), String.class, Affix.POSTFIX, metaData).getMetaData());
        assertNotNull(exampleIncrement.getMetaData());
    }
}