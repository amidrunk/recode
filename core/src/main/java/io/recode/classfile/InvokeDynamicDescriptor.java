package io.recode.classfile;

public interface InvokeDynamicDescriptor extends ConstantPoolEntryDescriptor {

    int getBootstrapMethodAttributeIndex();

    String getMethodName();

    String getMethodDescriptor();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.INVOKE_DYNAMIC;
    }

}
