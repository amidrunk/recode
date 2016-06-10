package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Goto;
import org.junit.Test;
import org.mockito.Mockito;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class GotoImplTest {

    private final Goto exampleElement = new GotoImpl(1234);

    @Test
    public void constructorShouldNotAcceptNegativeProgramCounter() {
        assertThrown(() -> new GotoImpl(-1), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        assertEquals(1234, exampleElement.getTargetProgramCounter());
        assertEquals(ElementType.GOTO, exampleElement.getElementType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleElement, exampleElement);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleElement.equals(null));
        assertFalse(exampleElement.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Goto other = new GotoImpl(1234);

        assertEquals(other, exampleElement);
        assertEquals(other.hashCode(), exampleElement.hashCode());
    }

    @Test
    public void gotoWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = Mockito.mock(ElementMetaData.class);

        assertNotNull(exampleElement.getMetaData());
        assertEquals(metaData, new GotoImpl(1234, metaData).getMetaData());
    }

}