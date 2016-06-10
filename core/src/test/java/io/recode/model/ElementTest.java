package io.recode.model;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class ElementTest {

    @Test
    public void asShouldNotAcceptNullType() {
        assertThrown(() -> AST.constant(1).as(null), AssertionError.class);
    }

    @Test
    public void asShouldFailIfTheProvidedTypeIsNotCorrect() {
        assertThrown(() -> AST.constant(1).as(MethodCall.class), IllegalArgumentException.class);
    }

    @Test
    public void asShouldReturnSameInstanceButWithNarrowedType() {
        final Element element = AST.constant(1);
        final Constant constant = element.as(Constant.class);

        assertEquals(element, constant);
    }
}
