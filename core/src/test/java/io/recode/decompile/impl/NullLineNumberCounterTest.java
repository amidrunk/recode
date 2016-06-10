package io.recode.decompile.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NullLineNumberCounterTest {

    @Test
    public void getShouldAlwaysReturnNegative() {
        final NullLineNumberCounter it = new NullLineNumberCounter();

        assertEquals(-1, it.get());
    }
}
