package io.recode.decompile.impl;

import io.recode.decompile.LineNumberCounter;

public final class NullLineNumberCounter implements LineNumberCounter {
    @Override
    public int get() {
        return -1;
    }
}
