package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import org.junit.Test;

import java.util.function.Supplier;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DefaultModelFactoryTest {

    @Test
    public void constructorShouldNotAcceptNullMetaDataSupplier() {
        assertThrown(() -> new DefaultModelFactory(null), AssertionError.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createElementMetaDataShouldReturnMetaDataFromSupplier() {
        final Supplier<ElementMetaData> supplier = mock(Supplier.class);
        final ElementMetaData metaData = mock(ElementMetaData.class);

        when(supplier.get()).thenReturn(metaData);

        final DefaultModelFactory modelFactory = new DefaultModelFactory(supplier);

        assertEquals(metaData, modelFactory.createElementMetaData());
        verify(supplier).get();
    }

}