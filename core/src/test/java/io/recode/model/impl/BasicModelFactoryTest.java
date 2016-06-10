package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class BasicModelFactoryTest {

    @Test
    public void createElementMetaDataShouldReturnEmptyMetaData() {
        final BasicModelFactory factory = new BasicModelFactory();
        final ElementMetaData metaData = factory.createElementMetaData();

        assertFalse(metaData.hasLineNumber());
        assertFalse(metaData.hasProgramCounter());
    }

}