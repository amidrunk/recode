package io.recode.codegeneration;

import io.recode.decompile.CodePointer;
import io.recode.model.Element;

import java.io.PrintWriter;

@FunctionalInterface
public interface CodeGeneratorDelegate<E extends Element> {

    void apply(CodeGenerationContext context, CodePointer<E> codePointer, PrintWriter out);

}
