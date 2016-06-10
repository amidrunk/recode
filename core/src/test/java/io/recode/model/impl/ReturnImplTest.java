package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReturnImplTest {

    @Test
    public void returnValueShouldHaveCorrectElementType() {
        assertEquals(ElementType.RETURN, new ReturnImpl().getElementType());
    }

    @Test
    public void returnWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = Mockito.mock(ElementMetaData.class);

        assertNotNull(new ReturnImpl().getMetaData());
        assertEquals(metaData, new ReturnImpl(metaData).getMetaData());
    }

}
