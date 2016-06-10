package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import org.junit.Test;
import org.mockito.Mockito;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocalVariableReferenceImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new LocalVariableReferenceImpl(null, String.class, 1), AssertionError.class);
        assertThrown(() -> new LocalVariableReferenceImpl("", String.class, 1), AssertionError.class);
        assertThrown(() -> new LocalVariableReferenceImpl("this", null, 1), AssertionError.class);
        assertThrown(() -> new LocalVariableReferenceImpl("this", String.class, -1), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        final LocalVariableReferenceImpl thisVar = new LocalVariableReferenceImpl("this", Object.class, 1234);

        assertEquals(ElementType.VARIABLE_REFERENCE, thisVar.getElementType());
        assertEquals(Object.class, thisVar.getType());
        assertEquals("this", thisVar.getName());
        assertEquals(1234, thisVar.getIndex());
    }

    @Test
    public void localVariableReferenceWithMetaDataCanBeCreated() {
        final ElementMetaData elementMetaData = Mockito.mock(ElementMetaData.class);

        assertNotNull(new LocalVariableReferenceImpl("foo", int.class, 1).getMetaData());
        assertEquals(elementMetaData, new LocalVariableReferenceImpl("foo", int.class, 1, elementMetaData).getMetaData());
    }
}
