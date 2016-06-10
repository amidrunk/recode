package io.recode.util;

import java.util.*;
import java.util.stream.Stream;

public final class SingleThreadedStack<E> implements Stack<E> {

    public static final class Creator implements StackCreator {
        @Override
        public <E> Stack<E> createStack() {
            return new SingleThreadedStack<>();
        }
    }

    private final LinkedList<StackListener<E>> stackListeners = new LinkedList<>();

    private final List<E> targetList;

    public SingleThreadedStack() {
        this(new ArrayList<>());
    }

    public SingleThreadedStack(List<E> targetList) {
        assert targetList != null : "Target list can't be null";
        this.targetList = targetList;
    }

    public void addStackListener(StackListener<E> stackListener) {
        assert stackListener != null : "Stack listener can't be null";

        stackListeners.add(stackListener);
    }

    public void removeStackListener(StackListener<E> stackListener) {
        assert stackListener != null : "Stack listener can't be null";

        stackListeners.remove(stackListener);
    }

    public void push(E element) {
        assert element != null : "Element can't be null";

        targetList.add(element);

        for (StackListener<E> stackListener : stackListeners) {
            stackListener.onElementPushed(this, element);
        }
    }

    @SuppressWarnings("unchecked")
    public E pop() {
        final E element;

        try {
            if (targetList instanceof Deque) {
                element = ((Deque<E>) targetList).removeLast();
            } else {
                element = targetList.remove(targetList.size() - 1);
            }
        } catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }

        for (StackListener<E> stackListener : stackListeners) {
            stackListener.onElementPopped(this, element);
        }

        return element;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        try {
            if (targetList instanceof Deque) {
                return ((Deque<E>) targetList).getLast();
            }

            return targetList.get(targetList.size() - 1);
        } catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
            throw new EmptyStackException();
        }
    }

    public void insert(int index, E element) {
        targetList.add(index, element);

        for (StackListener<E> stackListener : stackListeners) {
            stackListener.onElementInserted(this, element, index);
        }
    }

    @Override
    public int size() {
        return targetList.size();
    }

    @Override
    public boolean isEmpty() {
        return targetList.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return targetList.iterator();
    }

    @Override
    public List<E> tail(int fromIndex) {
        return targetList.subList(fromIndex < 0 ? size() + fromIndex : fromIndex, size());
    }

    @Override
    public Stream<E> stream() {
        return targetList.stream();
    }

    @Override
    public void clear() {
        targetList.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E swap(E newElement) {
        assert newElement != null : "New element can't be null";

        final E oldElement;

        try {
            if (targetList instanceof Deque) {
                oldElement = ((Deque<E>) targetList).removeLast();
            } else {
                oldElement = targetList.remove(targetList.size() - 1);
            }
        } catch (ArrayIndexOutOfBoundsException|NoSuchElementException e) {
            throw new EmptyStackException();
        }

        targetList.add(newElement);

        for (StackListener<E> stackListener : stackListeners) {
            stackListener.onElementSwapped(this, oldElement, newElement);
        }

        return oldElement;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SingleThreadedStack)) {
            return false;
        }

        final SingleThreadedStack other = (SingleThreadedStack) o;

        return targetList.equals(other.targetList);
    }

    @Override
    public int hashCode() {
        return targetList.hashCode();
    }

    @Override
    public String toString() {
        return "SingleThreadedStack{elements=" + targetList + "}";
    }
}
