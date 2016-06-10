package io.recode.classfile.impl;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MethodRefDescriptorImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new MethodRefDescriptorImpl(null, "bar", "()V"), AssertionError.class);
        assertThrown(() -> new MethodRefDescriptorImpl("", "bar", "()V"), AssertionError.class);
        assertThrown(() -> new MethodRefDescriptorImpl("Foo", null, "()V"), AssertionError.class);
        assertThrown(() -> new MethodRefDescriptorImpl("Foo", "", "()V"), AssertionError.class);
        assertThrown(() -> new MethodRefDescriptorImpl("Foo", "bar", null), AssertionError.class);
        assertThrown(() -> new MethodRefDescriptorImpl("Foo", "bar", ""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final MethodRefDescriptorImpl descriptor = new MethodRefDescriptorImpl("Foo", "bar", "()V");

        assertEquals("Foo", descriptor.getClassName());
        assertEquals("bar", descriptor.getMethodName());
        assertEquals("()V", descriptor.getDescriptor());
    }

    @Test
    public void methodRefDescriptorShouldBeEqualToItSelf() {
        final MethodRefDescriptorImpl descriptor = new MethodRefDescriptorImpl("Foo", "bar", "()V");

        assertEquals(descriptor, descriptor);
        assertEquals(descriptor.hashCode(), descriptor.hashCode());
    }

    @Test
    public void methodRefDescriptorShouldNotBeEqualToNullOrDifferentType() {
        final MethodRefDescriptorImpl descriptor = new MethodRefDescriptorImpl("Foo", "bar", "()V");

        assertFalse(descriptor.equals(null));
        assertFalse(descriptor.equals("foo"));
    }

    @Test
    public void methodRefDescriptorsWithEqualPropertiesShouldBeEqual() {
        final MethodRefDescriptorImpl descriptor1 = new MethodRefDescriptorImpl("Foo", "bar", "()V");
        final MethodRefDescriptorImpl descriptor2 = new MethodRefDescriptorImpl("Foo", "bar", "()V");

        assertEquals(descriptor2, descriptor1);
        assertEquals(descriptor1.hashCode(), descriptor2.hashCode());
    }
}

