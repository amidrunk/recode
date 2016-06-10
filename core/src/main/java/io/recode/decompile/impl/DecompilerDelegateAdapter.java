package io.recode.decompile.impl;

import io.recode.decompile.DecompilationStateSelector;
import io.recode.util.Priority;

public final class DecompilerDelegateAdapter<T> {

    private final int byteCode;

    private final Priority priority;

    private final DecompilationStateSelector decompilationStateSelector;

    private final T delegate;

    public DecompilerDelegateAdapter(int byteCode, Priority priority, DecompilationStateSelector decompilationStateSelector, T delegate) {
        assert priority != null : "Priority can't be null";
        assert decompilationStateSelector != null : "Decompilation state selector can't be null";
        assert delegate != null : "Delegate can't be null";

        this.byteCode = byteCode;
        this.priority = priority;
        this.decompilationStateSelector = decompilationStateSelector;
        this.delegate = delegate;
    }

    public int getByteCode() {
        return byteCode;
    }

    public Priority getPriority() {
        return priority;
    }

    public DecompilationStateSelector getDecompilationStateSelector() {
        return decompilationStateSelector;
    }

    public T getDelegate() {
        return delegate;
    }
}
