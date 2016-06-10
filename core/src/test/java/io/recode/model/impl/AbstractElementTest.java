package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.EmptyElementMetaData;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AbstractElementTest {

    @Test
    public void elementCanExistWithoutMetaData() {
        final AbstractElement element = new AbstractElement() {
            @Override
            public ElementType getElementType() {
                return null;
            }
        };

        assertTrue(element.getMetaData() instanceof EmptyElementMetaData);
    }

    @Test
    public void elementCanBeCreatedWithMetaData() {
        final ElementMetaData elementMetaData = mock(ElementMetaData.class);
        final AbstractElement element = new AbstractElement(elementMetaData) {
            @Override
            public ElementType getElementType() {
                return null;
            }
        };

        assertSame(elementMetaData, element.getMetaData());
    }
}