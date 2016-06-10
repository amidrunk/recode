package io.recode.classfile.impl;

import io.recode.classfile.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.recode.classfile.ConstantPoolEntry.*;
import static io.recode.classfile.ConstantPoolEntryTag.UTF8;

public final class DefaultConstantPool implements ConstantPool {

    private final ConstantPoolEntry[] entries;

    private DefaultConstantPool(ConstantPoolEntry[] entries) {
        this.entries = entries;
    }

    public List<ConstantPoolEntry> getEntries() {
        return Arrays.asList(entries);
    }

    @Override
    public String getClassName(int index) {
        final ConstantPoolEntry.ClassEntry classEntry = (ConstantPoolEntry.ClassEntry) getEntry(index, ConstantPoolEntryTag.CLASS);

        return getString(classEntry.getNameIndex());
    }

    @Override
    public String getString(int index) {
        return ((UTF8Entry) getEntry(index, UTF8)).getValue();
    }

    @Override
    public ConstantPoolEntry getEntry(int index) {
        assert index > 0 : "Index must be > 0";

        final int constantPoolIndex = index - 1;

        if (constantPoolIndex >= entries.length) {
            throw new IndexOutOfBoundsException("Index " + index + " is not a valid constant pool index; must be 1 >= index <= " + entries.length);
        }

        return entries[constantPoolIndex];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ConstantPoolEntry> T getEntry(int index, Class<T> type) {
        assert type != null : "Type can't be null";

        final ConstantPoolEntry entry = getEntry(index);

        if (!type.isInstance(entry)) {
            throw new IllegalArgumentException("Expected entry of type " + type.getName()
                    + " at constant pool index " + index + ", actually was " + entry);
        }

        return (T) entry;
    }

    @Override
    public ConstantPoolEntry[] getEntries(int[] indices) {
        assert indices != null : "Indices can't be null";

        final ConstantPoolEntry[] matchedEntries = new ConstantPoolEntry[indices.length];

        for (int i = 0; i < indices.length; i++) {
            matchedEntries[i] = getEntry(indices[i]);
        }

        return matchedEntries;
    }

    @Override
    public ConstantPoolEntryDescriptor[] getDescriptors(int[] indices) {
        assert indices != null : "Indices can't be null";

        final ConstantPoolEntryDescriptor[] descriptors = new ConstantPoolEntryDescriptor[indices.length];

        for (int i = 0; i < indices.length; i++) {
            final int index = indices[i];
            final ConstantPoolEntry entry = getEntry(index);
            final ConstantPoolEntryDescriptor descriptor;

            switch (entry.getTag()) {
                case FIELD_REF:
                    descriptor = getFieldRefDescriptor(index);
                    break;
                case INTERFACE_METHOD_REF:
                    descriptor = getInterfaceMethodRefDescriptor(index);
                    break;
                case NAME_AND_TYPE:
                    descriptor = getNameAndTypeDescriptor(index);
                    break;
                case METHOD_HANDLE:
                    descriptor = getMethodHandleDescriptor(index);
                    break;
                case METHOD_TYPE:
                    descriptor = getMethodTypeDescriptor(index);
                    break;
                case INVOKE_DYNAMIC:
                    descriptor = getInvokeDynamicDescriptor(index);
                    break;
                case METHOD_REF:
                    descriptor = getMethodRefDescriptor(index);
                    break;
                default:
                    throw new IllegalArgumentException("Constant pool entry " + entry + " not supported");
            }

            descriptors[i] = descriptor;
        }

        return descriptors;
    }

    @Override
    public FieldRefDescriptor getFieldRefDescriptor(int index) {
        final FieldRefEntry fieldRefEntry = getEntry(index, FieldRefEntry.class);
        final NameAndTypeEntry nameAndType = getEntry(fieldRefEntry.getNameAndTypeIndex(), NameAndTypeEntry.class);
        final String className = getClassName(fieldRefEntry.getClassIndex());
        final String fieldDescriptor = getString(nameAndType.getDescriptorIndex());
        final String fieldName = getString(nameAndType.getNameIndex());

        return new FieldRefDescriptorImpl(className, fieldDescriptor, fieldName);
    }

    @Override
    public long getLong(int index) {
        return getEntry(index, ConstantPoolEntry.LongEntry.class).getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ConstantPoolEntryDescriptor> T getDescriptor(int index, Class<T> type) {
        assert index > 0 : "Index must be > 0";
        assert type != null : "Type can't be null";

        final ConstantPoolEntryDescriptor[] descriptors = getDescriptors(new int[]{index});

        if (!type.isInstance(descriptors[0])) {
            throw new IllegalArgumentException("Descriptor is not an instance of " + type.getName() + ": " + descriptors[0]);
        }

        return (T) descriptors[0];
    }

    @Override
    public NameAndTypeDescriptor getNameAndTypeDescriptor(int index) {
        final NameAndTypeEntry nameAndTypeEntry = getEntry(index, NameAndTypeEntry.class);
        final UTF8Entry nameEntry = (UTF8Entry) getEntry(nameAndTypeEntry.getNameIndex(), UTF8);
        final UTF8Entry descriptorEntry = (UTF8Entry) getEntry(nameAndTypeEntry.getDescriptorIndex(), UTF8);

        return new NameAndTypeDescriptorImpl(nameEntry.getValue(), descriptorEntry.getValue());
    }

    @Override
    public MethodRefDescriptor getMethodRefDescriptor(int index) {
        final MethodRefEntry entry = getEntry(index, MethodRefEntry.class);
        final String className = getClassName(entry.getClassIndex());
        final NameAndTypeDescriptor nameAndType = getNameAndTypeDescriptor(entry.getNameAndTypeIndex());

        return new MethodRefDescriptorImpl(className, nameAndType.getName(), nameAndType.getDescriptor());
    }

    @Override
    public InterfaceMethodRefDescriptor getInterfaceMethodRefDescriptor(int index) {
        final InterfaceMethodRefEntry entry = getEntry(index, InterfaceMethodRefEntry.class);
        final String className = getClassName(entry.getClassIndex());
        final NameAndTypeDescriptor nameAndTypeDescriptor = getNameAndTypeDescriptor(entry.getNameAndTypeIndex());

        return new InterfaceMethodRefDescriptorImpl(className, nameAndTypeDescriptor.getName(), nameAndTypeDescriptor.getDescriptor());
    }

    @Override
    public InvokeDynamicDescriptor getInvokeDynamicDescriptor(int index) {
        final InvokeDynamicEntry entry = getEntry(index, InvokeDynamicEntry.class);
        final NameAndTypeDescriptor nameAndTypeDescriptor = getNameAndTypeDescriptor(entry.getNameAndTypeIndex());

        return new InvokeDynamicDescriptorImpl(entry.getBootstrapMethodAttributeIndex(),
                nameAndTypeDescriptor.getName(), nameAndTypeDescriptor.getDescriptor());
    }

    @Override
    public MethodHandleDescriptor getMethodHandleDescriptor(int index) {
        final MethodHandleEntry methodHandleEntry = getEntry(index, MethodHandleEntry.class);
        final MethodRefDescriptor methodRefDescriptor = getDescriptor(methodHandleEntry.getReferenceIndex(), MethodRefDescriptor.class);

        return new MethodHandleDescriptorImpl(
                methodHandleEntry.getReferenceKind(),
                methodRefDescriptor.getClassName(),
                methodRefDescriptor.getMethodName(),
                methodRefDescriptor.getDescriptor());
    }

    @Override
    public MethodTypeDescriptor getMethodTypeDescriptor(int index) {
        final MethodTypeEntry entry = getEntry(index, MethodTypeEntry.class);
        final UTF8Entry descriptorEntry = (UTF8Entry) getEntry(entry.getDescriptorIndex(), UTF8);

        return new MethodTypeDescriptorImpl(descriptorEntry.getValue());
    }

    private ConstantPoolEntry getEntry(int index, ConstantPoolEntryTag expectedTag) {
        assert (index > 0 && index <= entries.length) : "Index must be in range [1, " + entries.length + "], was " + index;

        final ConstantPoolEntry entry = entries[index - 1];

        if (entry.getTag() != expectedTag) {
            throw new ClassFileFormatException("Invalid class pool entry at index " + index + "; expected " + expectedTag + ", was: " + entry);
        }

        return entry;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultConstantPool)) {
            return false;
        }

        final DefaultConstantPool other = (DefaultConstantPool) obj;

        return Arrays.equals(entries, other.entries);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(entries);
    }

    @Override
    public String toString() {
        return "DefaultConstantPool{entries=" + getEntries() + "}";
    }

    public static final class Builder {

        private final ArrayList<ConstantPoolEntry> entries = new ArrayList<>();

        public Builder addEntry(ConstantPoolEntry entry) {
            assert entry != null : "entry can't be null";

            entries.add(entry);

            if (entry.getTag() == ConstantPoolEntryTag.LONG || entry.getTag() == ConstantPoolEntryTag.DOUBLE) {
                entries.add(null);
            }

            return this;
        }

        public DefaultConstantPool create() {
            return new DefaultConstantPool(entries.toArray(new ConstantPoolEntry[entries.size()]));
        }

    }

}
