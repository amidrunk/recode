package io.recode.util;

import org.junit.Test;

import java.util.regex.Pattern;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class StringReaderTest {

    @Test
    public void constructorShouldNotAcceptNullString() {
        assertThrown(() -> new StringReader(null), AssertionError.class);
    }

    @Test
    public void remainderShouldInitiallyBeEntireString() {
        assertEquals("foo", new StringReader("foo").remainder());
    }

    @Test
    public void readExactStringShouldNotAcceptNullArgument() {
        assertThrown(() -> new StringReader("foo").read(null), AssertionError.class);
    }

    @Test
    public void readExactStringShouldReturnFalseIfStringDoesNotMatcher() {
        final StringReader reader = new StringReader("foo");

        assertFalse(reader.read("bar"));
        assertEquals("foo", reader.remainder());
    }

    @Test
    public void readExactStringShouldReturnTrueAndForwardReaderIfStringMatches() {
        final StringReader reader = new StringReader("foobar");

        assertTrue(reader.read("foo"));
        assertEquals("bar", reader.remainder());
    }

    @Test
    public void readUntilShouldNotAcceptNullPattern() {
        assertThrown(() -> new StringReader("foo").readUntil(null), AssertionError.class);
    }

    @Test
    public void readUntilShouldReturnNullIfNoMatchIsFound() {
        final StringReader reader = new StringReader("foobar");

        assertFalse(reader.readUntil(Pattern.compile("X")).isPresent());
    }

    @Test
    public void readUntilShouldReturnMatchingStringAndProgressReader() {
        final StringReader reader = new StringReader("foo.bar");

        assertEquals("foo", reader.readUntil(Pattern.compile("\\.")).get());
        assertEquals(".bar", reader.remainder());
    }

    @Test
    public void readCharShouldReturnNegativeIfNothingRemains() {
        assertEquals(-1, new StringReader("").read());
    }

    @Test
    public void readCharShouldReturnCharacterAndProgressReader() {
        final StringReader reader = new StringReader("foo");

        assertEquals((int) 'f', reader.read());
        assertEquals("oo", reader.remainder());
        assertEquals((int) 'o', reader.read());
        assertEquals("o", reader.remainder());
    }

    @Test
    public void peekShouldReturnNegativeIfNothingRemains() {
        assertEquals(-1, new StringReader("").peek());
    }

    @Test
    public void peekShouldReturnCurrentCharAndNotProgressReader() {
        final StringReader reader = new StringReader("foo");

        assertEquals((int) 'f', reader.peek());
        assertEquals("foo", reader.remainder());
    }

    @Test
    public void skipShouldNotAcceptNegativeOrZeroDelta() {
        assertThrown(() -> new StringReader("foo").skip(-1), AssertionError.class);
        assertThrown(() -> new StringReader("foo").skip(0), AssertionError.class);
    }

    @Test
    public void skipShouldReturnFalseIfCountIsToLarge() {
        final StringReader reader = new StringReader("foo");

        assertFalse(reader.skip(5));
        assertEquals("foo", reader.remainder());
    }

    @Test
    public void skipShouldReturnTrueAndProgressReaderIfCountIsValid() {
        final StringReader reader = new StringReader("foo");

        assertTrue(reader.skip(1));
        assertEquals("oo", reader.remainder());
    }
}
