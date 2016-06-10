package io.recode.util;

import java.util.*;
import java.util.function.Function;

public final class Lists {

    public static <T> Optional<T> first(List<T> list) {
        assert list != null : "List can't be null";

        if (list.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> last(List<T> list) {
        assert list != null : "List can't be null";

        if (list.isEmpty()) {
            return Optional.empty();
        }

        if (list instanceof Deque) {
            return Optional.of(((Deque<T>) list).getLast());
        }

        return Optional.of(list.get(list.size() - 1));
    }

    public static <S, T> List<T> collect(List<S> list, Function<S, T> function) {
        assert list != null : "List can't be null";
        assert function != null : "Function can't be null";

        final ArrayList<T> copy = new ArrayList<>(list.size());

        for (S element : list) {
            copy.add(function.apply(element));
        }

        return copy;
    }

    public static <S, T> List<Pair<S, T>> zip(List<S> left, List<T> right) {
        assert left != null : "Left list can't be null";
        assert right != null : "Right list can't be null";

        final int size = left.size();

        if (right.size() != size) {
            throw new IllegalArgumentException("Lists must be of equal sizes");
        }

        final ArrayList<Pair<S, T>> zippedList = new ArrayList<>(size);

        final Iterator<S> leftIterator = left.iterator();
        final Iterator<T> rightIterator = right.iterator();

        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            zippedList.add(new Pair<S, T>(leftIterator.next(), rightIterator.next()));
        }

        if (leftIterator.hasNext() || rightIterator.hasNext()) {
            throw new ConcurrentModificationException("Left/right list was modified while zipping");
        }

        return zippedList;
    }

    /**
     * Optionally collects elements from the provided list. If the function returns the same elements as the
     * input, an empty optional will be returned. Otherwise, an optional with the new elements will be returned.
     *
     * @param list The list to collect elements from.
     * @param function The function to apply to the elements.
     * @param <T> The type of the elements.
     * @return A optional of a new list of any transformation was applied. If no transformation was applied,
     * an empty optional is returned.
     */
    public static <T> Optional<List<T>> optionallyCollect(List<T> list, Function<T, T> function) {
        assert list != null : "List can't be null";
        assert function != null : "Function can't be null";

        final Iterator<T> iterator = list.iterator();

        List<T> copy = null;
        int index = 0;

        while (iterator.hasNext()) {
            final T originalElement = iterator.next();
            final T transformedElement = function.apply(originalElement);

            if (!Objects.equals(originalElement, transformedElement)) {
                copy = new ArrayList<>(list.size());
                copy.addAll(list.subList(0, index));
                copy.add(transformedElement);
                break;
            }

            index++;
        }

        while (iterator.hasNext()) {
            copy.add(function.apply(iterator.next()));
        }

        return Optional.ofNullable(copy);
    }

}
