package io.recode.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public final class Arrays2 {

    public static <T> T single(T[] array) {
        assert array != null : "Array can't be null";

        if (array.length == 0) {
            throw new IllegalArgumentException("Array is empty: " + array.getClass().getComponentType().getName() + "[]");
        }

        if (array.length > 1) {
            throw new IllegalArgumentException("Array contains multiple elements: " + Arrays.asList(array));
        }

        return array[0];
    }

    public static <F, T> T single(F[] array, Function<F, T> function) {
        assert function != null : "Function can't be null";

        return function.apply(single(array));
    }
}
