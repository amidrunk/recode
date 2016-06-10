package io.recode.classfile.impl;

import io.recode.classfile.NameAndTypeDescriptor;

public final class NameAndTypeDescriptorImpl implements NameAndTypeDescriptor {

    private final String name;

    private final String descriptor;

    public NameAndTypeDescriptorImpl(String name, String descriptor) {
        assert name != null && !name.isEmpty() : "Name can't be null or empty";
        assert descriptor != null && !descriptor.isEmpty() : "Descriptor can't be null or empty";

        this.name = name;
        this.descriptor = descriptor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NameAndTypeDescriptorImpl that = (NameAndTypeDescriptorImpl) o;

        if (!descriptor.equals(that.descriptor)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + descriptor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NameAndTypeDescriptorImpl{" +
                "name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}
