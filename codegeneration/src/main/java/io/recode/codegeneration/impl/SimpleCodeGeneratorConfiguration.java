package io.recode.codegeneration.impl;

import io.recode.codegeneration.*;
import io.recode.decompile.CodePointer;
import io.recode.model.Element;
import io.recode.model.ElementType;
import io.recode.util.Iterators;
import io.recode.util.SuppliedIterator;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SimpleCodeGeneratorConfiguration implements CodeGeneratorConfiguration {

    private CodeGeneratorDelegateCandidate[] extensionCandidates;

    private CodeGeneratorAdviceCandidate[] advices;

    private SimpleCodeGeneratorConfiguration(CodeGeneratorDelegateCandidate[] extensionCandidates,
                                             CodeGeneratorAdviceCandidate[] advices) {
        this.extensionCandidates = extensionCandidates;
        this.advices = advices;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CodeGeneratorDelegate<? extends Element> getDelegate(CodeGenerationContext context, CodePointer<? extends Element> codePointer) {
        assert codePointer != null : "Code pointer can't be null";

        CodeGeneratorDelegateCandidate candidate = extensionCandidates[codePointer.getElement().getElementType().ordinal()];

        while (candidate != null) {
            if (candidate.selector().matches((CodePointer) codePointer)) {
                return candidate.extension();
            }

            candidate = candidate.next().orElseGet(() -> null);
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<CodeGeneratorAdvice<? extends Element>> getAdvices(CodeGenerationContext context, CodePointer<? extends Element> codePointer) {
        assert context != null : "Context can't be null";
        assert codePointer != null : "Code pointer can't be null";

        final CodeGeneratorAdviceCandidate firstCandidate = advices[codePointer.getElement().getElementType().ordinal()];

        if (firstCandidate == null) {
            return Iterators.empty();
        }

        return firstCandidate.iterator(candidate -> candidate.selector().matches((CodePointer) codePointer));
    }

    public static CodeGeneratorConfigurer configurer() {
        return new Configurer();
    }

    @SuppressWarnings("unchecked")
    private static final class Configurer implements CodeGeneratorConfigurer {

        private final CodeGeneratorDelegateCandidate[] extensionCandidates = new CodeGeneratorDelegateCandidate[ElementType.values().length];

        private final CodeGeneratorAdviceCandidate[] advices = new CodeGeneratorAdviceCandidate[ElementType.values().length];

        @Override
        public <E extends Element> OnContinuation<E> on(ElementSelector<E> elementSelector) {
            return delegate -> {
                assert elementSelector != null : "Element type can't be null";
                assert delegate != null : "Extension can't be null";

                final CodeGeneratorDelegateCandidate newCandidate = new CodeGeneratorDelegateCandidate(
                        elementSelector,
                        delegate,
                        Optional.<CodeGeneratorDelegateCandidate>empty());

                insert(extensionCandidates, elementSelector.getElementType().ordinal(), newCandidate);

                return this;
            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public <E extends Element> AroundContinuation<E> around(ElementSelector<E> elementSelector) {
            return advice -> {
                final CodeGeneratorAdviceCandidate newCandidate = new CodeGeneratorAdviceCandidate(
                        elementSelector, advice,
                        Optional.<CodeGeneratorAdviceCandidate>empty()
                );

                insert(advices, elementSelector.getElementType().ordinal(), newCandidate);

                return this;
            };
        }

        @Override
        public CodeGeneratorConfiguration configuration() {
            return new SimpleCodeGeneratorConfiguration(extensionCandidates, advices);
        }

        private void insert(CodeGeneratorExtensionCandidate[] extensionCandidates, int index, CodeGeneratorExtensionCandidate newCandidate) {
            CodeGeneratorExtensionCandidate<?, CodeGeneratorExtensionCandidate> existingCandidate = extensionCandidates[index];

            if (existingCandidate == null) {
                extensionCandidates[index] = newCandidate;
            } else {
                while (existingCandidate.next().isPresent()) {
                    existingCandidate = existingCandidate.next().get();
                }

                existingCandidate.next(newCandidate);
            }
        }
    }

    private static abstract class CodeGeneratorExtensionCandidate<E, T extends CodeGeneratorExtensionCandidate> {

        private final ElementSelector<? extends Element> elementElementSelector;

        private final E extension;

        private Optional<T> nextCandidate;

        private CodeGeneratorExtensionCandidate(ElementSelector<? extends Element> elementElementSelector,
                                                E extension,
                                                Optional<T> nextCandidate) {
            this.elementElementSelector = elementElementSelector;
            this.extension = extension;
            this.nextCandidate = nextCandidate;
        }

        public ElementSelector<? extends Element> selector() {
            return elementElementSelector;
        }

        public E extension() {
            return extension;
        }

        public Optional<T> next() {
            return nextCandidate;
        }

        public void next(T candidate) {
            this.nextCandidate = Optional.of(candidate);
        }

        @SuppressWarnings("unchecked")
        public Iterator<E> iterator(Predicate<T> predicate) {
            return new SuppliedIterator<E>(new Supplier<Optional<E>>() {

                private T currentCandidate = (T) CodeGeneratorExtensionCandidate.this;

                @Override
                public Optional<E> get() {
                    while (currentCandidate != null && !predicate.test(currentCandidate)) {
                        final Optional<T> next = currentCandidate.next();

                        if (!next.isPresent()) {
                            return Optional.empty();
                        }

                        currentCandidate = next.get();
                    }

                    if (currentCandidate == null) {
                        return Optional.empty();
                    }

                    final Optional<E> result = Optional.of((E) currentCandidate.extension());

                    currentCandidate = (T) currentCandidate.next().orElse(null);

                    return result;
                }
            });
        }
    }

    private static final class CodeGeneratorDelegateCandidate extends CodeGeneratorExtensionCandidate<CodeGeneratorDelegate<? extends Element>, CodeGeneratorDelegateCandidate> {
        private CodeGeneratorDelegateCandidate(ElementSelector<? extends Element> elementElementSelector,
                                               CodeGeneratorDelegate<? extends Element> extension,
                                               Optional<CodeGeneratorDelegateCandidate> nextCandidate) {
            super(elementElementSelector, extension, nextCandidate);
        }
    }

    private static final class CodeGeneratorAdviceCandidate extends CodeGeneratorExtensionCandidate<CodeGeneratorAdvice<? extends Element>, CodeGeneratorAdviceCandidate> {
        private CodeGeneratorAdviceCandidate(ElementSelector<? extends Element> elementElementSelector,
                                             CodeGeneratorAdvice<? extends Element> extension,
                                             Optional<CodeGeneratorAdviceCandidate> nextCandidate) {
            super(elementElementSelector, extension, nextCandidate);
        }
    }

}
