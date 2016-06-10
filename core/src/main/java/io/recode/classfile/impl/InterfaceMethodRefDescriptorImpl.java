package io.recode.classfile.impl;

import io.recode.classfile.InterfaceMethodRefDescriptor;

public final class InterfaceMethodRefDescriptorImpl implements InterfaceMethodRefDescriptor {

    private final String className;

    private final String methodName;

    private final String descriptor;

    public InterfaceMethodRefDescriptorImpl(String className, String methodName, String descriptor) {
        assert className != null && !className.isEmpty() : "Class name can't be null or empty";
        assert methodName != null && !methodName.isEmpty() : "Method name can't be null or empty";
        assert descriptor != null && !descriptor.isEmpty() : "Descriptor can't be null or empty";

        this.className = className;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterfaceMethodRefDescriptorImpl that = (InterfaceMethodRefDescriptorImpl) o;

        if (!className.equals(that.className)) return false;
        if (!descriptor.equals(that.descriptor)) return false;
        if (!methodName.equals(that.methodName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + descriptor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InterfaceMethodRefDescriptorImpl{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}
