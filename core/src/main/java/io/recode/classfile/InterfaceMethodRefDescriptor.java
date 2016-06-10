package io.recode.classfile;

public interface InterfaceMethodRefDescriptor extends MethodRefDescriptor {

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.INTERFACE_METHOD_REF;
    }

}
