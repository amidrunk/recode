package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.impl.AbstractModelFactory;

import java.util.function.Supplier;

public final class DefaultModelFactory extends AbstractModelFactory {

    private final Supplier<ElementMetaData> elementMetaDataSupplier;

    public DefaultModelFactory(Supplier<ElementMetaData> elementMetaDataSupplier) {
        assert elementMetaDataSupplier != null : "Meta data supplier can't be null";
        this.elementMetaDataSupplier = elementMetaDataSupplier;
    }

    @Override
    protected ElementMetaData createElementMetaData() {
        return elementMetaDataSupplier.get();
    }
}
