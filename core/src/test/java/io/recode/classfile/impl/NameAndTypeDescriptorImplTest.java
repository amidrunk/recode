package io.recode.classfile.impl;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class NameAndTypeDescriptorImplTest {

    private final NameAndTypeDescriptorImpl descriptor = new NameAndTypeDescriptorImpl("foo", "bar");

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new NameAndTypeDescriptorImpl(null, "I"), AssertionError.class);
        assertThrown(() -> new NameAndTypeDescriptorImpl("", "I"), AssertionError.class);
        assertThrown(() -> new NameAndTypeDescriptorImpl("foo", null), AssertionError.class);
        assertThrown(() -> new NameAndTypeDescriptorImpl("foo", ""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        assertEquals("foo", descriptor.getName());
        assertEquals("bar", descriptor.getDescriptor());
    }

    @Test
    public void descriptorShouldBeEqualToItSelf() {
        assertEquals(descriptor, descriptor);
    }

    @Test
    public void descriptorShouldNotBeEqualToNullOrInCorrectType() {
        assertFalse(descriptor.equals("foo"));
        assertFalse(descriptor.equals(null));
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertTrue(descriptor.toString().contains("foo"));
        assertTrue(descriptor.toString().contains("bar"));
    }

}
