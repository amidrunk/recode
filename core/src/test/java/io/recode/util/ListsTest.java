package io.recode.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class ListsTest {

    @Test
    public void firstShouldNotAcceptNullList() {
        assertThrown(() -> Lists.first(null), AssertionError.class);
    }

    @Test
    public void firstShouldReturnNonPresentOptionalForEmptyList() {
        assertFalse(Lists.first(Collections.emptyList()).isPresent());
    }

    @Test
    public void firstShouldReturnFirstElementInList() {
        assertEquals(Optional.of("foo"), Lists.first(Arrays.asList("foo", "bar")));
    }

    @Test
    public void lastShouldNotAcceptNullList() {
        assertThrown(() -> Lists.last(null), AssertionError.class);
    }

    @Test
    public void lastShouldReturnNonPresentOptionalForEmptyList() {
        assertFalse(Lists.last(Collections.emptyList()).isPresent());
    }

    @Test
    public void lastShouldReturnLastElementForList() {
        assertEquals(Optional.of("bar"), Lists.last(Arrays.asList("foo", "bar")));
    }

    @Test
    public void optionallyCollectShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Lists.optionallyCollect(null, mock(Function.class)), AssertionError.class);
        assertThrown(() -> Lists.optionallyCollect(mock(List.class), null), AssertionError.class);
    }

    @Test
    public void optionallyCollectShouldReturnEmptyOptionalForSameElements() {
        assertFalse(Lists.optionallyCollect(Arrays.asList("foo", "bar", "baz"), str -> str).isPresent());
    }

    @Test
    public void optionallyCollectShouldReturnNewListIfAnyElementIsTransformed() {
        final Function<String, String> function = mock(Function.class);

        when(function.apply(eq("foo"))).thenAnswer(returnsFirstArg());
        when(function.apply(eq("bar"))).thenReturn("BAR");
        when(function.apply(eq("baz"))).thenAnswer(returnsFirstArg());

        assertEquals(Optional.of(Arrays.asList("foo", "BAR", "baz")), Lists.optionallyCollect(Arrays.asList("foo", "bar", "baz"), function));
    }

    @Test
    public void zipShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Lists.zip(null, Collections.emptyList()), AssertionError.class);
        assertThrown(() -> Lists.zip(Collections.emptyList(), null), AssertionError.class);
    }

    @Test
    public void zipShouldNotAcceptListsOfDifferentLengths() {
        assertThrown(() -> Lists.<String, Integer>zip(Arrays.asList("foo"), Arrays.asList(1, 2, 3)), IllegalArgumentException.class);
    }

    @Test
    public void zipShouldReturnListOfPairedElements() {
        final List<Pair<String, Integer>> zipped = Lists.zip(Arrays.asList("a", "b", "c"), Arrays.asList(1, 2, 3));

        assertArrayEquals(new Pair[] {
                new Pair<>("a", 1), new Pair<>("b", 2), new Pair<>("c", 3)
        }, zipped.toArray());
    }

    @Test
    public void collectShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Lists.collect(null, mock(Function.class)), AssertionError.class);
        assertThrown(() -> Lists.collect(mock(List.class), null), AssertionError.class);
    }

    @Test
    public void collectShouldReturnNewElements() {
        final List<Integer> result = Lists.collect(Arrays.asList("a", "ab", "abc"), String::length);
        assertArrayEquals(new Integer[] {1,2,3}, result.toArray());
    }
}
