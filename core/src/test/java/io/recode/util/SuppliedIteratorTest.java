package io.recode.util;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Supplier;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class SuppliedIteratorTest {

    @Test
    public void constructorShouldNotAcceptNullSupplier() {
        assertThrown(() -> new SuppliedIterator<>(null), AssertionError.class);
    }

    @Test
    public void suppliedElementsShouldBeIterated() {
        final Supplier supplier = mock(Supplier.class);

        when(supplier.get()).thenReturn(Optional.of("foo"), Optional.of("bar"), Optional.empty());

        final SuppliedIterator iterator = new SuppliedIterator(supplier);

        assertEquals("foo", iterator.next());
        assertEquals("bar", iterator.next());
        assertFalse(iterator.hasNext());
    }

}