package io.recode.util;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RangeTest {

    private final Range exampleRange = new Range(1, 10);

    @Test
    public void fromCannotBeGreaterThanTo() {
        boolean failed = false;

        try {
            new Range(1, 0);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void constructorShouldRetainLimits() {
        assertEquals(1, exampleRange.getFrom());
        assertEquals(10, exampleRange.getTo());
    }

    @Test
    public void rangeShouldBeEqualToItSelf() {
        assertEquals(exampleRange, exampleRange);
    }

    @Test
    public void rangeShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleRange.equals(null));
        assertFalse(exampleRange.equals("foo"));
    }

    @Test
    public void rangesWithEqualPropertiesShouldBeEqual() {
        final Range other = new Range(1, 10);

        assertEquals(exampleRange, other);
        assertEquals(exampleRange.hashCode(), other.hashCode());
    }

    @Test
    public void toStringValueShouldContainLimits() {
        assertEquals("[1, 10]", exampleRange.toString());
    }

    @Test
    public void rangeCanBeCreatedFromDSL() {
        final Range range = Range.from(5).to(10);

        assertEquals(5, range.getFrom());
        assertEquals(10, range.getTo());
    }

    @Test
    public void rangeElementsCanBeRetrievedFromRange() {
        final Range range = Range.from(1).to(3);

        assertArrayEquals(new int[]{1, 2, 3}, range.all());
    }

    @Test
    public void eachShouldNotAcceptNullConsumer() {
        boolean failed = false;

        try {
            exampleRange.each(null);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void eachShouldVisitAllElements() {
        final Range range = Range.from(1).to(2);
        final IntConsumer consumer = mock(IntConsumer.class);

        range.each(consumer);

        final InOrder inOrder = Mockito.inOrder(consumer);

        inOrder.verify(consumer).accept(eq(1));
        inOrder.verify(consumer).accept(eq(2));
    }

    @Test
    public void collectShouldNotAcceptNullMapFunction() {
        boolean failed = false;

        try {
            exampleRange.collect(null);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void collectShouldReturnTransformedList() {
        final Range range = Range.from(1).to(2);
        final IntFunction intFunction = mock(IntFunction.class);

        when(intFunction.apply(eq(1))).thenReturn("foo");
        when(intFunction.apply(eq(2))).thenReturn("bar");

        final List result = range.collect(intFunction);

        assertArrayEquals(new Object[]{"foo", "bar"}, result.toArray());
    }

    @Test
    public void allAsListShouldReturnElementsAsList() {
        final List<Integer> list = Range.from(1).to(2).allAsList();

        assertArrayEquals(new Object[]{1, 2}, list.toArray());
    }
}
