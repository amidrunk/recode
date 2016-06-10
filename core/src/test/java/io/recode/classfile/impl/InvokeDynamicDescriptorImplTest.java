package io.recode.classfile.impl;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class InvokeDynamicDescriptorImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new InvokeDynamicDescriptorImpl(0, null, "foo"), AssertionError.class);
        assertThrown(() -> new InvokeDynamicDescriptorImpl(0, "", "foo"), AssertionError.class);
        assertThrown(() -> new InvokeDynamicDescriptorImpl(0, "foo", null), AssertionError.class);
        assertThrown(() -> new InvokeDynamicDescriptorImpl(0, "foo", ""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final InvokeDynamicDescriptorImpl descriptor = new InvokeDynamicDescriptorImpl(1234, "foo", "()V");

        assertEquals(1234, descriptor.getBootstrapMethodAttributeIndex());
        assertEquals("foo", descriptor.getMethodName());
        assertEquals("()V", descriptor.getMethodDescriptor());
    }
}
