package io.recode.classfile.impl;

import io.recode.classfile.*;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultClassFileTest {

    private final ConstantPool constantPool = mock(ConstantPool.class);

    @Test
    public void bootstrapMethodAttributesCanBeResolvedIfExists() {
        final BootstrapMethodsAttribute attribute = mock(BootstrapMethodsAttribute.class);
        final ClassFile classFile = DefaultClassFile.fromVersion(0, 0)
                .withConstantPool(constantPool)
                .withSignature(0, "Test", "Object", new String[0])
                .withFields(new Field[0])
                .withConstructors(new Constructor[0])
                .withMethods(new Method[0])
                .withAttributes(new Attribute[]{attribute})
                .create();

        when(attribute.getName()).thenReturn(BootstrapMethodsAttribute.ATTRIBUTE_NAME);
        assertTrue(classFile.getBootstrapMethodsAttribute().isPresent());
        assertEquals(attribute, classFile.getBootstrapMethodsAttribute().get());

        when(attribute.getName()).thenReturn("Unknown");
        assertFalse(classFile.getBootstrapMethodsAttribute().isPresent());
    }
}
