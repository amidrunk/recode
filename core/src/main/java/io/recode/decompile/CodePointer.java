package io.recode.decompile;

import io.recode.classfile.Method;
import io.recode.model.Element;

/**
 * A reference to a particular syntax element in the syntax tree in the context of a particular method.
 */
public interface CodePointer<E extends Element> {

    Method getMethod();

    E getElement();

    <C extends Element> CodePointer<C> forElement(C element);

}
