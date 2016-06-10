package io.recode.util;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class Arrays2Test {

    @Test
    public void singleShouldNotAcceptNullArray() {
        assertThrown(() -> Arrays2.single(null), AssertionError.class);
    }

    @Test
    public void singleShouldFailForEmptyArray() {
        assertThrown(() -> Arrays2.single(new Object[0]), IllegalArgumentException.class);
    }

    @Test
    public void singleShouldFailForArrayWithMoreThanOneElement() {
        assertThrown(() -> Arrays2.single(new Object[]{"foo", "bar"}), IllegalArgumentException.class);
    }

    @Test
    public void singleShouldReturnOnlyArrayElement() {
        assertEquals("foo", Arrays2.single(new Object[]{"foo"}));
    }

    @Test
    public void singleWithFunctionShouldNotAcceptNullFunction() {
        assertThrown(() -> Arrays2.single(new Object[0], null), AssertionError.class);
    }

    @Test
    public void singleWithFunctionShouldReturnSingleTransformedArguments() {
        final int length = Arrays2.single(new String[]{"foo"}, String::length);

        assertEquals(3, length);
    }
}
