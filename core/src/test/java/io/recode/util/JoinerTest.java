package io.recode.util;

import org.junit.Test;

import java.util.Arrays;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class JoinerTest {

    @Test
    public void joinShouldNotAcceptNullArgument() {
        assertThrown(() -> Joiner.join(null), AssertionError.class);
    }

    @Test
    public void joinContinuationShouldNotAcceptNullSeparator() {
        assertThrown(() -> Joiner.join(Arrays.asList("foo")).on(null), AssertionError.class);
    }

    @Test
    public void joinerShouldJoinElementsOnStringValue() {
        assertEquals("foo, bar, baz", Joiner.join(Arrays.asList("foo", "bar", "baz")).on(", "));
    }

}
