package io.recode.codegeneration;

import io.recode.decompile.CodePointer;
import io.recode.model.Element;
import io.recode.model.ElementType;

import java.util.function.Predicate;

public interface ElementSelector<E extends Element> {

    ElementType getElementType();

    boolean matches(CodePointer<E> codePointer);

    static <E extends Element> ElementSelector<E> forType(ElementType elementType) {
        assert elementType != null : "Element type can't be null";

        return new ElementSelector<E>() {
            @Override
            public ElementType getElementType() {
                return elementType;
            }

            @Override
            public boolean matches(CodePointer<E> codePointer) {
                assert codePointer != null : "Code pointer can't be null";
                return codePointer.getElement().getElementType() == elementType;
            }
        };
    }

    default ElementSelector<E> where(Predicate<CodePointer<E>> predicate) {
        assert predicate != null : "Predicate can't be null";

        return new ElementSelector<E>() {
            @Override
            public ElementType getElementType() {
                return ElementSelector.this.getElementType();
            }

            @Override
            public boolean matches(CodePointer<E> codePointer) {
                return ElementSelector.this.matches(codePointer) && predicate.test(codePointer);
            }
        };
    }

}
