package io.recode.classfile.impl;

import io.recode.classfile.BootstrapMethod;
import io.recode.classfile.BootstrapMethodsAttribute;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;

public class BootstrapMethodsAttributeImplTest {

    @Test
    public void constructorShouldNotAcceptNullMethods() {
        assertThrown(() -> new BootstrapMethodsAttributeImpl(null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainBootstrapMethods() {
        final BootstrapMethod bootstrapMethod = mock(BootstrapMethod.class);
        final List<BootstrapMethod> methods = Arrays.asList(bootstrapMethod);
        final BootstrapMethodsAttribute attribute = new BootstrapMethodsAttributeImpl(methods);

        assertArrayEquals(new Object[]{bootstrapMethod}, attribute.getBootstrapMethods().toArray());
    }
}
