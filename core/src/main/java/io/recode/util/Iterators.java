package io.recode.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Iterators {

    public static<T>  List<T> toList(Iterator<T> iterator) {
        assert iterator != null;

        final LinkedList<T> list = new LinkedList<T>();

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        return new ArrayList<T>(list);
    }

    public static <T> Iterator<T> empty() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                throw new NoSuchElementException();
            }
        };
    }

    @SafeVarargs
    public static <T> Iterator<T> of(T ... elements) {
        return new Iterator<T>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < elements.length;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return elements[index++];
            }
        };
    }

    public static <T> Iterator<T> filter(Iterator<T> iterator, Predicate<T> predicate) {
        assert iterator != null : "Iterator can't be null";
        assert predicate != null : "Predicate can't be null";

        return new SuppliedIterator<>(() -> {
            while (iterator.hasNext()) {
                final T element = iterator.next();

                if (predicate.test(element)) {
                    return Optional.of(element);
                }
            }

            return Optional.empty();
        });
    }

    public static <S, T> Iterator<T> collect(Iterator<S> iterator, Function<S, T> function) {
        assert iterator != null : "Iterator can't be null";
        assert function != null : "Function can't be null";

        return new SuppliedIterator<>(() -> {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            return Optional.of(function.apply(iterator.next()));
        });
    }
}
