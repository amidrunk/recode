package io.recode.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class InputStreamsTest {

    @Test
    public void rangeShouldReturnStreamWithDataWithinRange() throws Exception {
        final InputStream sourceStream = new ByteArrayInputStream("123456789".getBytes());
        final InputStream ranged = InputStreams.range(sourceStream, 3, 7);

        assertEquals(4, ranged.available());
        assertEquals('4', ranged.read());
        assertEquals('5', ranged.read());
        assertEquals('6', ranged.read());
        assertEquals('7', ranged.read());
        assertEquals(-1, ranged.read());
    }

    @Test
    public void rangedStreamCanBeReadIntoBuffer() throws Exception {
        final InputStream sourceStream = new ByteArrayInputStream("123456789".getBytes());
        final InputStream ranged = InputStreams.range(sourceStream, 3, 7);
        final byte[] buffer = new byte[128];

        final int bytesRead = ranged.read(buffer);

        assertEquals(4, bytesRead);
        assertEquals('4', buffer[0]);
        assertEquals('5', buffer[1]);
        assertEquals('6', buffer[2]);
        assertEquals('7', buffer[3]);
        assertEquals(0, buffer[4]);
    }
}