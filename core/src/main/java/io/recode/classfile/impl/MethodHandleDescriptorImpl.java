package io.recode.classfile.impl;

import io.recode.classfile.MethodHandleDescriptor;
import io.recode.classfile.ReferenceKind;

public class MethodHandleDescriptorImpl implements MethodHandleDescriptor {

    private final ReferenceKind referenceKind;

    private final String className;

    private final String methodName;

    private final String methodDescriptor;

    public MethodHandleDescriptorImpl(ReferenceKind referenceKind, String className, String methodName, String methodDescriptor) {
        assert referenceKind != null : "Reference kind can't be null";
        assert className != null && !className.isEmpty() : "Class name can't be null or empty";
        assert methodName != null && !methodName.isEmpty() : "Method name can't be null or empty";
        assert methodDescriptor != null && !methodDescriptor.isEmpty() : "Method descriptor can't be null or empty";

        this.referenceKind = referenceKind;
        this.className = className;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public ReferenceKind getReferenceKind() {
        return referenceKind;
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
    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodHandleDescriptorImpl that = (MethodHandleDescriptorImpl) o;

        if (!className.equals(that.className)) return false;
        if (!methodDescriptor.equals(that.methodDescriptor)) return false;
        if (!methodName.equals(that.methodName)) return false;
        if (referenceKind != that.referenceKind) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = referenceKind.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + methodDescriptor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MethodHandleDescriptorImpl{" +
                "referenceKind=" + referenceKind +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodDescriptor='" + methodDescriptor + '\'' +
                '}';
    }
}
