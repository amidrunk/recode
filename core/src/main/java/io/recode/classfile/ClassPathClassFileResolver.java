package io.recode.classfile;

import io.recode.classfile.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public final class ClassPathClassFileResolver implements ClassFileResolver {

    private final ClassLoader classLoader;

    private final ClassFileReader classFileReader;

    public ClassPathClassFileResolver(ClassFileReader classFileReader) {
        this(classFileReader, Thread.currentThread().getContextClassLoader());
    }

    public ClassPathClassFileResolver(ClassFileReader classFileReader, ClassLoader classLoader) {
        assert classFileReader != null : "Class file reader can't be null";
        assert classLoader != null : "Class loader can't be null";

        this.classFileReader = classFileReader;
        this.classLoader = classLoader;
    }

    @Override
    public ClassFile resolveClassFile(Type type) {
        assert type != null : "Type can't be null";

        final String resourceName = type.getTypeName().replace('.', '/') + ".class";

        try (InputStream in = classLoader.getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new ClassFileNotFoundException("Class file for type '" + type.getTypeName() + "' (resource '"
                        + resourceName + "') could not be found in class loader: " + classLoader);
            }

            return classFileReader.read(in);
        } catch (IOException e) {
            throw new ClassFileResolutionException("Failed to read class file from resource '" + resourceName + "'", e);
        }
    }

}
