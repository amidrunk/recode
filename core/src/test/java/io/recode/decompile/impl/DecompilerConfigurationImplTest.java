package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.decompile.*;
import io.recode.model.*;
import io.recode.util.Iterators;
import io.recode.util.Priority;
import io.recode.util.Range;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static io.recode.model.AST.constant;
import static io.recode.model.ModelQueries.runtimeType;
import static io.recode.model.ModelQueries.value;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class DecompilerConfigurationImplTest {

    private final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();
    private final DecompilerConfiguration emptyConfiguration = builder.build();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final DecompilerDelegate extension1 = mock(DecompilerDelegate.class, "extension1");
    private final CodeStream codeStream = mock(CodeStream.class);
    private final DecompilerDelegate extension2 = mock(DecompilerDelegate.class, "extension2");
    private final DecompilerDelegate enhancement1 = mock(DecompilerDelegate.class, "enhancement1");
    private final DecompilerDelegate enhancement2 = mock(DecompilerDelegate.class, "enhancement2");

    @Test
    public void getDecompilerExtensionShouldFailForInvalidArguments() {
        assertThrown(() -> emptyConfiguration.getDecompilerDelegate(null, 1), AssertionError.class);
        assertThrown(() -> emptyConfiguration.getDecompilerDelegate(decompilationContext, -1), AssertionError.class);
        assertThrown(() -> emptyConfiguration.getDecompilerDelegate(decompilationContext, 257), AssertionError.class);
    }

    @Test
    public void emptyConfigurationShouldReturnNullExtensionForAllByteCodes() {
        for (int i = 0; i < 256; i++) {
            assertNull(emptyConfiguration.getDecompilerDelegate(decompilationContext, i));
        }
    }

    @Test
    public void configurationCanHaveSingleExtensionForByteCode() throws IOException {
        final DecompilerConfiguration configuration = builder
                .on(ByteCode.nop).then(extension1)
                .build();

        final DecompilerDelegate extension = configuration.getDecompilerDelegate(decompilationContext, ByteCode.nop);

        extension.apply(decompilationContext, codeStream, ByteCode.nop);

        verify(extension1).apply(eq(decompilationContext), eq(codeStream), eq(ByteCode.nop));
    }

    @Test
    public void onShouldNotAcceptInvalidArguments() {
        assertThrown(() -> builder.on(-1), AssertionError.class);
        assertThrown(() -> builder.on(257), AssertionError.class);
        assertThrown(() -> builder.on(100).then(null), AssertionError.class);
    }

    @Test
    public void multipleExtensionsCanExistsForTheSameByteCode() throws IOException {
        final DecompilationStateSelector selector1 = mock(DecompilationStateSelector.class);
        final DecompilationStateSelector selector2 = mock(DecompilationStateSelector.class);
        final DecompilerConfiguration configuration = builder
                .on(ByteCode.nop).when(selector1).then(extension1)
                .on(ByteCode.nop).when(selector2).then(extension2)
                .build();

        when(selector1.select(eq(decompilationContext), eq(ByteCode.nop))).thenReturn(false);
        when(selector2.select(eq(decompilationContext), eq(ByteCode.nop))).thenReturn(true);

        assertEquals(extension2, configuration.getDecompilerDelegate(decompilationContext, ByteCode.nop));

        verify(selector1).select(eq(decompilationContext), eq(ByteCode.nop));
        verify(selector2).select(eq(decompilationContext), eq(ByteCode.nop));
    }

    @Test
    public void extendByteCodeRangeShouldNotAcceptInvalidRange() {
        assertThrown(() -> builder.on(1, 0), AssertionError.class);
        assertThrown(() -> builder.on(1, 1), AssertionError.class);
    }

    @Test
    public void byteCodeRangeCanBeExtended() throws IOException {
        final DecompilerDelegate extension = mock(DecompilerDelegate.class);
        final DecompilerConfiguration configuration = builder
                .on(ByteCode.iconst_0, ByteCode.iconst_5).then(extension)
                .build();

        final List<Integer> byteCodes = Arrays.asList(
                ByteCode.iconst_0,
                ByteCode.iconst_1,
                ByteCode.iconst_2,
                ByteCode.iconst_3,
                ByteCode.iconst_4,
                ByteCode.iconst_5);

        for (Integer byteCode : byteCodes) {
            final DecompilerDelegate actualExtension = configuration.getDecompilerDelegate(decompilationContext, byteCode);

            assertEquals(extension, actualExtension);
        }
    }

    @Test
    public void decompilerExtensionWithoutPriorityAndPredicateAndBeConfigured() {
        final DecompilerConfiguration it = builder.on(ByteCode.nop).then(extension1).build();

        assertEquals(extension1, it.getDecompilerDelegate(decompilationContext, ByteCode.nop));
    }

    @Test
    public void decompilerExtensionWithPriorityAndNoPredicateCanBeConfigured() {
        final DecompilerConfiguration it = builder.on(ByteCode.nop).withPriority(Priority.HIGH).then(extension1).build();

        assertEquals(extension1, it.getDecompilerDelegate(decompilationContext, ByteCode.nop));
    }

    @Test
    public void decompilerExtensionWithPriorityAndPredicateCanBeConfigured() {
        final DecompilationStateSelector selector = mock(DecompilationStateSelector.class);
        final DecompilerConfiguration it = builder.on(ByteCode.nop).withPriority(Priority.HIGH).when(selector).then(extension1).build();

        when(selector.select(eq(decompilationContext), eq(ByteCode.nop))).thenReturn(true);
        assertEquals(extension1, it.getDecompilerDelegate(decompilationContext, ByteCode.nop));
    }

    @Test
    public void multipleMatchingDecompilerExtensionsWithDifferentPrioritiesCanBeConfigured() {
        final DecompilerConfiguration configuration = builder
                .on(ByteCode.nop).withPriority(Priority.HIGH).then(extension1)
                .on(ByteCode.nop).withPriority(Priority.HIGHER).then(extension2)
                .build();

        assertEquals(extension2, configuration.getDecompilerDelegate(decompilationContext, ByteCode.nop));
    }

    @Test
    public void onByteCodesShouldNotAcceptInvalidArguments() {
        assertThrown(() -> builder.on(), AssertionError.class);
        assertThrown(() -> builder.on((int[]) null), AssertionError.class);
        assertThrown(() -> builder.on(1234), AssertionError.class);
    }

    @Test
    public void onByteCodesShouldSetupExtensionsForByteCodes() {
        final Range range = Range.from(1).to(3);
        final DecompilerConfiguration it = builder.on(range.all()).then(extension1).build();

        range.each(byteCode -> assertNotNull(it.getDecompilerDelegate(decompilationContext, byteCode)));
    }

    @Test
    public void getAdvisoryDecompilerEnhancementsShouldNotAcceptInvalidArguments() {
        final DecompilerConfiguration configuration = builder.build();

        assertThrown(() -> configuration.getAdvisoryDecompilerEnhancements(null, 1), AssertionError.class);
    }

    @Test
    public void getAdvisoryDecompilerEnhancementsShouldReturnEmptyIteratorIfNoMatchingEnhancementExists() {
        final DecompilerConfiguration configuration = builder.build();

        assertFalse(configuration.getAdvisoryDecompilerEnhancements(decompilationContext, 1).hasNext());
    }

    @Test
    public void getAdvisoryDecompilerEnhancementsShouldReturnConfiguredEnhancementsInPriorityOrder() {
        final DecompilerConfiguration configuration = builder
                .before(ByteCode.nop).withPriority(Priority.DEFAULT).then(enhancement1)
                .before(ByteCode.nop).withPriority(Priority.HIGH).then(enhancement2)
                .build();

        final Iterator<DecompilerDelegate> iterator = configuration.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.nop);

        assertEquals(Arrays.asList(enhancement2, enhancement1), Iterators.toList(iterator));
    }

    @Test
    public void getCorrectionalDecompilerEnhancementsShouldNotAcceptInvalidArguments() {
        final DecompilerConfiguration configuration = builder.build();

        assertThrown(() -> configuration.getCorrectionalDecompilerEnhancements(null, 0), AssertionError.class);
        assertThrown(() -> configuration.getCorrectionalDecompilerEnhancements(decompilationContext, -1), AssertionError.class);
    }

    @Test
    public void getCorrectionalDecompilerEnhancementsShouldReturnEmptyIteratorIfNoMatchesExist() {
        assertFalse(builder.build().getCorrectionalDecompilerEnhancements(decompilationContext, 1).hasNext());
    }

    @Test
    public void getCorrectionalDecompilerEnhancementsShouldReturnMatchingCorrectors() {
        final DecompilerConfiguration configuration = builder
                .after(ByteCode.nop).withPriority(Priority.DEFAULT).then(enhancement1)
                .after(ByteCode.nop).withPriority(Priority.HIGH).then(enhancement2)
                .build();

        final Iterator<DecompilerDelegate> iterator = configuration.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.nop);

        assertEquals(Arrays.asList(enhancement2, enhancement1), Iterators.toList(iterator));
    }

    @Test
    public void mergeShouldNotAcceptNullArg() {
        assertThrown(() -> builder.build().merge(null), AssertionError.class);
    }

    @Test
    public void mergeShouldNotAcceptIncorrectType() {
        assertThrown(() -> builder.build().merge(mock(DecompilerConfiguration.class)), AssertionError.class);
    }

    @Test
    public void mergeShouldCreateUnionOfNonIntersectingExtensions() {
        final DecompilerConfiguration configuration1 = DecompilerConfigurationImpl.newBuilder()
                .on(ByteCode.iadd).then(extension1)
                .build();

        final DecompilerConfiguration configuration2 = DecompilerConfigurationImpl.newBuilder()
                .on(ByteCode.isub).then(extension2)
                .build();

        final DecompilerConfiguration mergedConfiguration = configuration1.merge(configuration2);

        assertEquals(extension1, mergedConfiguration.getDecompilerDelegate(decompilationContext, ByteCode.iadd));
        assertEquals(extension2, mergedConfiguration.getDecompilerDelegate(decompilationContext, ByteCode.isub));
    }

    @Test
    public void mergeShouldOrganizeEqualExtensionsInPriorityOrder() {
        final DecompilerConfiguration configuration1 = DecompilerConfigurationImpl.newBuilder()
                .on(ByteCode.iadd).then(extension1)
                .build();

        final DecompilerConfiguration configuration2 = DecompilerConfigurationImpl.newBuilder()
                .on(ByteCode.iadd).then(extension2)
                .build();

        final DecompilerConfiguration mergedConfiguration1 = configuration1.merge(configuration2);

        assertEquals(extension1, mergedConfiguration1.getDecompilerDelegate(decompilationContext, ByteCode.iadd));

        final DecompilerConfiguration mergedConfiguration2 = configuration2.merge(configuration1);

        assertEquals(extension2, mergedConfiguration2.getDecompilerDelegate(decompilationContext, ByteCode.iadd));
    }

    @Test
    public void mergeShouldMergeAdvisoryEnhancements() {
        final DecompilerConfiguration configuration1 = DecompilerConfigurationImpl.newBuilder()
                .before(ByteCode.iadd).then(enhancement1)
                .build();

        final DecompilerConfiguration configuration2 = DecompilerConfigurationImpl.newBuilder()
                .before(ByteCode.isub).then(enhancement2)
                .build();

        final DecompilerConfiguration mergedConfiguration = configuration1.merge(configuration2);

        assertEquals(Arrays.asList(enhancement1), Iterators.toList(mergedConfiguration.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.iadd)));
        assertEquals(Arrays.asList(enhancement2), Iterators.toList(mergedConfiguration.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.isub)));
    }

    @Test
    public void mergeShouldOrganizeOverlappingAdvisoryEnhancementsInPriorityOrder() {
        final DecompilerConfiguration configuration1 = DecompilerConfigurationImpl.newBuilder()
                .before(ByteCode.iadd).then(enhancement1)
                .build();

        final DecompilerConfiguration configuration2 = DecompilerConfigurationImpl.newBuilder()
                .before(ByteCode.iadd).then(enhancement2)
                .build();

        final DecompilerConfiguration mergedConfiguration1 = configuration1.merge(configuration2);

        assertEquals(Arrays.asList(enhancement1, enhancement2), Iterators.toList(mergedConfiguration1.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.iadd)));

        final DecompilerConfiguration mergedConfiguration2 = configuration2.merge(configuration1);

        assertEquals(Arrays.asList(enhancement2, enhancement1), Iterators.toList(mergedConfiguration2.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.iadd)));
    }

    @Test
    public void mergeShouldMergeCorrectionalEnhancements() {
        final DecompilerConfiguration configuration1 = DecompilerConfigurationImpl.newBuilder()
                .after(ByteCode.iadd).then(enhancement1)
                .build();

        final DecompilerConfiguration configuration2 = DecompilerConfigurationImpl.newBuilder()
                .after(ByteCode.isub).then(enhancement2)
                .build();

        final DecompilerConfiguration it = configuration1.merge(configuration2);

        assertEquals(Arrays.asList(enhancement1), Iterators.toList(it.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iadd)));
        assertEquals(Arrays.asList(enhancement2), Iterators.toList(it.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.isub)));
    }

    @Test
    public void mergeShouldOrganizeOverlappingCorrectionalEnhancementsInPriorityOrder() {
        final DecompilerConfiguration configuration1 = DecompilerConfigurationImpl.newBuilder()
                .after(ByteCode.iadd).then(enhancement1)
                .build();

        final DecompilerConfiguration configuration2 = DecompilerConfigurationImpl.newBuilder()
                .after(ByteCode.iadd).then(enhancement2)
                .build();

        final DecompilerConfiguration mergedConfiguration1 = configuration1.merge(configuration2);

        assertEquals(Arrays.asList(enhancement1, enhancement2), Iterators.toList(mergedConfiguration1.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iadd)));

        final DecompilerConfiguration mergedConfiguration2 = configuration2.merge(configuration1);

        assertEquals(Arrays.asList(enhancement2, enhancement1), Iterators.toList(mergedConfiguration2.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iadd)));
    }

    @Test
    public void advisoryDecompilerEnhancementCanBeConfiguredForMultipleInstructions() {
        final DecompilerConfiguration configuration = DecompilerConfigurationImpl.newBuilder()
                .before(ByteCode.nop, ByteCode.dup).then(enhancement1)
                .build();

        assertEquals(Arrays.asList(enhancement1), Iterators.toList(configuration.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.nop)));
        assertEquals(Arrays.asList(enhancement1), Iterators.toList(configuration.getAdvisoryDecompilerEnhancements(decompilationContext, ByteCode.dup)));
    }

    @Test
    public void correctionalDecompilerEnhancementsCanBeConfiguredForMultipleInstructions() {
        final DecompilerConfiguration configuration = DecompilerConfigurationImpl.newBuilder()
                .after(ByteCode.nop, ByteCode.dup).then(enhancement1)
                .build();

        assertEquals(Arrays.asList(enhancement1), Iterators.toList(configuration.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.nop)));
        assertEquals(Arrays.asList(enhancement1), Iterators.toList(configuration.getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.dup)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void singleTransformationForElementTypeCanBeConfigured() {
        final ModelTransformation expectedTransformation = mock(ModelTransformation.class);

        final DecompilerConfiguration configuration = DecompilerConfigurationImpl.newBuilder()
                .map(ElementType.CONSTANT).forQuery(value().where(runtimeType().is(ModelQueries.equalTo(String.class)))).to(expectedTransformation)
                .build();

        final ModelTransformation<Element, Element>[] it = configuration.getTransformations(ElementType.CONSTANT);

        assertEquals(1, it.length);

        final ModelTransformation<Element, Element> transformation = it[0];
        final Constant input = constant("foo");
        final Constant output = constant("bar");

        when(expectedTransformation.apply(eq(input))).thenReturn(Optional.of(output));

        final Optional<Element> element = transformation.apply(input);

        assertEquals(Optional.of(output), element);
        verify(expectedTransformation).apply(eq(input));

        assertFalse(transformation.apply(constant(1)).isPresent());
        verifyNoMoreInteractions(expectedTransformation);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void transformationsShouldBeAppliedInPriorityOrder() {
        final ModelTransformation transformation1 = mock(ModelTransformation.class, withSettings().defaultAnswer(a -> Optional.of(a.getArguments()[0])));
        final ModelTransformation transformation2 = mock(ModelTransformation.class, withSettings().defaultAnswer(a -> Optional.of(a.getArguments()[0])));

        final DecompilerConfiguration configuration = DecompilerConfigurationImpl.newBuilder()
                .map(ElementType.CONSTANT).forQuery(value()).to(transformation1)
                .map(ElementType.CONSTANT).forQuery(value()).withPriority(Priority.HIGH).to(transformation2)
                .build();

        final Constant element = constant(1);

        applyTransformations(configuration, element);

        verify(transformation2).apply(eq(element));
        verifyZeroInteractions(transformation1);
    }

    @Test
    public void modelTransformationsShouldBeRetainedInPriorityOrderOnMerge() {
        final ModelTransformation transformation1 = mock(ModelTransformation.class, withSettings().name("t1").defaultAnswer(a -> Optional.of(a.getArguments()[0])));
        final ModelTransformation transformation2 = mock(ModelTransformation.class, withSettings().name("t2").defaultAnswer(a -> Optional.of(a.getArguments()[0])));

        final DecompilerConfiguration configuration1 = DecompilerConfigurationImpl.newBuilder()
                .map(ElementType.CONSTANT).forQuery(value()).to(transformation2)
                .build();

        final DecompilerConfiguration configuration2 = DecompilerConfigurationImpl.newBuilder()
                .map(ElementType.CONSTANT).forQuery(value()).withPriority(Priority.HIGH).to(transformation1)
                .build();

        final Element element = AST.constant(1);

        applyTransformations(configuration1.merge(configuration2), element);

        verify(transformation1).apply(eq(element));
        verifyNoMoreInteractions(transformation1);
        verifyZeroInteractions(transformation2);
        reset(transformation1, transformation2);

        applyTransformations(configuration2.merge(configuration1), element);

        verify(transformation1).apply(eq(element));
        verifyNoMoreInteractions(transformation1);
        verifyZeroInteractions(transformation2);
    }

    @Test
    public void modelQueryConfigurationShouldNotAcceptInvalidArguments() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();

        assertThrown(() -> builder.map((ElementType) null), AssertionError.class);
        assertThrown(() -> builder.map(ElementType.CONSTANT).forQuery(null), AssertionError.class);
        assertThrown(() -> builder.map(ElementType.CONSTANT).forQuery(mock(ModelQuery.class)).withPriority(null), AssertionError.class);
        assertThrown(() -> builder.map(ElementType.CONSTANT).forQuery(mock(ModelQuery.class)).withPriority(Priority.DEFAULT).to(null), AssertionError.class);
        assertThrown(() -> builder.map(ElementType.CONSTANT).forQuery(mock(ModelQuery.class)).to(null), AssertionError.class);
    }

    @SuppressWarnings("unchecked")
    private Optional<Element> applyTransformations(DecompilerConfiguration configuration, Element element) {
        for (ModelTransformation transformation : configuration.getTransformations(element.getElementType())) {
            final Optional result = transformation.apply(element);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }
}
