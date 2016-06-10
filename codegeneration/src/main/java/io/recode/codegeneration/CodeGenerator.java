package io.recode.codegeneration;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

@FunctionalInterface
public interface CodeGenerator<T> {

    void generateCode(T instance, PrintWriter out);

    default String generateCode(T instance, Charset charset) {
        assert instance != null;
        assert charset != null;

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter out = new PrintWriter(baos);

        generateCode(instance, out);

        out.flush();

        return new String(baos.toByteArray(), charset);
    }
}
