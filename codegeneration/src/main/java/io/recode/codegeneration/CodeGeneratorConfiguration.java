package io.recode.codegeneration;

import io.recode.decompile.CodePointer;
import io.recode.model.Element;

import java.util.Iterator;

public interface CodeGeneratorConfiguration {

    CodeGeneratorDelegate<? extends Element> getDelegate(CodeGenerationContext context, CodePointer<? extends Element> codePointer);

    Iterator<CodeGeneratorAdvice<? extends Element>> getAdvices(CodeGenerationContext context, CodePointer<? extends Element> codePointer);

}
