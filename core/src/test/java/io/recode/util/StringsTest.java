package io.recode.util;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class StringsTest {

    @Test
    public void rightPadShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Strings.rightPad(null, 10, ' '), AssertionError.class);
        assertThrown(() -> Strings.rightPad("", -1, ' '), AssertionError.class);
    }

    @Test
    public void rightPadShouldReturnSameStringIfLengthIsEqualToOrGreaterThanRequired() {
        assertEquals("foo", Strings.rightPad("foo", 2, ' '));
        assertEquals("foo", Strings.rightPad("foo", 3, ' '));
    }

    @Test
    public void rightPadShouldAppendPadCharacterAndReturnStringOfRequiredLength() {
        assertEquals("foo ", Strings.rightPad("foo", 4, ' '));
        assertEquals("fooXX", Strings.rightPad("foo", 5, 'X'));
        assertEquals("fooYYY", Strings.rightPad("foo", 6, 'Y'));
    }
}
