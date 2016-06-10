package io.recode.decompile.impl;

import io.recode.classfile.Method;
import io.recode.decompile.CodePointer;
import io.recode.model.Element;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class CodePointerTest {

    private final Element element = mock(Element.class);
    private final Method method = mock(Method.class);

    @Test
    public void constructorShouldValidateArguments() {
        assertThrown(() -> new CodePointerImpl(null, element), AssertionError.class);
        assertThrown(() -> new CodePointerImpl(method, null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final CodePointer codePointer = new CodePointerImpl(method, element);

        assertEquals(method, codePointer.getMethod());
        assertEquals(element, codePointer.getElement());
    }

    @Test
    public void forElementShouldNotAcceptNullElement() {
        final CodePointer codePointer = new CodePointerImpl(method, element);

        assertThrown(() -> codePointer.forElement(null), AssertionError.class);
    }

    @Test
    public void forElementShouldReturnPointerToElementInOriginalContext() {
        final CodePointer originalPointer = new CodePointerImpl(method, element);
        final Element newElement = mock(Element.class);
        final CodePointer newCodePointer = originalPointer.forElement(newElement);

        assertEquals(newElement, newCodePointer.getElement());
        assertEquals(method, newCodePointer.getMethod());
    }
}
