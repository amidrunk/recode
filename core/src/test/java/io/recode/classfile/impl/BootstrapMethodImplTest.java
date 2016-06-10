package io.recode.classfile.impl;

import org.junit.Assert;
import org.junit.Test;
import io.recode.classfile.impl.BootstrapMethodImpl;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BootstrapMethodImplTest {

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new BootstrapMethodImpl(-1, new int[]{0}), AssertionError.class);
        assertThrown(() -> new BootstrapMethodImpl(0, null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final BootstrapMethodImpl method = new BootstrapMethodImpl(1, new int[]{2, 3});

        assertEquals(1, method.getBootstrapMethodRef());
        assertArrayEquals(new int[] {2, 3}, method.getBootstrapArguments());
    }
}
