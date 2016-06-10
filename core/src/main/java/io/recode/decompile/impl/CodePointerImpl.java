package io.recode.decompile.impl;

import io.recode.decompile.CodePointer;
import io.recode.classfile.Method;
import io.recode.model.Element;

/**
 * A <code>CodePointer</code> references a particular element in a method. It can be an node in the
 * syntax tree.
 */
public final class CodePointerImpl<E extends Element> implements CodePointer<E> {

    private final Method method;

    private final E element;

    public CodePointerImpl(Method method, E element) {
        assert method != null : "Method can't be null";
        assert element != null : "Element can't be null";

        this.method = method;
        this.element = element;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public E getElement() {
        return element;
    }

    @Override
    public <C extends Element> CodePointer<C> forElement(C element) {
        return new CodePointerImpl<C>(method, element);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodePointerImpl that = (CodePointerImpl) o;

        if (!element.equals(that.element)) return false;
        if (!method.equals(that.method)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + element.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CodePointer{" +
                "method=" + method +
                ", element=" + element +
                '}';
    }
}
