package io.recode.codegeneration;

import io.recode.decompile.CodePointer;
import io.recode.model.Element;

@FunctionalInterface
public interface CodeGeneratorAdvice<E extends Element> {

    void apply(CodeGenerationContext context, CodePointer<E> codePointer, CodeGeneratorPointcut pointcut);

}
