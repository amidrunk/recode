package io.recode.model;

import java.util.Optional;

public final class ModelQueryTransformation<S extends Element, R extends Element, T extends Element> implements ModelTransformation<S, T> {

    private final ModelQuery<S, R> modelQuery;

    private final ModelTransformation<R, T> modelTransformation;

    public ModelQueryTransformation(ModelQuery<S, R> modelQuery, ModelTransformation<R, T> modelTransformation) {
        assert modelQuery != null : "Model query can't be null";
        assert modelTransformation != null : "Model transformation can't be null";

        this.modelQuery = modelQuery;
        this.modelTransformation = modelTransformation;
    }

    @Override
    public Optional<T> apply(S source) {
        final Optional<R> result = modelQuery.from(source);

        if (!result.isPresent()) {
            return Optional.empty();
        }

        return modelTransformation.apply(result.get());
    }

    public ModelQuery<S, R> getModelQuery() {
        return modelQuery;
    }

    public ModelTransformation<R, T> getTargetTransformation() {
        return modelTransformation;
    }
}
