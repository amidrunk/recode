package io.recode.model;

import org.junit.Test;

import java.util.Optional;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ModelQueryTransformationTest {

    private final ModelTransformation modelTransformation = mock(ModelTransformation.class);
    private final ModelQuery modelQuery = mock(ModelQuery.class);
    private final ModelQueryTransformation<Element, Element, Element> modelQueryTransformation = new ModelQueryTransformation(modelQuery, modelTransformation);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new ModelQueryTransformation<>(null, modelTransformation), AssertionError.class);
        assertThrown(() -> new ModelQueryTransformation<>(modelQuery, null), AssertionError.class);
    }

    @Test
    public void applyShouldReturnIfModelQueryReturnsEmptyResult() {
        final Element source = mock(Element.class);

        when(modelQuery.from(eq(source))).thenReturn(Optional.empty());

        assertFalse(modelQueryTransformation.apply(source).isPresent());

        verifyZeroInteractions(modelTransformation);
    }

    @Test
    public void applyShouldReturnTransformedResultIfModelQuerySucceeds() {
        final Element source = mock(Element.class);
        final Element intermediate = mock(Element.class);
        final Element result = mock(Element.class);

        when(modelQuery.from(eq(source))).thenReturn(Optional.of(intermediate));
        when(modelTransformation.apply(eq(intermediate))).thenReturn(Optional.of(result));

        assertEquals(Optional.of(result), modelQueryTransformation.apply(source));
    }
}