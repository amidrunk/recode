package io.recode.util;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class IteratorsTest {

    @Test
    public void iteratorOfShouldReturnIteratorWithMatchingContents() {
        final Iterator<String> iterator = Iterators.of("foo", "bar");

        assertEquals("foo", iterator.next());
        assertEquals("bar", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void filteredShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Iterators.filter(null, mock(Predicate.class)), AssertionError.class);
        assertThrown(() -> Iterators.filter(mock(Iterator.class), null), AssertionError.class);
    }

    @Test
    public void filteredShouldReturnMatchingElements() {
        final Iterator<Integer> iterator = Iterators.of(1, 2, 3, 4);
        final Iterator<Integer> filtered = Iterators.filter(iterator, n -> n % 2 == 0);

        assertEquals(Integer.valueOf(2), filtered.next());
        assertEquals(Integer.valueOf(4), filtered.next());
        assertFalse(filtered.hasNext());
    }

    @Test
    public void collectShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Iterators.collect(null, mock(Function.class)), AssertionError.class);
        assertThrown(() -> Iterators.collect(mock(Iterator.class), null), AssertionError.class);
    }

    @Test
    public void collectShouldReturnTransformedElements() {
        final Iterator sourceIterator = Iterators.of(1, 2, 3, 4);
        final Function function = String::valueOf;
        final Iterator transformedIterator = Iterators.collect(sourceIterator, function);

        assertEquals("1", transformedIterator.next());
        assertEquals("2", transformedIterator.next());
        assertEquals("3", transformedIterator.next());
        assertEquals("4", transformedIterator.next());
        assertFalse(transformedIterator.hasNext());
    }

    @Test
    public void emptyShouldNeverReturnAnyElements() {
        final Iterator<Object> iterator = Iterators.empty();

        assertFalse(iterator.hasNext());
        assertThrown(() -> iterator.next(), NoSuchElementException.class);
    }

    @Test
    public void toListShouldReturnEmptyListForEmptyIterator() {
        final Iterator<String> iterator = Collections.emptyIterator();

        assertTrue(Iterators.toList(iterator).isEmpty());
    }

    @Test
    public void toListShouldReturnListOfElementsInIterator() {
        final Iterator<Integer> iterator = Arrays.asList(1, 2, 3, 4).iterator();
        final List<Integer> list = Iterators.toList(iterator);

        assertEquals(Arrays.asList(1, 2, 3, 4), list);
    }
}