package io.recode.decompile;

import io.recode.model.Element;
import io.recode.model.ElementType;
import io.recode.model.ModelTransformation;

import java.util.Iterator;

public interface DecompilerConfiguration {

    DecompilerDelegate getDecompilerDelegate(DecompilationContext context, int byteCode);

    Iterator<DecompilerDelegate> getAdvisoryDecompilerEnhancements(DecompilationContext context, int byteCode);

    Iterator<DecompilerDelegate> getCorrectionalDecompilerEnhancements(DecompilationContext context, int byteCode);

    ModelTransformation<Element, Element>[] getTransformations(ElementType elementType);

    DecompilerConfiguration merge(DecompilerConfiguration other);

}
