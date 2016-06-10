package io.recode.classfile;

import java.util.List;

public interface ConstantPool {

    List<ConstantPoolEntry> getEntries();

    String getClassName(int index);

    String getString(int index);

    ConstantPoolEntry getEntry(int index);

    <T extends ConstantPoolEntry> T getEntry(int index, Class<T> type);

    ConstantPoolEntry[] getEntries(int[] indices);

    ConstantPoolEntryDescriptor[] getDescriptors(int[] indices);

    long getLong(int index);

    <T extends ConstantPoolEntryDescriptor> T getDescriptor(int index, Class<T> type);

    FieldRefDescriptor getFieldRefDescriptor(int index);

    NameAndTypeDescriptor getNameAndTypeDescriptor(int index);

    MethodRefDescriptor getMethodRefDescriptor(int index);

    InterfaceMethodRefDescriptor getInterfaceMethodRefDescriptor(int index);

    InvokeDynamicDescriptor getInvokeDynamicDescriptor(int index);

    MethodHandleDescriptor getMethodHandleDescriptor(int index);

    MethodTypeDescriptor getMethodTypeDescriptor(int index);

}
