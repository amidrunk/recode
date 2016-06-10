package io.recode.classfile.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import io.recode.classfile.impl.UnknownAttributeImpl;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class UnknownAttributeImplTest {

    private final byte[] data = "foo".getBytes();
    private final UnknownAttributeImpl attribute = new UnknownAttributeImpl("anattr", data);

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullName() {
        new UnknownAttributeImpl(null, new byte[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullData() {
        new UnknownAttributeImpl("foo", null);
    }

    @Test
    public void constructorShouldRetainNameAndData() throws IOException {
        assertEquals("anattr", attribute.getName());
        Assert.assertArrayEquals(data, IOUtils.toByteArray(attribute.getData()));
    }

    @Test
    public void attributeShouldBeEqualToItSelf() {
        assertEquals(attribute, attribute);
        assertEquals(attribute.hashCode(), attribute.hashCode());
    }

    @Test
    public void attributeShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(attribute, null);
        assertNotEquals(attribute, "foo");
    }

    @Test
    public void attributesWithEqualPropertiesShouldBeEqual() {
        final UnknownAttributeImpl other = new UnknownAttributeImpl("anattr", data);

        assertEquals(attribute, other);
        assertEquals(attribute.hashCode(), other.hashCode());
    }

    @Test
    public void toStringValueShouldContainProperties() {
        assertThat(attribute.toString(), containsString("anattr"));
    }

}
