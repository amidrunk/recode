package io.recode.model.impl;

import io.recode.model.ElementMetaData;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import org.junit.Test;

import java.math.BigDecimal;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FieldReferenceImplTest {

    private final Expression exampleTargetInstance = mock(Expression.class);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new FieldReferenceImpl(exampleTargetInstance, null, getClass(), "foo"), AssertionError.class);
        assertThrown(() -> new FieldReferenceImpl(exampleTargetInstance, getClass(), null, "foo"), AssertionError.class);
        assertThrown(() -> new FieldReferenceImpl(exampleTargetInstance, getClass(), getClass(), null), AssertionError.class);
        assertThrown(() -> new FieldReferenceImpl(exampleTargetInstance, getClass(), getClass(), ""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArgumentsAndInitializeInstance() {
        final FieldReferenceImpl fieldReference = new FieldReferenceImpl(exampleTargetInstance, String.class, int.class, "foo");

        assertTrue(fieldReference.getTargetInstance().isPresent());
        assertEquals(exampleTargetInstance, fieldReference.getTargetInstance().get());
        assertEquals(String.class, fieldReference.getDeclaringType());
        assertEquals(ElementType.FIELD_REFERENCE, fieldReference.getElementType());
        assertEquals("foo", fieldReference.getFieldName());
        assertEquals(int.class, fieldReference.getFieldType());
        assertEquals(int.class, fieldReference.getType());
    }

    @Test
    public void staticFieldReferenceCanBeCreated() {
        final FieldReferenceImpl ref = new FieldReferenceImpl(null, BigDecimal.class, BigDecimal.class, "ZERO");

        assertFalse(ref.getTargetInstance().isPresent());
        assertTrue(ref.isStatic());
    }

    @Test
    public void fieldReferenceWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new FieldReferenceImpl(exampleTargetInstance, String.class, String.class, "foo").getMetaData());
        assertEquals(metaData, new FieldReferenceImpl(exampleTargetInstance, String.class, String.class, "foo", metaData).getMetaData());
    }

}
