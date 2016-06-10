package io.recode.classfile.impl;

import io.recode.classfile.Attribute;
import io.recode.classfile.UnknownAttribute;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public final class UnknownAttributeImpl implements Attribute, UnknownAttribute {

    private final String name;

    private final byte[] data;

    public UnknownAttributeImpl(String name, byte[] data) {
        assert name != null : "name can't be null";
        assert data != null : "data can't be null";

        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputStream getData() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof UnknownAttributeImpl)) {
            return false;
        }

        final UnknownAttributeImpl other = (UnknownAttributeImpl) obj;

        return name.equals(other.name) && Arrays.equals(data, other.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{name.hashCode(), Arrays.hashCode(data)});
    }

    @Override
    public String toString() {
        return "UnknownAttribute{name=\"" + name + "\", data=" + Arrays.toString(data) + "}";
    }
}
