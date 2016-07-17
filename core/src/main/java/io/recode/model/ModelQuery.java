package io.recode.model;

import java.util.Optional;
import java.util.function.Predicate;

public interface ModelQuery<S, R> {

    Optional<R> from(S from);

    @SuppressWarnings("unchecked")
    default Predicate<S> is(Predicate<? extends R> predicate) {
        assert predicate != null : "Predicate can't be null";

        return source -> {
            final Optional<R> result = ModelQuery.this.from(source);

            if (!result.isPresent()) {
                return false;
            }

            return ((Predicate) predicate).test(result.get());
        };
    }

    @SuppressWarnings("unchecked")
    default <E> ModelQuery<S, E> get(ModelQuery<? extends R, E> modelQuery) {
        assert modelQuery != null : "Model query can't be null";

        return source -> {
            final Optional<R> intermediateResult = ModelQuery.this.from(source);

            if (!intermediateResult.isPresent()) {
                return Optional.empty();
            }

            return ((ModelQuery) modelQuery).from(intermediateResult.get());
        };
    }

    default <E> ModelQuery<S, R> join(ModelQuery<R, E> query) {
        assert query != null : "Query can't be null";

        return from -> {
            final Optional<R> result = ModelQuery.this.from(from);

            if (!result.isPresent()) {
                return Optional.empty();
            }

            if (!query.from(result.get()).isPresent()) {
                return Optional.empty();
            }

            return result;
        };
    }

    interface WhereContinuation<S, R> extends ModelQuery<S, R> {

        default WhereContinuation<S, R> and(Predicate<? extends R> predicate) {
            return where(predicate);
        }

    }

    @SuppressWarnings("unchecked")
    default WhereContinuation<S, R> where(Predicate<? extends R> predicate) {
        assert predicate != null : "Predicate can't be null";

        return from -> {
            final Optional<R> result = ModelQuery.this.from(from);

            if (!result.isPresent()) {
                return result;
            }

            if (!((Predicate) predicate).test(result.get())) {
                return Optional.empty();
            }

            return result;
        };
    }

    @SuppressWarnings("unchecked")
    default <T> ModelQuery<S, T> as(Class<T> type) {
        assert type != null : "Type can't be null";

        return from -> {
            final Optional<R> result = ModelQuery.this.from(from);

            if (!result.isPresent()) {
                return Optional.empty();
            }

            final R resultValue = result.get();

            if (!type.isInstance(resultValue)) {
                return Optional.empty();
            }

            return Optional.of((T) resultValue);
        };
    }

    default<T> Optional<T> search(Element element) {
        return null;
    }
}
