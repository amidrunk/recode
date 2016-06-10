package io.recode.classfile.impl;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class FieldRefDescriptorImplTest {

    @Test
    public void constructorShouldValidateArguments() {
        assertThrown(() -> new FieldRefDescriptorImpl(null, "I", "foo"), AssertionError.class);
        assertThrown(() -> new FieldRefDescriptorImpl("", "I", "foo"), AssertionError.class);
        assertThrown(() -> new FieldRefDescriptorImpl("java/lang/String", null, "foo"), AssertionError.class);
        assertThrown(() -> new FieldRefDescriptorImpl("java/lang/String", "", "foo"), AssertionError.class);
        assertThrown(() -> new FieldRefDescriptorImpl("java/lang/String", "I", null), AssertionError.class);
        assertThrown(() -> new FieldRefDescriptorImpl("java/lang/String", "I", ""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final FieldRefDescriptorImpl fieldDescriptor = new FieldRefDescriptorImpl("java/lang/String", "I", "foo");

        assertEquals("java/lang/String", fieldDescriptor.getClassName());
        assertEquals("I", fieldDescriptor.getDescriptor());
        assertEquals("foo", fieldDescriptor.getName());
    }
}