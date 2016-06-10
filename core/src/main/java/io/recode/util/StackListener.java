package io.recode.util;

public interface StackListener<E> {

    void onElementPushed(Stack<E> stack, E element);

    void onElementPopped(Stack<E> stack, E element);

    void onElementInserted(Stack<E> stack, E element, int index);

    void onElementSwapped(Stack<E> stack, E oldElement, E newElement);
}
