package io.recode.classfile;

public interface MethodHandleDescriptor extends ConstantPoolEntryDescriptor {

    ReferenceKind getReferenceKind();

    String getClassName();

    String getMethodName();

    String getMethodDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.METHOD_HANDLE;
    }

}
