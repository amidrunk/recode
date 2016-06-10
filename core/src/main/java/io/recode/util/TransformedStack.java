package io.recode.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class TransformedStack<S, T> implements io.recode.util.Stack<S> {

    private final io.recode.util.Stack<T> targetStack;

    private final Function<S, T> acceptTransform;

    private final Function<T, S> retrieveTransform;

    private final List<StackListenerDelegate> stackListeners = new LinkedList<>();

    public TransformedStack(io.recode.util.Stack<T> targetStack, Function<S, T> acceptTransform, Function<T, S> retrieveTransform) {
        assert targetStack != null : "Target stack can't be null";
        assert acceptTransform != null : "Source to target converter can't be null";
        assert retrieveTransform != null : "Target source source converter can't be null";

        this.targetStack = targetStack;
        this.acceptTransform = acceptTransform;
        this.retrieveTransform = retrieveTransform;
    }

    @Override
    public void addStackListener(StackListener<S> stackListener) {
        assert stackListener != null : "Stack listener can't be null";

        final StackListenerDelegate delegate = new StackListenerDelegate(stackListener);
        targetStack.addStackListener(delegate);
        stackListeners.add(delegate);
    }

    @Override
    public void removeStackListener(StackListener<S> stackListener) {
        assert stackListener != null : "Stack listener can't be null";

        for (Iterator<StackListenerDelegate> iterator = stackListeners.iterator(); iterator.hasNext(); ) {
            final StackListenerDelegate delegate = iterator.next();

            if (Objects.equals(delegate.targetStackListener, stackListener)) {
                targetStack.removeStackListener(delegate);
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void push(S element) {
        targetStack.push(acceptTransform.apply(element));
    }

    @Override
    public S pop() {
        return retrieveTransform.apply(targetStack.pop());
    }

    @Override
    public S peek() {
        return retrieveTransform.apply(targetStack.peek());
    }

    @Override
    public void insert(int index, S element) {
        targetStack.insert(index, acceptTransform.apply(element));
    }

    @Override
    public int size() {
        return targetStack.size();
    }

    @Override
    public boolean isEmpty() {
        return targetStack.isEmpty();
    }

    @Override
    public List<S> tail(int fromIndex) {
        final List<T> targetList = targetStack.tail(fromIndex);

        return new AbstractList<S>() {
            @Override
            public S get(int index) {
                return retrieveTransform.apply(targetList.get(index));
            }

            @Override
            public int size() {
                return targetList.size();
            }
        };
    }

    @Override
    public Stream<S> stream() {
        return targetStack.stream().map(retrieveTransform);
    }

    @Override
    public void clear() {
        targetStack.clear();
    }

    @Override
    public S swap(S newElement) {
        return retrieveTransform.apply(targetStack.swap(acceptTransform.apply(newElement)));
    }

    @Override
    public Iterator<S> iterator() {
        return Iterators.collect(targetStack.iterator(), retrieveTransform);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransformedStack that = (TransformedStack) o;

        if (!acceptTransform.equals(that.acceptTransform)) return false;
        if (!targetStack.equals(that.targetStack)) return false;
        if (!retrieveTransform.equals(that.retrieveTransform)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetStack.hashCode();
        result = 31 * result + acceptTransform.hashCode();
        result = 31 * result + retrieveTransform.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TransformedStack{" +
                "targetStack=" + targetStack +
                ", acceptTransform=" + acceptTransform +
                ", retrieveTransform=" + retrieveTransform +
                '}';
    }

    private class StackListenerDelegate implements StackListener<T> {

        private final StackListener<S> targetStackListener;

        private StackListenerDelegate(StackListener<S> targetStackListener) {
            this.targetStackListener = targetStackListener;
        }

        @Override
        public void onElementPushed(io.recode.util.Stack<T> stack, T element) {
            targetStackListener.onElementPushed(TransformedStack.this, retrieveTransform.apply(element));
        }

        @Override
        public void onElementPopped(io.recode.util.Stack<T> stack, T element) {
            targetStackListener.onElementPopped(TransformedStack.this, retrieveTransform.apply(element));
        }

        @Override
        public void onElementInserted(io.recode.util.Stack<T> stack, T element, int index) {
            targetStackListener.onElementInserted(TransformedStack.this, retrieveTransform.apply(element), index);
        }

        @Override
        public void onElementSwapped(io.recode.util.Stack<T> stack, T oldElement, T newElement) {
            targetStackListener.onElementSwapped(TransformedStack.this, retrieveTransform.apply(oldElement), retrieveTransform.apply(newElement));
        }
    }
}
