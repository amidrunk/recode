package io.recode.util;

import java.util.Collections;

@SuppressWarnings("unchecked")
public final class Sequences {

    private static final Sequence EMPTY = unmodifiableSeries(new LinkedSequence());

    public static <E> Sequence<E> emptySequence() {
        return EMPTY;
    }

    public static <E> Sequence<E> sequenceOf(E... elements) {
        final Sequence<E> sequence = new LinkedSequence<>();

        Collections.addAll(sequence, elements);

        return unmodifiableSeries(sequence);
    }

    public static <E> Sequence<E> unmodifiableSeries(Sequence<E> sequence) {
        return new UnmodifiableSequence<>(sequence);
    }

}
