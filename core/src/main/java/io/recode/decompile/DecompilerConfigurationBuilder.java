package io.recode.decompile;

import io.recode.model.Element;
import io.recode.model.ElementType;
import io.recode.model.ModelQuery;
import io.recode.model.ModelTransformation;
import io.recode.util.Priority;

public interface DecompilerConfigurationBuilder {

    ExtendContinuation<DecompilerDelegate> before(int byteCode);

    ExtendContinuation<DecompilerDelegate> before(int... byteCodes);

    ExtendContinuation<DecompilerDelegate> after(int byteCode);

    ExtendContinuation<DecompilerDelegate> after(int... byteCodes);

    ExtendContinuation<DecompilerDelegate> on(int byteCode);

    ExtendContinuation<DecompilerDelegate> on(int... byteCodes);

    ExtendContinuation<DecompilerDelegate> on(int startByteCode, int endByteCode);

    OnElementTypeContinuation map(ElementType elementType);

    interface OnElementTypeContinuation {

        <R extends Element> ForQueryContinuationWithPriority<R> forQuery(ModelQuery<Element, R> query);
    }

    interface ForQueryContinuationWithoutPriority<R extends Element> {

        DecompilerConfigurationBuilder to(ModelTransformation<R, ? extends Element> transformation);

    }

    interface ForQueryContinuationWithPriority<R extends Element> extends ForQueryContinuationWithoutPriority<R> {

        ForQueryContinuationWithoutPriority<R> withPriority(Priority priority);

    }

    interface ExtendContinuation<T> extends WithPriorityContinuation<T> {

        WithPriorityContinuation<T> withPriority(Priority priority);
    }

    interface WithPriorityContinuation<T> extends WhenContinuation<T> {

        WhenContinuation<T> when(DecompilationStateSelector selector);

    }

    interface WhenContinuation<T> {

        DecompilerConfigurationBuilder then(T t);

    }

    DecompilerConfiguration build();

}
