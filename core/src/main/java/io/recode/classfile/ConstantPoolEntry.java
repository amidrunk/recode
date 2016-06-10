package io.recode.classfile;

import java.util.Arrays;

public abstract class ConstantPoolEntry {

    public abstract ConstantPoolEntryTag getTag();

    @SuppressWarnings("unchecked")
    public <T extends ConstantPoolEntry> T as(Class<T> type) {
        assert type != null : "Type can't be null";

        if (!type.isInstance(this)) {
            throw new IllegalArgumentException("Constant pool entry '" + this + "' can't be cast to '" + type.getName() + "'");
        }

        return (T) this;
    }

    public static final class ClassEntry extends ConstantPoolEntry {

        private final int nameIndex;

        public ClassEntry(int nameIndex) {
            assert nameIndex >= 0 : "Name index (" + nameIndex + ") must be positive";
            this.nameIndex = nameIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.CLASS;
        }

        public int getNameIndex() {
            return nameIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof ClassEntry)) {
                return false;
            }

            final ClassEntry other = (ClassEntry) obj;

            return other.nameIndex == nameIndex;
        }

        @Override
        public int hashCode() {
            return nameIndex;
        }

        @Override
        public String toString() {
            return "ClassEntry{nameIndex=" + nameIndex + "}";
        }
    }

    public static final class FieldRefEntry extends ConstantPoolEntry {

        private final int classIndex;

        private final int nameAndTypeIndex;

        public FieldRefEntry(int classIndex, int nameAndTypeIndex) {
            assert classIndex >= 0 : "Class index must be positive";
            assert nameAndTypeIndex >= 0 : "Name-and-type index must be positive";

            this.classIndex = classIndex;
            this.nameAndTypeIndex = nameAndTypeIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.FIELD_REF;
        }

        public int getClassIndex() {
            return classIndex;
        }

        public int getNameAndTypeIndex() {
            return nameAndTypeIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof FieldRefEntry)) {
                return false;
            }

            final FieldRefEntry other = (FieldRefEntry) obj;

            return other.classIndex == classIndex && other.nameAndTypeIndex == nameAndTypeIndex;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new int[]{classIndex, nameAndTypeIndex});
        }

        @Override
        public String toString() {
            return "FieldRefEntry{classIndex=" + classIndex + ", nameAndTypeIndex=" + nameAndTypeIndex + "}";
        }
    }

    public static final class MethodRefEntry extends ConstantPoolEntry {

        private final int classIndex;

        private final int nameAndTypeIndex;

        public MethodRefEntry(int classIndex, int nameAndTypeIndex) {
            assert classIndex >= 0 : "Class index must be positive";
            assert nameAndTypeIndex >= 0 : "Name-and-type index must be positive";

            this.classIndex = classIndex;
            this.nameAndTypeIndex = nameAndTypeIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.METHOD_REF;
        }

        public int getClassIndex() {
            return classIndex;
        }

        public int getNameAndTypeIndex() {
            return nameAndTypeIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof MethodRefEntry)) {
                return false;
            }

            final MethodRefEntry other = (MethodRefEntry) obj;

            return other.classIndex == classIndex && other.nameAndTypeIndex == nameAndTypeIndex;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new int[]{classIndex, nameAndTypeIndex});
        }

        @Override
        public String toString() {
            return "MethodRefEntry{classIndex=" + classIndex + ", nameAndTypeIndex=" + nameAndTypeIndex + "}";
        }
    }

    public static final class InterfaceMethodRefEntry extends ConstantPoolEntry {

        private final int classIndex;

        private final int nameAndTypeIndex;

        public InterfaceMethodRefEntry(int classIndex, int nameAndTypeIndex) {
            assert classIndex >= 0 : "Class index must be positive";
            assert nameAndTypeIndex >= 0 : "Name-and-type index must be positive";

            this.classIndex = classIndex;
            this.nameAndTypeIndex = nameAndTypeIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.INTERFACE_METHOD_REF;
        }

        public int getClassIndex() {
            return classIndex;
        }

        public int getNameAndTypeIndex() {
            return nameAndTypeIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof InterfaceMethodRefEntry)) {
                return false;
            }

            final InterfaceMethodRefEntry other = (InterfaceMethodRefEntry) obj;

            return other.classIndex == classIndex && other.nameAndTypeIndex == nameAndTypeIndex;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new int[]{classIndex, nameAndTypeIndex});
        }

        @Override
        public String toString() {
            return "InterfaceMethodRefEntry{classIndex=" + classIndex + ", nameAndTypeIndex=" + nameAndTypeIndex + "}";
        }
    }

    public static final class StringEntry extends ConstantPoolEntry {

        private final int stringIndex;

        public StringEntry(int stringIndex) {
            assert stringIndex >= 0 : "string index must be positive";
            this.stringIndex = stringIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.STRING;
        }

        public int getStringIndex() {
            return stringIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof StringEntry)) {
                return false;
            }

            final StringEntry other = (StringEntry) obj;

            return stringIndex == other.stringIndex;
        }

        @Override
        public int hashCode() {
            return stringIndex;
        }

        @Override
        public String toString() {
            return "StringEntry{stringIndex=" + stringIndex + "}";
        }
    }

    public static final class IntegerEntry extends ConstantPoolEntry {

        private final int value;

        public IntegerEntry(int value) {
            this.value = value;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.INTEGER;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof IntegerEntry)) {
                return false;
            }

            final IntegerEntry other = (IntegerEntry) obj;

            return other.value == value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return "IntegerEntry{value=" + value + "}";
        }
    }

    public static final class FloatEntry extends ConstantPoolEntry {

        private final float value;

        public FloatEntry(float value) {
            this.value = value;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.FLOAT;
        }

        public float getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof FloatEntry)) {
                return false;
            }

            final FloatEntry other = (FloatEntry) obj;

            return other.value == value;
        }

        @Override
        public int hashCode() {
            return new Float(value).hashCode();
        }

        @Override
        public String toString() {
            return "FloatEntry{value=" + value + "}";
        }
    }

    public static final class LongEntry extends ConstantPoolEntry {

        private final long value;

        public LongEntry(long value) {
            this.value = value;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.LONG;
        }

        public long getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof LongEntry)) {
                return false;
            }

            final LongEntry other = (LongEntry) obj;

            return other.value == value;
        }

        @Override
        public int hashCode() {
            return new Long(value).hashCode();
        }

        @Override
        public String toString() {
            return "LongEntry{value=" + value + "}";
        }
    }

    public static final class DoubleEntry extends ConstantPoolEntry {

        private final double value;

        public DoubleEntry(double value) {
            this.value = value;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.DOUBLE;
        }

        public double getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof DoubleEntry)) {
                return false;
            }

            final DoubleEntry other = (DoubleEntry) obj;

            return other.value == value;
        }

        @Override
        public int hashCode() {
            return new Double(value).hashCode();
        }

        @Override
        public String toString() {
            return "DoubleEntry{value=" + value + "}";
        }
    }

    public static final class NameAndTypeEntry extends ConstantPoolEntry {

        private final int nameIndex;

        private final int descriptorIndex;

        public NameAndTypeEntry(int nameIndex, int descriptorIndex) {
            assert nameIndex >= 0 : "Name index must be positive";
            assert descriptorIndex >= 0 : "Descriptor index must be positive";

            this.nameIndex = nameIndex;
            this.descriptorIndex = descriptorIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.NAME_AND_TYPE;
        }

        public int getNameIndex() {
            return nameIndex;
        }

        public int getDescriptorIndex() {
            return descriptorIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof NameAndTypeEntry)) {
                return false;
            }

            final NameAndTypeEntry other = (NameAndTypeEntry) obj;

            return other.nameIndex == nameIndex && other.descriptorIndex == descriptorIndex;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new int[]{nameIndex, descriptorIndex});
        }

        @Override
        public String toString() {
            return "NameAndTypeEntry{nameIndex=" + nameIndex + ", descriptorIndex=" + descriptorIndex + "}";
        }
    }

    public static final class UTF8Entry extends ConstantPoolEntry {

        private final String value;

        public UTF8Entry(String value) {
            assert value != null : "value can't be null";
            this.value = value;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.UTF8;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof ConstantPoolEntry)) {
                return false;
            }

            final UTF8Entry other = (UTF8Entry) obj;

            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return "UTF8Entry{value='" + value + "'}";
        }
    }

    public static final class MethodHandleEntry extends ConstantPoolEntry {

        private final ReferenceKind referenceKind;

        private final int referenceIndex;

        public MethodHandleEntry(ReferenceKind referenceKind, int referenceIndex) {
            assert referenceKind != null : "Reference kind can't be null";
            assert referenceIndex >= 0 : "Reference index must be positive";

            this.referenceKind = referenceKind;
            this.referenceIndex = referenceIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.METHOD_HANDLE;
        }

        public ReferenceKind getReferenceKind() {
            return referenceKind;
        }

        public int getReferenceIndex() {
            return referenceIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof MethodHandleEntry)) {
                return false;
            }

            final MethodHandleEntry other = (MethodHandleEntry) obj;

            return other.referenceKind.equals(referenceKind) && other.referenceIndex == referenceIndex;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new int[]{referenceKind.hashCode(), referenceIndex});
        }

        @Override
        public String toString() {
            return "MethodHandleEntry{referenceKind=" + referenceKind + ", referenceIndex=" + referenceIndex + "}";
        }
    }

    public static final class MethodTypeEntry extends ConstantPoolEntry {

        private final int descriptorIndex;

        public MethodTypeEntry(int descriptorIndex) {
            assert descriptorIndex >= 0 : "Descriptor index must be positive";
            this.descriptorIndex = descriptorIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.METHOD_TYPE;
        }

        public int getDescriptorIndex() {
            return descriptorIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof MethodTypeEntry)) {
                return false;
            }

            final MethodTypeEntry other = (MethodTypeEntry) obj;

            return descriptorIndex == other.descriptorIndex;
        }

        @Override
        public int hashCode() {
            return descriptorIndex;
        }

        @Override
        public String toString() {
            return "MethodTypeEntry{descriptorIndex=" + descriptorIndex + "}";
        }
    }

    public static final class InvokeDynamicEntry extends ConstantPoolEntry {

        private final int bootstrapMethodAttributeIndex;

        private final int nameAndTypeIndex;

        public InvokeDynamicEntry(int bootstrapMethodAttributeIndex, int nameAndTypeIndex) {
            assert bootstrapMethodAttributeIndex >= 0 : "Bootstrap method attribute index must be positive";
            assert nameAndTypeIndex >= 0 : "Name-and-type index must be positive";

            this.bootstrapMethodAttributeIndex = bootstrapMethodAttributeIndex;
            this.nameAndTypeIndex = nameAndTypeIndex;
        }

        @Override
        public ConstantPoolEntryTag getTag() {
            return ConstantPoolEntryTag.INVOKE_DYNAMIC;
        }

        public int getBootstrapMethodAttributeIndex() {
            return bootstrapMethodAttributeIndex;
        }

        public int getNameAndTypeIndex() {
            return nameAndTypeIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof InvokeDynamicEntry)) {
                return false;
            }

            final InvokeDynamicEntry other = (InvokeDynamicEntry) obj;

            return other.bootstrapMethodAttributeIndex == bootstrapMethodAttributeIndex
                    && other.nameAndTypeIndex == nameAndTypeIndex;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new int[]{bootstrapMethodAttributeIndex, nameAndTypeIndex});
        }

        @Override
        public String toString() {
            return "InvokeDynamicEntry{"
                    + "bootstrapMethodAttributeIndex=" + bootstrapMethodAttributeIndex + ", "
                    + "nameAndTypeIndex=" + nameAndTypeIndex
                    + "}";
        }
    }

}
