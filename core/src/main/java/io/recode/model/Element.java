package io.recode.model;

public interface Element {

    ElementType getElementType();

    ElementMetaData getMetaData();

    @SuppressWarnings("unchecked")
    default <T extends Element> T as(Class<T> type) {
        assert type != null : "Type can't be null";

        if (!type.isInstance(this)) {
            throw new IllegalArgumentException("Syntax element is not of type " + type.getName() + ": " + this);
        }

        return (T) this;
    }

}
