package io.recode.classfile.impl;

import io.recode.model.Signature;
import org.junit.Test;

import java.lang.reflect.Type;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class MethodReferenceImplTest {

    private final Signature signature = mock(Signature.class);

    private final MethodReferenceImpl exampleMethodReference = new MethodReferenceImpl(String.class, "foo", signature);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new MethodReferenceImpl(null, "foo", signature), AssertionError.class);
        assertThrown(() -> new MethodReferenceImpl(mock(Type.class), null, signature), AssertionError.class);
        assertThrown(() -> new MethodReferenceImpl(mock(Type.class), "", signature), AssertionError.class);
        assertThrown(() -> new MethodReferenceImpl(mock(Type.class), "foo", null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        assertEquals("foo", exampleMethodReference.getName());
        assertEquals(String.class, exampleMethodReference.getTargetType());
        assertEquals(signature, exampleMethodReference.getSignature());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleMethodReference, exampleMethodReference);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleMethodReference.equals("foo"));
        assertFalse(exampleMethodReference.equals(null));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final MethodReferenceImpl other = new MethodReferenceImpl(String.class, "foo", signature);

        assertEquals(other, exampleMethodReference);
        assertEquals(exampleMethodReference.hashCode(), other.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertTrue(exampleMethodReference.toString().contains(String.class.getName()));
        assertTrue(exampleMethodReference.toString().contains(signature.toString()));
        assertTrue(exampleMethodReference.toString().contains("foo"));
    }
}
