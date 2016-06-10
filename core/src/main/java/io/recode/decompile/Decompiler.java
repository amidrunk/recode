package io.recode.decompile;

import io.recode.classfile.CodeAttribute;
import io.recode.classfile.Method;
import io.recode.decompile.impl.InputStreamCodeStream;
import io.recode.model.Element;

import java.io.IOException;
import java.io.InputStream;

public interface Decompiler {

    // TODO: Why on earth is the code stream provided here when the code is available in the method? Better
    // to provide line numbers in that case.
    Element[] parse(Method method, CodeStream codeStream) throws IOException;

    Element[] parse(Method method, CodeStream codeStream, DecompilationProgressCallback callback) throws IOException;

    default Element[] decompile(Method method) throws IOException {
        final CodeAttribute code = method.getCode();

        try (final InputStream in = code.getCode()) {
            return parse(method, new InputStreamCodeStream(in));
        }
    }

}
