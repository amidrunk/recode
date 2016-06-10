package io.recode.classfile;

public interface MethodTypeDescriptor extends ConstantPoolEntryDescriptor {

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.METHOD_TYPE;
    }

}
