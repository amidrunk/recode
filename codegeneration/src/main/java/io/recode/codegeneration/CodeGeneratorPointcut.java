package io.recode.codegeneration;

import io.recode.decompile.CodePointer;

public interface CodeGeneratorPointcut {

    void proceed(CodeGenerationContext context, CodePointer codePointer);

}
