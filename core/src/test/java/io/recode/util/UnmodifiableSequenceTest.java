package io.recode.util;

import org.junit.Test;

import java.util.Arrays;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class UnmodifiableSequenceTest {

    private final Sequence<String> sourceSequence = new LinkedSequence<>();

    private final Sequence<String> sequence = new UnmodifiableSequence<>(sourceSequence);

    @Test
    public void constructorShouldNotAcceptNullSourceSequence() {
        assertThrown(() -> new UnmodifiableSequence<>(null), AssertionError.class);
    }

    @Test
    public void firstShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo", "bar"));

        final Sequence.SingleElement<String> selector = sequence.first();

        assertTrue(selector.exists());
        assertEquals("foo", selector.get());

        assertThrown(() -> selector.swap("X"), UnsupportedOperationException.class);
        assertThrown(() -> selector.remove(), UnsupportedOperationException.class);
    }

    @Test
    public void lastShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo", "bar"));

        final Sequence.SingleElement<String> selector = sequence.last();

        assertTrue(selector.exists());
        assertEquals("bar", selector.get());

        assertThrown(() -> selector.swap("X"), UnsupportedOperationException.class);
        assertThrown(() -> selector.remove(), UnsupportedOperationException.class);
    }

    @Test
    public void atShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo"));

        final Sequence.SingleElement<String> selector = sequence.at(0);

        assertEquals("foo", selector.get());

        assertThrown(() -> selector.swap("X"), UnsupportedOperationException.class);
        assertThrown(() -> selector.remove(), UnsupportedOperationException.class);
    }

    @Test
    public void allShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo"));

        assertEquals(Arrays.asList("foo"), sequence.all().get());
        assertThrown(() -> sequence.all().remove(), UnsupportedOperationException.class);
    }

    @Test
    public void clearShouldNotBeSupported() {
        assertThrown(() -> sequence.clear(), UnsupportedOperationException.class);
    }

    @Test
    public void sizeShouldBeReturnedFromSourceSequence() {
        sourceSequence.add("foo");

        assertEquals(1, sequence.size());
    }

    @Test
    public void firstByPredicateShouldReturnUnmodifiableSelector() {
        sourceSequence.add("foo");

        final Sequence.SingleElement<String> selector = sequence.first(s -> true);

        assertTrue(selector.exists());
        assertEquals("foo", selector.get());
        assertThrown(() -> selector.swap("bar"), UnsupportedOperationException.class);
        assertThrown(() -> selector.remove(), UnsupportedOperationException.class);
    }

    @Test
    public void lastByPredicateShouldReturnUnmodifiableSelector() {
        sourceSequence.addAll(Arrays.asList("foo", "bar"));

        final Sequence.SingleElement<String> selector = sequence.last(s -> true);

        assertTrue(selector.exists());
        assertEquals("bar", selector.get());
        assertThrown(() -> selector.swap("baz"), UnsupportedOperationException.class);
        assertThrown(() -> selector.remove(), UnsupportedOperationException.class);
    }

    @Test
    public void insertBeforeInElementSelectorShouldBeUnsupported() {
        sourceSequence.add("foo");

        assertThrown(() -> sequence.first().insertBefore("bar"), UnsupportedOperationException.class);
        assertThrown(() -> sequence.first(s -> true).insertBefore("bar"), UnsupportedOperationException.class);
        assertThrown(() -> sequence.last().insertBefore("bar"), UnsupportedOperationException.class);
        assertThrown(() -> sequence.last(s -> true).insertBefore("bar"), UnsupportedOperationException.class);
        assertThrown(() -> sequence.at(0).insertBefore("bar"), UnsupportedOperationException.class);

        assertArrayEquals(new String[]{"foo"}, sourceSequence.toArray());
    }
}