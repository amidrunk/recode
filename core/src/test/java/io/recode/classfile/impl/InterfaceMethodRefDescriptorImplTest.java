package io.recode.classfile.impl;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class InterfaceMethodRefDescriptorImplTest {

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new InterfaceMethodRefDescriptorImpl(null, "foo", "()V"), AssertionError.class);
        assertThrown(() -> new InterfaceMethodRefDescriptorImpl("", "foo", "()V"), AssertionError.class);
        assertThrown(() -> new InterfaceMethodRefDescriptorImpl("foo", null, "()V"), AssertionError.class);
        assertThrown(() -> new InterfaceMethodRefDescriptorImpl("foo", "", "()V"), AssertionError.class);
        assertThrown(() -> new InterfaceMethodRefDescriptorImpl("foo", "bar", null), AssertionError.class);
        assertThrown(() -> new InterfaceMethodRefDescriptorImpl("foo", "bar", ""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final InterfaceMethodRefDescriptorImpl descriptor = new InterfaceMethodRefDescriptorImpl("foo", "bar", "()V");

        assertEquals("foo", descriptor.getClassName());
        assertEquals("bar", descriptor.getMethodName());
        assertEquals("()V", descriptor.getDescriptor());
    }
}
