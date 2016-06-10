package io.recode.util;

import org.junit.Test;

import java.util.Arrays;

import static io.recode.test.Assertions.assertThrown;
import static io.recode.util.Sequences.sequenceOf;
import static io.recode.util.Sequences.unmodifiableSeries;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SequencesTest {

    @Test
    public void emptySeriesShouldReturnEmptyImmutableSeries() {
        final Sequence<Object> sequence = Sequences.emptySequence();

        assertTrue(sequence.isEmpty());
        assertEquals(0, sequence.size());

        assertThrown(() -> sequence.add("foo"), UnsupportedOperationException.class);
    }

    @Test
    public void unmodifiableSeriesShouldNotAcceptNullSource() {
        assertThrown(() -> unmodifiableSeries(null), AssertionError.class);
    }

    @Test
    public void unmodifiableSeriesShouldReturnReadOnlyViewOfSeries() {
        final Sequence<String> sequence = new LinkedSequence<>();

        sequence.add("foo");
        sequence.add("bar");

        final Sequence<String> unmodifiableSequence = unmodifiableSeries(sequence);

        assertEquals(Arrays.asList("foo", "bar"), unmodifiableSequence.all().get());
        assertThrown(() -> unmodifiableSequence.add("baz"), UnsupportedOperationException.class);
        assertThrown(() -> unmodifiableSequence.remove("foo"), UnsupportedOperationException.class);
        assertThrown(() -> unmodifiableSequence.clear(), UnsupportedOperationException.class);
        assertThrown(() -> unmodifiableSequence.all().remove(), UnsupportedOperationException.class);
        assertThrown(() -> unmodifiableSequence.first().swap("baz"), UnsupportedOperationException.class);
    }

    @Test
    public void sequenceOfShouldReturnUnmodifiableSequenceWithElements() {
        final Sequence<String> sequence = sequenceOf("x", "y", "z");

        assertEquals(Arrays.asList("x", "y", "z"), sequence.all().get());
        assertThrown(() -> sequence.add("foo"), UnsupportedOperationException.class);
    }
}