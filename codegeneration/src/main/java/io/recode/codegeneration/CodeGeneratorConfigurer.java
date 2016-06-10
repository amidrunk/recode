package io.recode.codegeneration;

import io.recode.model.Element;

/**
 * A <code>CodeGeneratorConfigurer</code> is used to create a configuration for a code generator. A code generator
 * configuration provides support for creating a configuration for handling elements during code generation through
 * delegates. Multiple delegates can be configured for an element, allowing for overrideable core configurations.
 */
public interface CodeGeneratorConfigurer {

    <E extends Element> OnContinuation<E> on(ElementSelector<E> elementSelector);

    <E extends Element> AroundContinuation<E> around(ElementSelector<E> elementSelector);

    CodeGeneratorConfiguration configuration();

    interface OnContinuation<E extends Element> {

        CodeGeneratorConfigurer then(CodeGeneratorDelegate<? extends E> delegate);

    }

    interface AroundContinuation<E extends Element> {

        CodeGeneratorConfigurer then(CodeGeneratorAdvice<E> advice);

    }

}
