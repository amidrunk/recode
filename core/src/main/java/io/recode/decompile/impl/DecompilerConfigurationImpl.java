package io.recode.decompile.impl;

import io.recode.decompile.*;
import io.recode.model.*;
import io.recode.classfile.ByteCode;
import io.recode.util.Iterators;
import io.recode.util.Priority;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static io.recode.decompile.DecompilerConfigurationBuilder.ExtendContinuation;
import static io.recode.util.Iterators.collect;
import static io.recode.util.Iterators.empty;
import static io.recode.util.Iterators.filter;

public final class DecompilerConfigurationImpl implements DecompilerConfiguration {

    private final DecompilerDelegateAdapter<DecompilerDelegate>[][] decompilerExtensions;

    private final DecompilerDelegateAdapter<DecompilerDelegate>[][] advisoryDecompilerEnhancements;

    private final DecompilerDelegateAdapter<DecompilerDelegate>[][] correctionalDecompilerEnhancements;

    private final DecompilerDelegateAdapter<ModelTransformation>[][] modelTransformationAdapters;

    private final ModelTransformation[][] modelTransformations;

    private DecompilerConfigurationImpl(DecompilerDelegateAdapter<DecompilerDelegate>[][] decompilerExtensions,
                                        DecompilerDelegateAdapter<DecompilerDelegate>[][] advisoryDecompilerEnhancements,
                                        DecompilerDelegateAdapter<DecompilerDelegate>[][] correctionalDecompilerEnhancements,
                                        DecompilerDelegateAdapter<ModelTransformation>[][] modelTransformationAdapters) {
        this.decompilerExtensions = decompilerExtensions;
        this.advisoryDecompilerEnhancements = advisoryDecompilerEnhancements;
        this.correctionalDecompilerEnhancements = correctionalDecompilerEnhancements;
        this.modelTransformationAdapters = modelTransformationAdapters;
        this.modelTransformations = Arrays.stream(modelTransformationAdapters)
                .map(adapters -> adapters == null ? new ModelTransformation[0] : Arrays.stream(adapters).map(DecompilerDelegateAdapter::getDelegate).toArray(ModelTransformation[]::new))
                .toArray(ModelTransformation[][]::new);
    }

    @Override
    public DecompilerDelegate getDecompilerDelegate(DecompilationContext context, int byteCode) {
        assert context != null : "Decompilation context can't be null";
        assert validByteCode(byteCode) : "Byte code must be in range [0, 255]";

        final DecompilerDelegateAdapter[] candidates = decompilerExtensions[byteCode];

        if (candidates == null) {
            return null;
        }

        for (DecompilerDelegateAdapter adapter : candidates) {
            if (adapter.getDecompilationStateSelector().select(context, byteCode)) {
                return (DecompilerDelegate) adapter.getDelegate();
            }
        }

        return null;
    }

    @Override
    public Iterator<DecompilerDelegate> getAdvisoryDecompilerEnhancements(DecompilationContext context, int byteCode) {
        return selectEnhancements(advisoryDecompilerEnhancements, context, byteCode);
    }

