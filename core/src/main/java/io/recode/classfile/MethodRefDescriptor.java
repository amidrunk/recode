package io.recode.classfile;

public interface MethodRefDescriptor extends ConstantPoolEntryDescriptor {

    String getClassName();

    String getMethodName();

    String getDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.METHOD_REF;
    }

}
