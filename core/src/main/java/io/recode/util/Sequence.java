package io.recode.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Sequence<T> extends Collection<T> {

    SingleElement<T> last();

    SingleElement<T> last(Predicate<T> predicate);

    SingleElement<T> first();

    SingleElement<T> first(Predicate<T> predicate);

    SingleElement<T> at(int index);

    MultipleElements<T> all();

    MultipleElements<T> tail(int offset);

    void clear();

    boolean isEmpty();

    int size();

    interface ElementSelector<T> {

        boolean exists();

    }

    interface SingleElement<T> extends ElementSelector<T> {

        void swap(T newElement);

        T remove();

        T get();

        /**
         * Inserts an element before the element that matches this selector. This can cause the selector to
         * evaluate to a different element, causing the {@link Sequence.SingleElement#get()}
         * method to return the new element. For example:
         * <pre>{@code
         * selector = sequence.first();
         * expect(selector.get()).toBe("bar");
         * selector.insertBefore("foo");
         * expect(selector.get()).toBe("foo");
         * }</pre>
         *
         * @param element The element that should be inserted.
         */
        void insertBefore(T element);

        // TODO insertAfter

        SingleElement<T> previous();

        default Optional<T> optional() {
            return (exists() ? Optional.of(get()) : Optional.empty());
        }

    }

    interface MultipleElements<T> extends ElementSelector<T> {

        List<T> get();

        void remove();
    }

}
