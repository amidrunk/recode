package io.recode.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

public final class SuppliedIterator<T> implements Iterator<T> {

    private final Supplier<Optional<T>> supplier;

    private Optional<T> next;

    private boolean eof;

    public SuppliedIterator(Supplier<Optional<T>> supplier) {
        assert supplier != null : "Supplier can't be null";

        this.supplier = supplier;
    }

    @Override
    public boolean hasNext() {
        if (eof) {
            return false;
        }

        if (next != null) {
            return true;
        }

        next = supplier.get();

        if (!next.isPresent()) {
            eof = true;
            return false;
        }

        return true;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        final T nextElement = next.get();

        next = null;

        return nextElement;
    }

}
