package io.recode.util;

import java.util.List;
import java.util.stream.Stream;

// TODO extend sequence?
public interface Stack<E> extends Iterable<E> {

    void addStackListener(StackListener<E> stackListener);

    void removeStackListener(StackListener<E> stackListener);

    void push(E element);

    E pop();

    E peek();

    void insert(int index, E element);

    int size();

    boolean isEmpty();

    List<E> tail(int fromIndex);

    Stream<E> stream();

    void clear();

    E swap(E newElement);

}
