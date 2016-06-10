package io.recode.classfile;

import io.recode.classfile.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassPathClassFileResolverTest {

    private final ClassFileReader classFileReader = mock(ClassFileReader.class);

    private final ClassPathClassFileResolver resolver = new ClassPathClassFileResolver(classFileReader);

    @Test
    public void constructorShouldNotAcceptNullClassLoader() {
        boolean failed = false;

        try {
            new ClassPathClassFileResolver(null);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void resolveClassFileShouldNotAcceptNullType() {
        boolean failed = false;

        try {
            resolver.resolveClassFile(null);
        } catch (AssertionError e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void resolveClassFileShouldFailIfResourceCannotBeFound() {
        final Type unResolvableType = mock(Type.class);

        when(unResolvableType.getTypeName()).thenReturn("com.foo.bar.Invalid");

        boolean failed = false;

        try {
            resolver.resolveClassFile(unResolvableType);
        } catch (ClassFileNotFoundException e) {
            failed = true;
        }

        assertTrue(failed);
    }

    @Test
    public void resolveClassShouldFailIfLoadingFailsWithIOException() throws Exception {
        final IOException cause = new IOException();

        when(classFileReader.read(any(InputStream.class))).thenThrow(cause);

        boolean failed = false;

        try {
            resolver.resolveClassFile(getClass());
        } catch (ClassFileResolutionException e) {
            failed = true;
            assertEquals(cause, e.getCause());
        }
    }

    @Test
    public void resolveClassFileShouldReturnClassFileFromClassFileReader() throws IOException {
        final ClassFile classFile = mock(ClassFile.class);

        when(classFileReader.read(any(InputStream.class))).thenReturn(classFile);

        assertEquals(classFile, resolver.resolveClassFile(getClass()));
    }
}
