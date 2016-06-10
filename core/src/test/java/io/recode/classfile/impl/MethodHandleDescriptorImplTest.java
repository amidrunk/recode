package io.recode.classfile.impl;

import io.recode.classfile.ReferenceKind;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class MethodHandleDescriptorImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new MethodHandleDescriptorImpl(null, "Foo", "bar", "()V"), AssertionError.class);
        assertThrown(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, null, "bar", "()V"), AssertionError.class);
        assertThrown(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "", "bar", "()V"), AssertionError.class);
        assertThrown(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", null, "()V"), AssertionError.class);
        assertThrown(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", "", "()V"), AssertionError.class);
        assertThrown(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", "bar", null), AssertionError.class);
        assertThrown(() -> new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", "bar", ""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final MethodHandleDescriptorImpl descriptor = new MethodHandleDescriptorImpl(ReferenceKind.GET_FIELD, "Foo", "bar", "()V");

        assertEquals(ReferenceKind.GET_FIELD, descriptor.getReferenceKind());
        assertEquals("Foo", descriptor.getClassName());
        assertEquals("bar", descriptor.getMethodName());
        assertEquals("()V", descriptor.getMethodDescriptor());
    }
}
