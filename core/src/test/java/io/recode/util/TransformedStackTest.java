package io.recode.util;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.function.Function;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class TransformedStackTest {

    private final Stack<Integer> targetStack = mock(Stack.class);

    private final Function<String, Integer> sourceToTarget = Integer::parseInt;
    private final Function<Integer, String> targetToSource = Object::toString;
    private final Stack<String> stack = new TransformedStack<String, Integer>(targetStack, sourceToTarget, targetToSource);
    private final StackListener stackListener = mock(StackListener.class);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new TransformedStack<>(null, sourceToTarget, targetToSource), AssertionError.class);
        assertThrown(() -> new TransformedStack<>(targetStack, null, targetToSource), AssertionError.class);
        assertThrown(() -> new TransformedStack<>(targetStack, sourceToTarget, null), AssertionError.class);
    }

    @Test
    public void pushShouldPushTransformedElementToTargetStack() {
        stack.push("1234");
        verify(targetStack).push(eq(1234));
    }

    @Test
    public void peekShouldReturnTransformedElementFromTargetStack() {
        when(targetStack.peek()).thenReturn(1234);
        assertEquals("1234", stack.peek());
        verify(targetStack).peek();
    }

    @Test
    public void popShouldReturnTransformedElementFromTargetStack() {
        when(targetStack.pop()).thenReturn(1234);
        assertEquals("1234", stack.pop());
        verify(targetStack).pop();
    }

    @Test
    public void sizeShouldBeReturnedFromTargetStack() {
        when(targetStack.size()).thenReturn(100);
        assertEquals(100, stack.size());
        verify(targetStack).size();
    }

    @Test
    public void insertShouldInsertTransformedElementInTargetStack() {
        stack.insert(1234, "1000");
        verify(targetStack).insert(eq(1234), eq(1000));
    }

    @Test
    public void isEmptyShouldBeReturnedFromTargetStack() {
        when(targetStack.isEmpty()).thenReturn(true);
        assertTrue(stack.isEmpty());
        verify(targetStack).isEmpty();
    }

    @Test
    public void tailShouldReturnTransformedTail() {
        when(targetStack.tail(eq(2))).thenReturn(Arrays.asList(1, 2, 3, 4));
        assertEquals(Arrays.asList("1", "2", "3", "4"), stack.tail(2));
        verify(targetStack).tail(eq(2));
    }

    @Test
    public void streamShouldReturnTransformedStreamFromTargetStack() {
        when(targetStack.stream()).thenReturn(Arrays.asList(1, 2, 3).stream());
        final String[] result = stack.stream().toArray(String[]::new);

        assertArrayEquals(new String[] {"1", "2", "3"}, result);

        verify(targetStack).stream();
    }

    @Test
    public void clearShouldClearTargetStack() {
        stack.clear();
        verify(targetStack).clear();
    }

    @Test
    public void iteratorShouldReturnTransformedIterator() {
        when(targetStack.iterator()).thenReturn(Arrays.asList(1, 2, 3).iterator());

        assertEquals(Arrays.asList("1", "2", "3"), Iterators.toList(stack.iterator()));

        verify(targetStack).iterator();
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(stack, stack);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(stack, null);
        assertNotEquals(stack, "foo");
    }

    @Test
    public void instancesWithEqualTargetStacksAndTransformersShouldBeEqual() {
        final TransformedStack<String, Integer> other = new TransformedStack<>(targetStack, sourceToTarget, targetToSource);

        assertEquals(other, stack);
        assertEquals(other.hashCode(), stack.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertTrue(stack.toString().contains(targetStack.toString()));
        assertTrue(stack.toString().contains(sourceToTarget.toString()));
        assertTrue(stack.toString().contains(targetToSource.toString()));
    }

    @Test
    public void addStackListenerShouldNotAcceptNullArg() {
        assertThrown(() -> stack.addStackListener(null), AssertionError.class);
    }

    @Test
    public void addStackListenerShouldAddStackListenerOnTarget() {
        stack.addStackListener(stackListener);
        verify(targetStack).addStackListener(any());
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsPushed() {
        stack.addStackListener(stackListener);

        targetListener().onElementPushed(targetStack, 1234);

        verify(stackListener).onElementPushed(eq(stack), eq("1234"));
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsPopped() {
        stack.addStackListener(stackListener);

        targetListener().onElementPopped(targetStack, 1234);

        verify(stackListener).onElementPopped(eq(stack), eq("1234"));
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsInserted() {
        stack.addStackListener(stackListener);

        targetListener().onElementInserted(targetStack, 1234, 100);

        verify(stackListener).onElementInserted(eq(stack), eq("1234"), eq(100));
    }

    @Test
    public void removeStackListenerShouldNotAcceptNullArg() {
        assertThrown(() -> stack.removeStackListener(null), AssertionError.class);
    }

    @Test
    public void removeStackListenerShouldRemoveListenerFromStack() {
        stack.addStackListener(stackListener);

        final StackListener targetListener = targetListener();

        stack.removeStackListener(stackListener);

        verify(targetStack).removeStackListener(targetListener);
    }

    @Test
    public void swapShouldSwapOnTargetAndReturnResult() {
        when(targetStack.swap(1)).thenReturn(2);

        assertEquals("2", stack.swap("1"));
    }

    @Test
    public void listenerShouldGetNotifiedWhenStackElementIsSwapped() {
        stack.addStackListener(stackListener);

        targetListener().onElementSwapped(targetStack, 1, 2);

        verify(stackListener).onElementSwapped(eq(stack), eq("1"), eq("2"));
    }

    private StackListener targetListener() {
        final ArgumentCaptor<StackListener> captor = ArgumentCaptor.forClass(StackListener.class);
        verify(targetStack).addStackListener(captor.capture());
        return captor.getValue();
    }
}