package io.recode.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PairTest {

    private final Pair<String, String> examplePair = new Pair<>("foo", "bar");

    @Test
    public void constructorShouldRetainArguments() {
        final Pair<String, String> it = new Pair<>("foo", "bar");

        assertEquals("foo", it.left());
        assertEquals("bar", it.right());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(examplePair, examplePair);
    }

    @Test
    public void pairShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(examplePair, null);
        assertNotEquals(examplePair, "foo");
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Pair<String, String> other = new Pair<>("foo", "bar");

        assertEquals(other, examplePair);
        assertEquals(other.hashCode(), examplePair.hashCode());
    }

    @Test
    public void toStringValueShouldContainElements() {
        assertTrue(examplePair.toString().contains("foo"));
        assertTrue(examplePair.toString().contains("bar"));
    }

    @Test
    public void newPairWithChangedLeftElementCanBeCreated() {
        final Pair<String, String> pair = Pair.of("foo", "bar");
        assertEquals(Pair.of(1234, "bar"), pair.left(1234));
    }

    @Test
    public void newPairWithChangedRightElementCanBeCreated() {
        final Pair<String, String> pair = Pair.of("foo", "bar");
        assertEquals(Pair.of("foo", 1234), pair.right(1234));
    }
}