package io.recode.decompile;

import java.io.IOException;

// TODO: Not a transformation
public interface DecompilerElementDelegate<R> {

    void apply(DecompilationContext context, CodeStream codeStream, int byteCode, R result) throws IOException;

}
