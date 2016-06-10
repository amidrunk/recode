package io.recode;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class UnresolvedTypeTest {

    @Test
    public void constructorShouldNotAcceptNullOrEmptyTypeName() {
        assertThrown(() -> new UnresolvedType(null), AssertionError.class);
        assertThrown(() -> new UnresolvedType(""), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainTypeName() {
        assertEquals("com.company.Foo", new UnresolvedType("com.company.Foo").getTypeName());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        final UnresolvedType type = new UnresolvedType("foo");

        assertEquals(type, type);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrInstanceOfOtherType() {
        final UnresolvedType type = new UnresolvedType("foo");

        assertFalse(type.equals("foo"));
        assertFalse(type.equals(null));
    }

    @Test
    public void instancesWithEqualTypeNamesShouldBeEqual() {
        final UnresolvedType type1 = new UnresolvedType("java.lang.String");
        final UnresolvedType type2 = new UnresolvedType("java.lang.String");

        assertEquals(type1, type2);
        assertEquals(type1.hashCode(), type2.hashCode());
    }

    @Test
    public void toStringValueShouldContainTypeName() {
        final UnresolvedType type = new UnresolvedType("java.lang.String");
        assertTrue(type.toString().contains("java.lang.String"));
    }
}
