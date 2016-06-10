package io.recode.classfile;

public interface FieldRefDescriptor extends ConstantPoolEntryDescriptor {

    String getClassName();

    String getDescriptor();

    String getName();

    default ConstantPoolEntryTag getTag() {
        return ConstantPoolEntryTag.FIELD_REF;
    }

}
