package io.recode.model;

import java.util.Optional;

public interface ModelTransformation<S extends Element, T extends Element> {

    Optional<T> apply(S source);

}
