package io.recode.util;

import org.junit.Test;

import java.util.Arrays;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class TransformedSequenceTest {

    private final Sequence<String> sourceSequence = new LinkedSequence<>();

    private final Sequence<Integer> transformedSequence = new TransformedSequence<>(sourceSequence, Object::toString, Integer::parseInt);

    @Test
    public void enlistShouldNotAcceptNullArg() {
        assertThrown(() -> transformedSequence.add(null), AssertionError.class);
    }

    @Test
    public void enlistShouldEnlistTransformedElementInSourceSeries() {
        transformedSequence.add(1234);
        assertEquals(Arrays.asList("1234"), sourceSequence.all().get());
    }

    @Test
    public void lastShouldReturnTransformedElementSelectorFromSourceSeries() {
        sourceSequence.add("1234");

        final Sequence.SingleElement<Integer> last = transformedSequence.last();

        assertTrue(last.exists());
        assertEquals(Integer.valueOf(1234), last.get());

        last.swap(2345);
        assertEquals("2345", sourceSequence.last().get());
        assertEquals(Integer.valueOf(2345), last.get());

        last.remove();

        assertTrue(sourceSequence.isEmpty());
    }

    @Test
    public void firstShouldReturnTransformedElementSelectorFromSourceSeries() {
        sourceSequence.add("1111");

        assertEquals(Integer.valueOf(1111), transformedSequence.first().get());
        assertTrue(transformedSequence.first().exists());

        transformedSequence.first().swap(2345);

        assertEquals("2345", sourceSequence.first().get());
        assertEquals(Integer.valueOf(2345), transformedSequence.first().get());

        transformedSequence.first().remove();

        assertTrue(sourceSequence.isEmpty());
    }

    @Test
    public void atIndexShouldReturnTransformedSelectorFromSourceSeries() {
        sourceSequence.add("1");
        sourceSequence.add("2");
        sourceSequence.add("3");

        assertEquals(Integer.valueOf(2), transformedSequence.at(1).get());
        assertTrue(transformedSequence.at(1).exists());

        transformedSequence.at(1).swap(4321);

        assertEquals("4321", sourceSequence.at(1).get());
        assertEquals(Integer.valueOf(4321), transformedSequence.at(1).get());
        assertEquals(Arrays.asList("1", "4321", "3"), sourceSequence.all().get());

        transformedSequence.at(1).remove();

        assertEquals(Arrays.asList("1", "3"), sourceSequence.all().get());
    }

    @Test
    public void allShouldReturnTransformedSelectorFromSourceSeries() {
        sourceSequence.add("1");
        sourceSequence.add("2");

        assertEquals(Arrays.asList(1, 2), transformedSequence.all().get());

        transformedSequence.all().remove();

        assertTrue(sourceSequence.isEmpty());
    }

    @Test
    public void sizeShouldBeReturnedFromSourceList() {
        sourceSequence.add("1234");

        assertEquals(1, transformedSequence.size());
    }

    @Test
    public void iteratorShouldContainTransformedElements() {
        sourceSequence.add("1");
        sourceSequence.add("2");
        sourceSequence.add("3");

        assertEquals(Arrays.asList(1, 2, 3), Iterators.toList(transformedSequence.iterator()));
    }

    @Test
    public void clearShouldClearSourceSeries() {
        sourceSequence.add("1234");
        sourceSequence.add("2345");
        transformedSequence.clear();

        assertTrue(sourceSequence.isEmpty());
    }

    @Test
    public void isEmptyShouldBeTrueIfSourceSeriesIsEmpty() {
        assertTrue(transformedSequence.isEmpty());
    }

    @Test
    public void isEmptyShouldBeFalseIfSourceSeriesIsFalse() {
        sourceSequence.add("1234");
        assertFalse(transformedSequence.isEmpty());
    }

    @Test
    public void firstShouldNotAcceptNullPredicate() {
        assertThrown(() -> transformedSequence.first(null), AssertionError.class);
    }

    @Test
    public void firstShouldReturnFirstMatchingElement() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3", "4"));
        assertEquals(Integer.valueOf(2), transformedSequence.first(n -> n >= 2).get());
        assertEquals(Integer.valueOf(3), transformedSequence.first(n -> n >= 3).get());
    }

    @Test
    public void lastByPredicateShouldReturnFirstMatchingElementFromEnd() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3", "4"));

        final Sequence.SingleElement<Integer> selector = transformedSequence.last(n -> n <= 3);

        assertTrue(selector.exists());
        assertEquals(Integer.valueOf(3), selector.get());
    }

    @Test
    public void lastByPredicateShouldNotAcceptNullArg() {
        assertThrown(() -> transformedSequence.last(null), AssertionError.class);
    }

    @Test
    public void insertBeforeFirstShouldInsertElementAtFirstIndex() {
        sourceSequence.add("1");
        transformedSequence.first().insertBefore(0);

        assertEquals(Arrays.asList("0", "1"), sourceSequence.all().get());
    }

    @Test
    public void insertBeforeFirstByPredicateShouldInsertElementBeforeFirstMatchingElement() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3"));

        transformedSequence.first(n -> n == 2).insertBefore(100);

        assertEquals(Arrays.asList("1", "100", "2", "3"), sourceSequence.all().get());
    }

    @Test
    public void insertBeforeLastShouldInsertElementSecondToLast() {
        sourceSequence.addAll(Arrays.asList("1", "2"));

        transformedSequence.last().insertBefore(100);

        assertEquals(Arrays.asList("1", "100", "2"), sourceSequence.all().get());
    }


    @Test
    public void insertBeforeLastByPredicateShouldInsertElementBeforeFirstMatchingFromEnd() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3"));

        transformedSequence.last(n -> n <= 2).insertBefore(100);

        assertEquals(Arrays.asList("1", "100", "2", "3"), sourceSequence.all().get());
    }

    @Test
    public void insertBeforeElementAtIndexShouldInsertElementBeforeMatchingIndex() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3"));

        transformedSequence.at(1).insertBefore(100);

        assertEquals(Arrays.asList("1", "100", "2", "3"), sourceSequence.all().get());
    }

    @Test
    public void selectorCanBeNavigatedToPreviousElement() {
        sourceSequence.addAll(Arrays.asList("1", "2", "3"));
        assertEquals(Integer.valueOf(2), transformedSequence.last().previous().get());
    }

}