package io.recode.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class ByteBufferInputStream extends InputStream {

    private final ByteBuffer byteBuffer;

    public ByteBufferInputStream(ByteBuffer byteBuffer) {
        assert byteBuffer != null : "Byte buffer can't be null";
        this.byteBuffer = byteBuffer;
    }

    @Override
    public int read() {
        if (byteBuffer.remaining() == 0) {
            return -1;
        }

        return byteBuffer.get();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        assert b != null : "Buffer can't be null";
        assert off >= 0 : "Offset must be positive";
        assert len >= 0 : "Length must be positive";

        if (!byteBuffer.hasRemaining()) {
            return -1;
        }

        int bytesRead = 0;

        for (int i = 0; i < len && byteBuffer.hasRemaining(); i++) {
            b[i] = byteBuffer.get();
            bytesRead++;
        }

        return bytesRead;
    }

    @Override
    public int available() {
        return byteBuffer.remaining();
    }

    @Override
    public long skip(long n) throws IOException {
        final int requestedNewPosition = byteBuffer.position() + (int) n;
        final int actualNewPosition = Math.min(requestedNewPosition, byteBuffer.capacity());

        byteBuffer.position(actualNewPosition);

        return n - (requestedNewPosition - actualNewPosition);
    }
}
