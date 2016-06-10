package io.recode.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InOrder;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.function.Supplier;

import static io.recode.test.Assertions.assertThrown;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.Parameters;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(Parameterized.class)
public class SingleThreadedStackTest {

    private final StackListener stackListener = mock(StackListener.class);

    @Parameters
    public static Iterable<Object[]> stacks() {
        return Arrays.<Object[]>asList(
                new Object[]{(Supplier) SingleThreadedStack::new},
                new Object[]{(Supplier) () -> new SingleThreadedStack<>(new LinkedList<>())}
        );
    }

    private final Stack<String> stack;

    public SingleThreadedStackTest(Supplier<Stack<String>> stackSupplier) {
        this.stack = stackSupplier.get();
    }

    @Test
    public void constructorShouldNotAcceptNullBackingList() {
        assertThrown(() -> new SingleThreadedStack<String>(null), AssertionError.class);
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
    public void instancesWithEqualContentsShouldBeEqual() {
        final SingleThreadedStack<String> other = new SingleThreadedStack<>();

        stack.push("foo");
        other.push("foo");

        assertEquals(other, stack);
        assertEquals(other.hashCode(), stack.hashCode());
    }

    @Test
    public void toStringShouldReturnStringWithContents() {
        stack.push("foo");
        stack.push("bar");

        assertTrue(stack.toString().contains("foo"));
        assertTrue(stack.toString().contains("bar"));
    }

    @Test
    public void pushShouldNotAcceptNullElement() {
        assertThrown(() -> stack.push(null), AssertionError.class);
    }

    @Test
    public void popShouldFailIfStackIsEmpty() {
        assertThrown(() -> stack.pop(), EmptyStackException.class);
    }

    @Test
    public void peekShouldFailIfStackIsEmpty() {
        assertThrown(() -> stack.peek(), EmptyStackException.class);
    }

    @Test
    public void popShouldReturnLastElementAndRemoveIt() {
        stack.push("bar");
        stack.push("foo");

        assertEquals("foo", stack.pop());
        assertEquals(Arrays.asList("bar"), stack.stream().collect(toList()));

        assertEquals("bar", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    public void peekShouldReturnLastElementWithoutRemovingIt() {
        stack.push("bar");
        stack.push("foo");

        assertEquals("foo", stack.peek());
        assertEquals(Arrays.asList("bar", "foo"), stack.stream().collect(toList()));
    }

    @Test
    public void addStackListenerShouldNotAcceptNullArg() {
        assertThrown(() -> stack.addStackListener(null), AssertionError.class);
    }

    @Test
    public void removeStackListenerShouldNotAcceptNullArg() {
        assertThrown(() -> stack.removeStackListener(null), AssertionError.class);
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsPushed() {
        stack.addStackListener(stackListener);
        stack.push("foo");

        verify(stackListener).onElementPushed(eq(stack), eq("foo"));
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsPopped() {
        stack.addStackListener(stackListener);

        stack.push("foo");
        stack.pop();

        final InOrder inOrder = inOrder(stackListener);

        inOrder.verify(stackListener).onElementPushed(eq(stack), eq("foo"));
        inOrder.verify(stackListener).onElementPopped(eq(stack), eq("foo"));
    }

    @Test
    public void stackListenerShouldGetNotifiedWhenElementIsInserted() {
        stack.addStackListener(stackListener);

        stack.push("foo");
        stack.insert(0, "bar");

        final InOrder inOrder = inOrder(stackListener);

        inOrder.verify(stackListener).onElementPushed(eq(stack), eq("foo"));
        inOrder.verify(stackListener).onElementInserted(eq(stack), eq("bar"), eq(0));
    }

    @Test
    public void insertShouldInsertElementAtIndex() {
        stack.push("foo");
        stack.insert(0, "bar");

        assertEquals(Arrays.asList("bar", "foo"), stack.stream().collect(toList()));
    }

    @Test
    public void tailShouldReturnElementsFromIndex() {
        stack.push("foo");
        stack.push("bar");
        stack.push("baz");

        assertEquals(Arrays.asList("foo", "bar", "baz"), stack.tail(0));
        assertEquals(Arrays.asList("bar", "baz"), stack.tail(1));
        assertEquals(Arrays.asList("baz"), stack.tail(2));
        assertTrue(stack.tail(3).isEmpty());
    }

    @Test
    public void tailShouldFailForInvalidIndex() {
        assertThrown(() -> stack.tail(1), IllegalArgumentException.class);
    }

    @Test
    public void clearShouldRemoveAllElements() {
        stack.push("foo");
        stack.push("bar");
        stack.clear();

        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    public void streamShouldStreamAllElements() {
        stack.push("foo");
        stack.push("bar");
        stack.push("baz");

        final String[] strings = stack.stream().toArray(String[]::new);

        assertArrayEquals(new String[] {"foo", "bar", "baz"}, strings);
    }

    @Test
    public void swapShouldFailIfStackIsEmpty() {
        assertThrown(() -> stack.swap("foo"), EmptyStackException.class);
    }

    @Test
    public void swapShouldNotAcceptNullElement() {
        stack.push("foo");
        assertThrown(() -> stack.swap(null), AssertionError.class);
    }

    @Test
    public void swapShouldReplaceTopElementAndReturnOld() {
        stack.push("bar");
        stack.push("foo");

        assertEquals("foo", stack.swap("baz"));
        assertEquals(Arrays.asList("bar", "baz"), stack.stream().collect(toList()));
    }

    @Test
    public void swapShouldNotifyStackListener() {
        stack.push("foo");
        stack.addStackListener(stackListener);
        stack.swap("bar");

        verify(stackListener).onElementSwapped(eq(stack), eq("foo"), eq("bar"));
    }

    @Test
    public void tailWithNegativeValueShouldReturnValuesFromEnd() {
        stack.push("baz");
        stack.push("bar");
        stack.push("foo");

        assertEquals(Arrays.asList("foo"), stack.tail(-1));
        assertEquals(Arrays.asList("bar", "foo"), stack.tail(-2));
        assertEquals(Arrays.asList("baz", "bar", "foo"), stack.tail(-3));
    }
}