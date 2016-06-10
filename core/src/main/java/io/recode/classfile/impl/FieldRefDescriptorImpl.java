package io.recode.classfile.impl;

import io.recode.classfile.FieldRefDescriptor;

public final class FieldRefDescriptorImpl implements FieldRefDescriptor {

    private final String className;

    private final String descriptor;

    private final String name;

    public FieldRefDescriptorImpl(String className, String descriptor, String name) {
        assert className != null && !className.isEmpty() : "Class name can't be null or empty";
        assert descriptor != null && !descriptor.isEmpty() : "Descriptor can't be null or empty";
        assert name != null && !name.isEmpty() : "Name can't be null or empty";

        this.className = className;
        this.descriptor = descriptor;
        this.name = name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldRefDescriptorImpl that = (FieldRefDescriptorImpl) o;

        if (!className.equals(that.className)) return false;
        if (!descriptor.equals(that.descriptor)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + descriptor.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldRefDescriptorImpl{" +
                "className='" + className + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
