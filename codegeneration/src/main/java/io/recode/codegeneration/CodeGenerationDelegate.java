package io.recode.codegeneration;

import io.recode.decompile.CodePointer;

@FunctionalInterface
public interface CodeGenerationDelegate {

    void delegate(CodeGenerationContext context, CodePointer codePointer);

}
