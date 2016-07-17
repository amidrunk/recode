package io.recode.util;

import java.io.IOException;
import java.io.InputStream;

public class InputStreams {

    public static InputStream range(InputStream in, int from, int to) {
        assert in != null : "in can't be null";
        assert from >= 0 : "from must be positive";
        assert to > from : "to must be greater than from";

        final int bytesToRead = to - from;

        return new InputStream() {

            private int bytesRead;

            @Override
            public int available() throws IOException {
                return Math.min(in.available(), bytesToRead - bytesRead);
            }

            @Override
            public int read() throws IOException {
                if (bytesRead == 0) {
                    in.skip(from);
                }

                if (bytesRead >= bytesToRead) {
                    return -1;
                }

                final int n = in.read();

                if (n != -1) {
                    bytesRead++;
                }

                return n;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                final int limit = Math.min(len, bytesToRead - bytesRead);

                if (bytesRead == 0) {
                    in.skip(from);
                }

                final int bytesReadIntoBuffer = in.read(b, off, limit);

                bytesRead += bytesReadIntoBuffer;

                return bytesReadIntoBuffer;
            }
        };
    }

}
