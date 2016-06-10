package io.recode.classfile.impl;

import io.recode.classfile.InvokeDynamicDescriptor;

public final class InvokeDynamicDescriptorImpl implements InvokeDynamicDescriptor {

    private final int bootstrapMethodAttributeIndex;

    private final String methodName;

    private final String methodDescriptor;

    public InvokeDynamicDescriptorImpl(int bootstrapMethodAttributeIndex, String methodName, String methodDescriptor) {
        assert methodName != null && !methodName.isEmpty() : "Method name can't be null or empty";
        assert methodDescriptor != null && !methodDescriptor.isEmpty() : "Method descriptor can't be null or empty";

        this.bootstrapMethodAttributeIndex = bootstrapMethodAttributeIndex;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public int getBootstrapMethodAttributeIndex() {
        return bootstrapMethodAttributeIndex;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvokeDynamicDescriptorImpl that = (InvokeDynamicDescriptorImpl) o;

        if (bootstrapMethodAttributeIndex != that.bootstrapMethodAttributeIndex) return false;
        if (!methodDescriptor.equals(that.methodDescriptor)) return false;
        if (!methodName.equals(that.methodName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bootstrapMethodAttributeIndex;
        result = 31 * result + methodName.hashCode();
        result = 31 * result + methodDescriptor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InvokeDynamicDescriptorImpl{" +
                "bootstrapMethodAttributeIndex=" + bootstrapMethodAttributeIndex +
                ", methodName='" + methodName + '\'' +
                ", methodDescriptor='" + methodDescriptor + '\'' +
                '}';
    }
}
