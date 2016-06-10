package io.recode.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public final class Range {

    private final int from;

    private final int to;

    public Range(int from, int to) {
        assert from <= to : "From must be less than or equal to to";

        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public static FromContinuation from(int from) {
        return to -> new Range(from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        if (from != range.from) return false;
        if (to != range.to) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        return result;
    }

    @Override
    public String toString() {
        return "[" + from + ", " + to + "]";
    }

    public int[] all() {
        final int[] elements = new int[to - from + 1];

        for (int i = from; i <= to; i++) {
            elements[i - from] = i;
        }

        return elements;
    }

    public List<Integer> allAsList() {
        return collect(n -> n);
    }

    public void each(IntConsumer intConsumer) {
        assert intConsumer != null : "IntConsumer can't be null";

        for (int i = from; i <= to; i++) {
            intConsumer.accept(i);
        }
    }

    public<R> List<R> collect(IntFunction<R> intFunction) {
        assert intFunction != null : "IntFunction can't be null";

        final List<R> result = new ArrayList<>(to - from + 1);

        for (int i = from; i <= to; i++) {
            result.add(intFunction.apply(i));
        }

        return result;
    }

    public interface FromContinuation {

        Range to(int to);

    }
}
