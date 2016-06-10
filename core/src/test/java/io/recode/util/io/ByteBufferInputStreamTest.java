package io.recode.util.io;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteBufferInputStreamTest {

    @Test
    public void constructorShouldNotAcceptNullByteBuffer() {
        assertThrown(() -> new ByteBufferInputStream(null), AssertionError.class);
    }

    @Test
    public void entireByteBufferCanBeRead() throws Exception {
        final byte[] originalArray = {1, 2, 3, 4};
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(originalArray));

        assertArrayEquals(originalArray, IOUtils.toByteArray(in));
    }

    @Test
    public void availableShouldReturnRemainingNumberOfBytes() {
        final byte[] originalArray = {1, 2, 3, 4};
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(originalArray));

        assertEquals(4, in.available());
        assertEquals(1, in.read());
        assertEquals(3, in.available());
        assertEquals(2, in.read());
    }

    @Test
    public void skipShouldSkipBytes() throws IOException {
        final byte[] originalArray = {1, 2, 3, 4, 5};
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(originalArray));

        assertEquals(1L, in.skip(1));
        assertEquals(4, in.available());
        assertEquals(2, in.read());
        assertEquals(3, in.available());
        assertEquals(2L, in.skip(2));
        assertEquals(1, in.available());
        assertEquals(5, in.read());
        assertEquals(0L, in.skip(10));
        assertEquals(0, in.available());
    }

    @Test
    public void readBufferShouldNotAcceptInvalidParameters() throws Exception {
        final ByteBufferInputStream in = new ByteBufferInputStream(ByteBuffer.wrap(new byte[]{1, 2}));

        assertThrown(() -> in.read(null, 0, 1), AssertionError.class);
        assertThrown(() -> in.read(new byte[1], -1, 1), AssertionError.class);
        assertThrown(() -> in.read(new byte[1], 0, -1), AssertionError.class);
    }
}
