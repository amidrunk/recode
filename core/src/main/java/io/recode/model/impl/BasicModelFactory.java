package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.EmptyElementMetaData;
import io.recode.model.impl.AbstractModelFactory;

public final class BasicModelFactory extends AbstractModelFactory {
    @Override
    protected ElementMetaData createElementMetaData() {
        return EmptyElementMetaData.EMPTY;
    }
}
