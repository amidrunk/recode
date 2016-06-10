package io.recode;

import org.junit.Test;

import java.lang.reflect.Type;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RuntimeTypeResolverTest {

    private final RuntimeTypeResolver resolver = new RuntimeTypeResolver();

    @Test
    public void resolveTypeShouldNotAcceptNullOrEmptyTypeName() {
        assertThrown(() -> resolver.resolveType(null), AssertionError.class);
        assertThrown(() -> resolver.resolveType(""), AssertionError.class);
    }

    @Test
    public void resolveTypeShouldReturnClassIfClassIsFoundInContext() {
        final Type type = resolver.resolveType("java.lang.String");

        assertEquals(String.class, type);
    }

    @Test
    public void resolveTypeShouldReturnUnresolvedTypeForUnknownType() {
        final Type type = resolver.resolveType("foo");

        assertTrue(type instanceof UnresolvedType);
        assertEquals("foo", type.getTypeName());
    }
}