    @Override
    public Iterator<DecompilerDelegate> getCorrectionalDecompilerEnhancements(DecompilationContext context, int byteCode) {
        return selectEnhancements(correctionalDecompilerEnhancements, context, byteCode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ModelTransformation<Element, Element>[] getTransformations(ElementType elementType) {
        return modelTransformations[elementType.ordinal()];
    }

    @Override
    @SuppressWarnings("unchecked")
    public DecompilerConfiguration merge(DecompilerConfiguration other) {
        assert other != null : "Other can't be null";
        assert other instanceof DecompilerConfigurationImpl : "Decompiler configuration type not supported";

        final DecompilerConfigurationImpl secondConfiguration = (DecompilerConfigurationImpl) other;
        final DecompilerConfigurationBuilder mergedConfigurationBuilder = new Builder();

        final Merger merger = (extend, decompilerDelegateAdapters) -> {
            for (DecompilerDelegateAdapter<DecompilerDelegate>[] extensionsForByteCode : decompilerDelegateAdapters) {
                if (extensionsForByteCode != null) {
                    for (DecompilerDelegateAdapter<DecompilerDelegate> extensionForByteCode : extensionsForByteCode) {
                        extend.apply(extensionForByteCode.getByteCode())
                                .withPriority(extensionForByteCode.getPriority())
                                .when(extensionForByteCode.getDecompilationStateSelector())
                                .then(extensionForByteCode.getDelegate());
                    }

                }
            }
        };

        merger.accept(mergedConfigurationBuilder::on, decompilerExtensions);
        merger.accept(mergedConfigurationBuilder::on, secondConfiguration.decompilerExtensions);
        merger.accept(mergedConfigurationBuilder::before, advisoryDecompilerEnhancements);
        merger.accept(mergedConfigurationBuilder::before, secondConfiguration.advisoryDecompilerEnhancements);
        merger.accept(mergedConfigurationBuilder::after, correctionalDecompilerEnhancements);
        merger.accept(mergedConfigurationBuilder::after, secondConfiguration.correctionalDecompilerEnhancements);

        final ElementType[] elementTypes = ElementType.values();

        final Merger modelTransformationMerger = (extend, modelTransformationAdapters) -> {
            for (DecompilerDelegateAdapter[] adapters : modelTransformationAdapters) {
                if (adapters != null) {
                    for (DecompilerDelegateAdapter adapter : adapters) {
                        final ModelQueryTransformation modelQueryTransformation = (ModelQueryTransformation) adapter.getDelegate();

                        mergedConfigurationBuilder.map(elementTypes[adapter.getByteCode()])
                                .forQuery(modelQueryTransformation.getModelQuery())
                                .withPriority(adapter.getPriority())
                                .to(modelQueryTransformation.getTargetTransformation());
                    }
                }
            }
        };

        modelTransformationMerger.accept(mergedConfigurationBuilder::on, modelTransformationAdapters);
        modelTransformationMerger.accept(mergedConfigurationBuilder::on, secondConfiguration.modelTransformationAdapters);

        return mergedConfigurationBuilder.build();
    }

    /**
     * Type declaration of BiConsumer that merges an existing configuration into a builder. Used solely to
     * simplify declaration of closure.
     */
    @FunctionalInterface
    interface Merger extends BiConsumer<Function<Integer, ExtendContinuation>, DecompilerDelegateAdapter[][]> {
    }

    private Iterator<DecompilerDelegate> selectEnhancements(DecompilerDelegateAdapter<DecompilerDelegate>[][] source,
                                                            DecompilationContext context, int byteCode) {
        assert context != null : "Context can't be null";
        assert ByteCode.isValid(byteCode) : "Byte code is not valid";

        final DecompilerDelegateAdapter<DecompilerDelegate>[] enhancements = source[byteCode];

        if (enhancements == null) {
            return empty();
        }

        return collect(filter(Iterators.of(enhancements),
                        adapter -> adapter.getDecompilationStateSelector().select(context, byteCode)),
                DecompilerDelegateAdapter::getDelegate);
    }

    private static boolean validByteCode(int byteCode) {
        return (byteCode & ~0xFF) == 0;
    }

    public static DecompilerConfigurationBuilder newBuilder() {
        return new Builder();
    }

    @SuppressWarnings("unchecked")
    private static class Builder implements DecompilerConfigurationBuilder {

        private final DecompilerDelegateAdapter<DecompilerDelegate>[][] decompilerExtensions = new DecompilerDelegateAdapter[256][];

        private final DecompilerDelegateAdapter<DecompilerDelegate>[][] advisoryDecompilerEnhancements = new DecompilerDelegateAdapter[256][];

        private final DecompilerDelegateAdapter<DecompilerDelegate>[][] correctionalDecompilerEnhancements = new DecompilerDelegateAdapter[256][];

        private final DecompilerDelegateAdapter<ModelTransformation>[][] modelTransformations = new DecompilerDelegateAdapter[ElementType.values().length][];

        public DecompilerConfiguration build() {
            return new DecompilerConfigurationImpl(
                    decompilerExtensions,
                    advisoryDecompilerEnhancements,
                    correctionalDecompilerEnhancements,
                    modelTransformations);
        }

        @Override
        public OnElementTypeContinuation map(ElementType elementType) {
            assert elementType != null : "Element type can't be null";

            return new OnElementTypeContinuation() {
                @Override
                public <R extends Element> ForQueryContinuationWithPriority<R> forQuery(ModelQuery<Element, R> query) {
                    assert query != null : "Query can't be null";

                    return new ForQueryContinuationWithPriority<R>() {

                        private Priority priority = Priority.DEFAULT;

                        @Override
                        public ForQueryContinuationWithoutPriority<R> withPriority(Priority priority) {
                            assert priority != null : "Priority can't be null";
                            this.priority = priority;
                            return this;
                        }

                        @Override
                        public DecompilerConfigurationBuilder to(ModelTransformation<R, ? extends Element> transformation) {
                            assert transformation != null : "Transformation can't be null";

                            final int index = elementType.ordinal();

                            priorityInsert(
                                    modelTransformations,
                                    index,
                                    new DecompilerDelegateAdapter(
                                            index,
                                            priority,
                                            DecompilationStateSelector.ALL,
                                            new ModelQueryTransformation(query, transformation)));

                            return Builder.this;
                        }
                    };
                }
            };
        }

        @Override
        public ExtendContinuation<DecompilerDelegate> before(int byteCode) {
            return new DecompilerExtensionBuilder<>(this, new int[]{byteCode}, advisoryDecompilerEnhancements);
        }

        @Override
        public ExtendContinuation<DecompilerDelegate> before(int... byteCodes) {
            return new DecompilerExtensionBuilder<>(this, byteCodes, advisoryDecompilerEnhancements);
        }

        @Override
        public ExtendContinuation<DecompilerDelegate> after(int byteCode) {
            return new DecompilerExtensionBuilder<>(this, new int[]{byteCode}, correctionalDecompilerEnhancements);
        }

        @Override
        public ExtendContinuation<DecompilerDelegate> after(int... byteCodes) {
            return new DecompilerExtensionBuilder<>(this, byteCodes, correctionalDecompilerEnhancements);
        }

        @Override
        public ExtendContinuation<DecompilerDelegate> on(int startByteCode, int endByteCode) {
            assert endByteCode > startByteCode : "End byte code must be greater than start byte code";
            assert ByteCode.isValid(startByteCode) : "Start byte code is not valid";
            assert ByteCode.isValid(endByteCode) : "End byte code is not valid";

            final int[] byteCodes = new int[endByteCode - startByteCode + 1];

            for (int i = startByteCode; i <= endByteCode; i++) {
                byteCodes[i - startByteCode] = i;
            }

            return new DecompilerExtensionBuilder(this, byteCodes, decompilerExtensions);
        }

        @Override
        public ExtendContinuation<DecompilerDelegate> on(int byteCode) {
            assert ByteCode.isValid(byteCode) : "Byte code is not valid";
            return new DecompilerExtensionBuilder(this, new int[]{byteCode}, decompilerExtensions);
        }

        @Override
        public ExtendContinuation<DecompilerDelegate> on(int... byteCodes) {
            assert byteCodes != null : "Byte codes can't be null";
            assert byteCodes.length > 0 : "Byte codes can't be empty";

            final int[] copy = new int[byteCodes.length];

            for (int i = 0; i < byteCodes.length; i++) {
                assert ByteCode.isValid(byteCodes[i]) : "Byte code is not valid: " + byteCodes[i];

                copy[i] = byteCodes[i];
            }

            return new DecompilerExtensionBuilder<>(this, copy, decompilerExtensions);
        }

        private static class DecompilerExtensionBuilder<T> implements ExtendContinuation<T> {

            private final Builder builder;

            private final int[] byteCodes;

            private final DecompilerDelegateAdapter<T>[][] targetArray;

            private Priority priority = Priority.DEFAULT;

            private DecompilationStateSelector decompilationStateSelector = DecompilationStateSelector.ALL;

            private DecompilerExtensionBuilder(Builder builder, int[] indexes, DecompilerDelegateAdapter<T>[][] targetArray) {
                this.builder = builder;
                this.byteCodes = indexes;
                this.targetArray = targetArray;
            }

            @Override
            public WithPriorityContinuation<T> withPriority(Priority priority) {
                this.priority = priority;
                return this;
            }

            @Override
            public WhenContinuation<T> when(DecompilationStateSelector selector) {
                this.decompilationStateSelector = selector;
                return this;
            }

            @Override
            public DecompilerConfigurationBuilder then(T extension) {
                for (int index : byteCodes) {
                    priorityInsert(targetArray, index, new DecompilerDelegateAdapter<>(index, priority, decompilationStateSelector, extension));
                }

                return builder;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void priorityInsert(DecompilerDelegateAdapter<T>[][] array, int index, DecompilerDelegateAdapter<T> adapter) {
        final DecompilerDelegateAdapter<T>[] existingExtensions = array[index];

        if (existingExtensions == null) {
            array[index] = new DecompilerDelegateAdapter[]{adapter};
        } else {
            final DecompilerDelegateAdapter<T>[] newExtensions = Arrays.copyOf(
                    existingExtensions,
                    existingExtensions.length + 1,
                    DecompilerDelegateAdapter[].class);

            for (int i = 0; i < newExtensions.length; i++) {
                if (newExtensions[i] == null || adapter.getPriority().ordinal() > newExtensions[i].getPriority().ordinal()) {
                    System.arraycopy(newExtensions, i, newExtensions, i + 1, newExtensions.length - 1 - i);
                    newExtensions[i] = adapter;
                    break;
                }
            }

            array[index] = newExtensions;
        }
    }

}
