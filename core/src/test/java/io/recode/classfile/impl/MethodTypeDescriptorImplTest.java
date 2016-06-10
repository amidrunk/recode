package io.recode.classfile.impl;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class MethodTypeDescriptorImplTest {

    @Test
    public void constructorShouldNotAcceptNullOrEmptyDescriptor() {
        assertThrown(() -> new MethodTypeDescriptorImpl(null), AssertionError.class);
        assertThrown(() -> new MethodTypeDescriptorImpl(""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainDescriptor() {
        final MethodTypeDescriptorImpl descriptor = new MethodTypeDescriptorImpl("()V");

        assertEquals("()V", descriptor.getDescriptor());
    }
}
