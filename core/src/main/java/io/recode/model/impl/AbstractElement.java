package io.recode.model.impl;

import io.recode.model.Element;
import io.recode.model.ElementMetaData;
import io.recode.model.EmptyElementMetaData;

public abstract class AbstractElement implements Element {

    private final ElementMetaData elementMetaData;

    protected AbstractElement() {
        this(null);
    }

    protected AbstractElement(ElementMetaData elementMetaData) {
        this.elementMetaData = (elementMetaData == null ? EmptyElementMetaData.EMPTY : elementMetaData);
    }

    @Override
    public ElementMetaData getMetaData() {
        return elementMetaData;
    }
}
