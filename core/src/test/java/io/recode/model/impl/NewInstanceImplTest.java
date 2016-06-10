package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.Expression;
import io.recode.model.Signature;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class NewInstanceImplTest {

    private final Signature exampleSignature = mock(Signature.class);
    private final Expression exampleParameter = mock(Expression.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new NewInstanceImpl(null, exampleSignature, Collections.<Expression>emptyList()), AssertionError.class);
        assertThrown(() -> new NewInstanceImpl(mock(Type.class), null, Collections.<Expression>emptyList()), AssertionError.class);
        assertThrown(() -> new NewInstanceImpl(mock(Type.class), exampleSignature, null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final NewInstanceImpl newInstance = new NewInstanceImpl(String.class, exampleSignature, Arrays.asList(exampleParameter));

        assertEquals(String.class, newInstance.getType());
        assertEquals(exampleSignature, newInstance.getConstructorSignature());
        assertArrayEquals(new Object[]{exampleParameter}, newInstance.getParameters().toArray());
    }

    @Test
    public void newInstanceElementShouldBeEqualToItSelf() {
        final NewInstanceImpl e = new NewInstanceImpl(String.class, exampleSignature, Collections.<Expression>emptyList());

        assertEquals(e, e);
        assertEquals(e.hashCode(), e.hashCode());
    }

    @Test
    public void newInstanceShouldNotBeEqualToNullOrIncorrectType() {
        final NewInstanceImpl it = new NewInstanceImpl(String.class, exampleSignature, Collections.<Expression>emptyList());

        assertFalse(it.equals(null));
        assertFalse(it.equals("foo"));
    }

    @Test
    public void newInstanceElementsWithEqualPropertiesShouldBeEqual() {
        final NewInstanceImpl newInstance1 = new NewInstanceImpl(String.class, exampleSignature, Collections.<Expression>emptyList());
        final NewInstanceImpl newInstance2 = new NewInstanceImpl(String.class, exampleSignature, Collections.<Expression>emptyList());

        assertEquals(newInstance2, newInstance1);
        assertEquals(newInstance2.hashCode(), newInstance1.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        final NewInstanceImpl it = new NewInstanceImpl(String.class, exampleSignature, Arrays.asList(exampleParameter));

        assertTrue(it.toString().contains(String.class.getName()));
        assertTrue(it.toString().contains(exampleSignature.toString()));
        assertTrue(it.toString().contains(exampleParameter.toString()));
    }

    @Test
    public void elementWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new NewInstanceImpl(String.class, exampleSignature, Collections.emptyList()).getMetaData());
        assertEquals(metaData, new NewInstanceImpl(String.class, exampleSignature, Collections.emptyList(), metaData).getMetaData());
    }

}
