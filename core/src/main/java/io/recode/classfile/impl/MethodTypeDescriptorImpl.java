package io.recode.classfile.impl;

import io.recode.classfile.MethodTypeDescriptor;

public final class MethodTypeDescriptorImpl implements MethodTypeDescriptor {

    private final String descriptor;

    public MethodTypeDescriptorImpl(String descriptor) {
        assert descriptor != null && !descriptor.isEmpty(): "Descriptor can't be null";
        this.descriptor = descriptor;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodTypeDescriptorImpl that = (MethodTypeDescriptorImpl) o;

        if (!descriptor.equals(that.descriptor)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return descriptor.hashCode();
    }

    @Override
    public String toString() {
        return "MethodTypeDescriptorImpl{" +
                "descriptor='" + descriptor + '\'' +
                '}';
    }
}
