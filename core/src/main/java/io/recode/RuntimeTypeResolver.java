package io.recode;

import java.lang.reflect.Type;
import java.util.function.Supplier;

public final class RuntimeTypeResolver implements TypeResolver {

    public static final Supplier<ClassLoader> DEFAULT_CLASS_LOADER_SUPPLIER = () -> Thread.currentThread().getContextClassLoader();

    private final Supplier<ClassLoader> classLoaderSupplier;

    public RuntimeTypeResolver() {
        this(DEFAULT_CLASS_LOADER_SUPPLIER);
    }

    public RuntimeTypeResolver(Supplier<ClassLoader> classLoaderSupplier) {
        assert classLoaderSupplier != null : "classLoaderSupplier can't be null";

        this.classLoaderSupplier = classLoaderSupplier;
    }

    @Override
    public Type resolveType(String name) {
        assert name != null && !name.isEmpty() : "Type name can't be null or empty";

        try {
            return Class.forName(name, false, classLoaderSupplier.get());
        } catch (ClassNotFoundException e) {
            return new UnresolvedType(name);
        }
    }
}
