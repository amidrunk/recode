package io.recode.model.impl;

import io.recode.model.Expression;
import org.junit.Test;
import org.mockito.Mockito;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class ArrayInitializerImplTest {

    private final Expression exampleValue = Mockito.mock(Expression.class);
    private final ArrayInitializerImpl exampleInitializer = new ArrayInitializerImpl(1234, exampleValue);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new ArrayInitializerImpl(-1, exampleValue), AssertionError.class);
        assertThrown(() -> new ArrayInitializerImpl(0, null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        assertEquals(1234, exampleInitializer.getIndex());
        assertEquals(exampleValue, exampleInitializer.getValue());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleInitializer, exampleInitializer);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleInitializer.equals(null));
        assertFalse(exampleInitializer.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final ArrayInitializerImpl other = new ArrayInitializerImpl(1234, exampleValue);

        assertEquals(other, exampleInitializer);
        assertEquals(other.hashCode(), exampleInitializer.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final String it = exampleInitializer.toString();

        assertTrue(it.contains("1234"));
        assertTrue(it.contains(exampleInitializer.toString()));
    }

}
