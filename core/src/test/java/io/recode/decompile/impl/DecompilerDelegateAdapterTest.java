package io.recode.decompile.impl;

import io.recode.decompile.impl.DecompilerDelegateAdapter;
import org.junit.Test;
import io.recode.decompile.DecompilationStateSelector;
import io.recode.util.Priority;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DecompilerDelegateAdapterTest {

    private final DecompilationStateSelector decompilationStateSelector = mock(DecompilationStateSelector.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new DecompilerDelegateAdapter<String>(0, null, decompilationStateSelector, "foo"), AssertionError.class);
        assertThrown(() -> new DecompilerDelegateAdapter<String>(0, Priority.DEFAULT, null, "foo"), AssertionError.class);
        assertThrown(() -> new DecompilerDelegateAdapter<String>(0, Priority.DEFAULT, decompilationStateSelector, null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final DecompilerDelegateAdapter<String> it = new DecompilerDelegateAdapter<>(123, Priority.HIGH, decompilationStateSelector, "foo");

        assertEquals(123, it.getByteCode());
        assertEquals(Priority.HIGH, it.getPriority());
        assertEquals(decompilationStateSelector, it.getDecompilationStateSelector());
        assertEquals("foo", it.getDelegate());
    }
}