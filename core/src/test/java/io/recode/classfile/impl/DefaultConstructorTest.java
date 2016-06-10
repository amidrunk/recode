package io.recode.classfile.impl;

import io.recode.classfile.Attribute;
import io.recode.classfile.ClassFile;
import io.recode.model.MethodSignature;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Supplier;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class DefaultConstructorTest {

    private final Supplier<ClassFile> classFileSupplier = mock(Supplier.class);

    private final DefaultConstructor constructor = new DefaultConstructor(classFileSupplier, 1234, "<init>", MethodSignature.parse("()V"), new Attribute[]{});

    @Test
    public void constructorShouldNotAcceptNullClassFileSupplier() {
        assertThrown(() -> new DefaultConstructor(null, 0, "foo", MethodSignature.parse("()V"), new Attribute[]{}), AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullName() {
        assertThrown(() -> new DefaultConstructor(classFileSupplier, 0, null, MethodSignature.parse("()V"), new Attribute[]{}), AssertionError.class);
    }

    @Test
    public void nameMustBeConstructorName() {
        assertThrown(() -> new DefaultConstructor(classFileSupplier, 0, "foo", MethodSignature.parse("()V"), new Attribute[]{}), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final ClassFile classFile = mock(ClassFile.class);
        when(classFileSupplier.get()).thenReturn(classFile);

        assertEquals(classFile, constructor.getClassFile());
        assertEquals(1234, constructor.getAccessFlags());
        assertEquals("<init>", constructor.getName());
        assertEquals(MethodSignature.parse("()V"), constructor.getSignature());
        assertEquals(Arrays.asList(new Attribute[]{}), constructor.getAttributes());
    }

    @Test
    public void signatureCannotBeNull() {
        assertThrown(() -> new DefaultConstructor(classFileSupplier, 0, "<init>", null, new Attribute[]{}), AssertionError.class);
    }

    @Test
    public void attributesCannotBeNull() {
        assertThrown(() -> new DefaultConstructor(classFileSupplier, 0, "<init>", MethodSignature.parse("()V"), null), AssertionError.class);
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(constructor, constructor);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(constructor.equals(null));
        assertFalse(constructor.equals("foo"));
    }

    @Test
    public void equalConstructorsShouldBeEqual() {
        final DefaultConstructor other = new DefaultConstructor(classFileSupplier, 1234, "<init>", MethodSignature.parse("()V"), new Attribute[]{});

        assertEquals(other, constructor);
        assertEquals(other.hashCode(), constructor.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertTrue(constructor.toString().contains("1234"));
        assertTrue(constructor.toString().contains("<init>"));
        assertTrue(constructor.toString().contains("()V"));
    }
}
