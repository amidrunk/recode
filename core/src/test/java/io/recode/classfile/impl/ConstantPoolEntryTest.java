package io.recode.classfile.impl;

import io.recode.classfile.ConstantPoolEntry;
import io.recode.classfile.ConstantPoolEntryTag;
import io.recode.classfile.ReferenceKind;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class ConstantPoolEntryTest {

    private final ConstantPoolEntry.ClassEntry classEntry = new ConstantPoolEntry.ClassEntry(1234);

    private final ConstantPoolEntry.UTF8Entry utf8Entry = new ConstantPoolEntry.UTF8Entry("foobar");
    private final ConstantPoolEntry.FieldRefEntry fieldRefEntry = new ConstantPoolEntry.FieldRefEntry(1234, 2345);
    private final ConstantPoolEntry.MethodRefEntry methodRefEntry = new ConstantPoolEntry.MethodRefEntry(1234, 2345);
    private final ConstantPoolEntry.InterfaceMethodRefEntry interfaceMethodRefEntry = new ConstantPoolEntry.InterfaceMethodRefEntry(1234, 2345);
    private final ConstantPoolEntry.StringEntry stringEntry = new ConstantPoolEntry.StringEntry(1234);
    private final ConstantPoolEntry.IntegerEntry integerEntry = new ConstantPoolEntry.IntegerEntry(1234);
    private final ConstantPoolEntry.FloatEntry floatEntry = new ConstantPoolEntry.FloatEntry(1234f);
    private final ConstantPoolEntry.LongEntry longEntry = new ConstantPoolEntry.LongEntry(1234L);
    private final ConstantPoolEntry.DoubleEntry doubleEntry = new ConstantPoolEntry.DoubleEntry(1234d);
    private final ConstantPoolEntry.NameAndTypeEntry nameAndTypeEntry = new ConstantPoolEntry.NameAndTypeEntry(1234, 2345);
    private final ConstantPoolEntry.MethodHandleEntry methodHandleEntry = new ConstantPoolEntry.MethodHandleEntry(ReferenceKind.GET_FIELD, 1234);
    private final ConstantPoolEntry.MethodTypeEntry methodTypeEntry = new ConstantPoolEntry.MethodTypeEntry(1234);
    private final ConstantPoolEntry.InvokeDynamicEntry invokeDynamicEntry = new ConstantPoolEntry.InvokeDynamicEntry(1234, 2345);

    @Test(expected = AssertionError.class)
    public void classEntryCannotHaveNegativeNameIndex() {
        new ConstantPoolEntry.ClassEntry(-1);
    }

    @Test
    public void classEntryShouldDefineTagAndNameIndex() {
        assertEquals(1234, classEntry.getNameIndex());
        assertEquals(ConstantPoolEntryTag.CLASS, classEntry.getTag());
    }

    @Test
    public void classEntryShouldBeEqualToItSelf() {
        assertEquals(classEntry, classEntry);
        assertEquals(classEntry.hashCode(), classEntry.hashCode());
    }

    @Test
    public void classEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(classEntry, null);
        assertNotEquals(classEntry, "");
    }

    @Test
    public void classEntriesWithEqualPropertyValuesShouldBeEqual() {
        final ConstantPoolEntry.ClassEntry other = new ConstantPoolEntry.ClassEntry(1234);

        assertEquals(classEntry, other);
        assertEquals(classEntry.hashCode(), other.hashCode());
    }

    @Test
    public void classEntryToStringValueShouldContainNameIndex() {
        assertThat(classEntry.toString(), containsString("1234"));
    }

    @Test(expected = AssertionError.class)
    public void fieldRefEntryCannotHaveNegativeClassIndex() {
        new ConstantPoolEntry.FieldRefEntry(-1, 0);
    }

    @Test(expected = AssertionError.class)
    public void fieldRefEntryCannotHaveNegativeNameAndTypeIndex() {
        new ConstantPoolEntry.FieldRefEntry(0, -1);
    }

    @Test
    public void fieldRefEntryShouldDefineClassIndexAndNameAndTypeIndex() {
        assertEquals(1234, fieldRefEntry.getClassIndex());
        assertEquals(2345, fieldRefEntry.getNameAndTypeIndex());
        assertEquals(ConstantPoolEntryTag.FIELD_REF, fieldRefEntry.getTag());
    }

    @Test
    public void fieldRefEntryShouldBeEqualToItSelf() {
        assertEquals(fieldRefEntry, fieldRefEntry);
        assertEquals(fieldRefEntry.hashCode(), fieldRefEntry.hashCode());
    }

    @Test
    public void fieldRefEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(fieldRefEntry, null);
        assertNotEquals(fieldRefEntry, "foo");
    }

    @Test
    public void fieldRefEntriesWithEqualPropertyValuesShouldBeEqual() {
        final ConstantPoolEntry.FieldRefEntry other = new ConstantPoolEntry.FieldRefEntry(1234, 2345);

        assertEquals(fieldRefEntry, other);
        assertEquals(fieldRefEntry.hashCode(), other.hashCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertThat(fieldRefEntry.toString(), containsString("1234"));
        assertThat(fieldRefEntry.toString(), containsString("2345"));
    }

    @Test(expected = AssertionError.class)
    public void methodRefEntryCannotHaveNegativeClassIndex() {
        new ConstantPoolEntry.MethodRefEntry(-1, 0);
    }

    @Test(expected = AssertionError.class)
    public void methodRefEntryCannotHaveNegativeNameAndTypeIndex() {
        new ConstantPoolEntry.MethodRefEntry(0, -1);
    }

    @Test
    public void methodRefEntryShouldDefineProperties() {
        assertEquals(1234, methodRefEntry.getClassIndex());
        assertEquals(2345, methodRefEntry.getNameAndTypeIndex());
        assertEquals(ConstantPoolEntryTag.METHOD_REF, methodRefEntry.getTag());
    }

    @Test
    public void methodRefEntryShouldBeEqualToItSelf() {
        assertEquals(methodRefEntry, methodRefEntry);
        assertEquals(methodRefEntry.hashCode(), methodRefEntry.hashCode());
    }

    @Test
    public void methodRefEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(methodRefEntry, null);
        assertNotEquals(methodRefEntry, "foo");
    }

    @Test
    public void methodRefEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.MethodRefEntry other = new ConstantPoolEntry.MethodRefEntry(1234, 2345);

        assertEquals(methodRefEntry, other);
        assertEquals(methodRefEntry.hashCode(), other.hashCode());
    }

    @Test
    public void methodRefToStringValueShouldContainPropertyValues() {
        assertThat(methodRefEntry.toString(), containsString("1234"));
        assertThat(methodRefEntry.toString(), containsString("2345"));
    }

    @Test(expected = AssertionError.class)
    public void interfaceMethodRefCannotHaveNegativeClassIndex() {
        new ConstantPoolEntry.InterfaceMethodRefEntry(-1, 0);
    }

    @Test(expected = AssertionError.class)
    public void interfaceMethodRefCannotHaveNegativeNameAndTypeIndex() {
        new ConstantPoolEntry.InterfaceMethodRefEntry(0, -1);
    }

    @Test
    public void interfaceMethodRefEntryShouldInitializeProperties() {
        assertEquals(1234, interfaceMethodRefEntry.getClassIndex());
        assertEquals(2345, interfaceMethodRefEntry.getNameAndTypeIndex());
        assertEquals(ConstantPoolEntryTag.INTERFACE_METHOD_REF, interfaceMethodRefEntry.getTag());
    }

    @Test
    public void interfaceMethodRefEntryShouldBeEqualToItSelf() {
        assertEquals(interfaceMethodRefEntry, interfaceMethodRefEntry);
        assertEquals(interfaceMethodRefEntry.hashCode(), interfaceMethodRefEntry.hashCode());
    }

    @Test
    public void interfaceMethodRefEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(interfaceMethodRefEntry, null);
        assertNotEquals(interfaceMethodRefEntry, "foo");
    }

    @Test
    public void interfaceMethodRefEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.InterfaceMethodRefEntry other = new ConstantPoolEntry.InterfaceMethodRefEntry(1234, 2345);

        assertEquals(interfaceMethodRefEntry, other);
        assertEquals(interfaceMethodRefEntry.hashCode(), other.hashCode());
    }

    @Test
    public void interfaceMethodEntryToStringValueShouldContainPropertyValues() {
        assertThat(interfaceMethodRefEntry.toString(), containsString("1234"));
        assertThat(interfaceMethodRefEntry.toString(), containsString("2345"));
    }

    @Test(expected = AssertionError.class)
    public void stringEntryCannotHaveNegativeStringIndex() {
        new ConstantPoolEntry.StringEntry(-1);
    }

    @Test
    public void stringEntryShouldHaveStringIndex() {
        assertEquals(1234, stringEntry.getStringIndex());
        assertEquals(ConstantPoolEntryTag.STRING, stringEntry.getTag());
    }

    @Test
    public void stringEntryShouldBeEqualToItSelf() {
        assertEquals(stringEntry, stringEntry);
        assertEquals(stringEntry.hashCode(), stringEntry.hashCode());
    }

    @Test
    public void stringEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(stringEntry, null);
        assertNotEquals(stringEntry, "foo");
    }

    @Test
    public void stringEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.StringEntry other = new ConstantPoolEntry.StringEntry(1234);

        assertEquals(stringEntry, other);
        assertEquals(stringEntry.hashCode(), other.hashCode());
    }

    @Test
    public void stringEntryToStringValueShouldContainStringIndex() {
        assertThat(stringEntry.toString(), containsString("1234"));
    }

    @Test
    public void integerEntryShouldContainValueAndTag() {
        assertEquals(1234, integerEntry.getValue());
        assertEquals(ConstantPoolEntryTag.INTEGER, integerEntry.getTag());
    }

    @Test
    public void integerEntryShouldBeEqualToItSelf() {
        assertEquals(integerEntry, integerEntry);
        assertEquals(integerEntry.hashCode(), integerEntry.hashCode());
    }

    @Test
    public void integerEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(integerEntry, null);
        assertNotEquals(integerEntry, "foo");
    }

    @Test
    public void integerEntriesWithEqualPropertyValuesShouldBeEqual() {
        final ConstantPoolEntry.IntegerEntry other = new ConstantPoolEntry.IntegerEntry(1234);

        assertEquals(integerEntry, other);
        assertEquals(integerEntry.hashCode(), other.hashCode());
    }

    @Test
    public void floatEntryShouldSpecifyValueAndTag() {
        assertEquals(1234f, floatEntry.getValue(), .1f);
        assertEquals(ConstantPoolEntryTag.FLOAT, floatEntry.getTag());
    }

    @Test
    public void floatEntryShouldBeEqualToItSelf() {
        assertEquals(floatEntry, floatEntry);
        assertEquals(floatEntry.hashCode(), floatEntry.hashCode());
    }

    @Test
    public void floatEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(floatEntry, null);
        assertNotEquals(floatEntry, "foo");
    }

    @Test
    public void floatEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.FloatEntry other = new ConstantPoolEntry.FloatEntry(1234f);

        assertEquals(floatEntry, other);
        assertEquals(floatEntry.hashCode(), other.hashCode());
    }

    @Test
    public void floatEntryToStringValueShouldContainPropertyValue() {
        assertThat(floatEntry.toString(), containsString("1234"));
    }

    @Test
    public void longEntryShouldDefineValueAndTag() {
        assertEquals(1234L, longEntry.getValue());
        assertEquals(ConstantPoolEntryTag.LONG, longEntry.getTag());
    }

    @Test
    public void longEntryShouldBeEqualToItSelf() {
        assertEquals(longEntry, longEntry);
        assertEquals(longEntry.hashCode(), longEntry.hashCode());
    }

    @Test
    public void longEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.LongEntry other = new ConstantPoolEntry.LongEntry(1234L);
        assertEquals(longEntry, other);
        assertEquals(longEntry.hashCode(), other.hashCode());
    }

    @Test
    public void longEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(longEntry, null);
        assertNotEquals(longEntry, "foo");
    }

    @Test
    public void longEntryToStringValueShouldContainValue() {
        assertThat(longEntry.toString(), containsString("1234"));
    }

    @Test
    public void doubleEntryShouldDefineValueAndTag() {
        assertEquals(1234d, doubleEntry.getValue(), .1d);
        assertEquals(ConstantPoolEntryTag.DOUBLE, doubleEntry.getTag());
    }

    @Test
    public void doubleEntryShouldBeEqualToItSelf() {
        assertEquals(doubleEntry, doubleEntry);
        assertEquals(doubleEntry.hashCode(), doubleEntry.hashCode());
    }

    @Test
    public void doubleEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(doubleEntry, null);
        assertNotEquals(doubleEntry, "foo");
    }

    @Test
    public void doubleEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.DoubleEntry other = new ConstantPoolEntry.DoubleEntry(1234d);

        assertEquals(doubleEntry, other);
        assertEquals(doubleEntry.hashCode(), other.hashCode());
    }

    @Test
    public void doubleEntryToStringValueShouldContainValue() {
        assertThat(doubleEntry.toString(), containsString("1234"));
    }

    @Test(expected = AssertionError.class)
    public void nameAndTypeEntryCannotHaveNegativeNameIndex() {
        new ConstantPoolEntry.NameAndTypeEntry(-1, 0);
    }

    @Test(expected = AssertionError.class)
    public void nameAndTypeEntryCannotHaveNegativeDescriptorIndex() {
        new ConstantPoolEntry.NameAndTypeEntry(0, -1);
    }

    @Test
    public void nameAndTypeEntryShouldHaveDefinedProperties() {
        assertEquals(1234, nameAndTypeEntry.getNameIndex());
        assertEquals(2345, nameAndTypeEntry.getDescriptorIndex());
        assertEquals(ConstantPoolEntryTag.NAME_AND_TYPE, nameAndTypeEntry.getTag());
    }

    @Test
    public void nameAndTypeEntryShouldBeEqualToItself() {
        assertEquals(nameAndTypeEntry, nameAndTypeEntry);
    }

    @Test
    public void nameAndTypeEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(nameAndTypeEntry, null);
        assertNotEquals(nameAndTypeEntry, "foo");
    }

    @Test
    public void nameAndTypeIndexEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.NameAndTypeEntry other = new ConstantPoolEntry.NameAndTypeEntry(1234, 2345);

        assertEquals(nameAndTypeEntry, other);
        assertEquals(nameAndTypeEntry.hashCode(), other.hashCode());
    }

    @Test
    public void nameAndTypeIndexEntryToStringValueShouldContainPropertyValues() {
        assertThat(nameAndTypeEntry.toString(), containsString("1234"));
        assertThat(nameAndTypeEntry.toString(), containsString("2345"));
    }

    @Test
    public void integerEntryToStringValueShouldContainValue() {
        assertThat(integerEntry.toString(), containsString("1234"));
    }


    @Test(expected = AssertionError.class)
    public void utf8EntryCannotHaveNullString() {
        new ConstantPoolEntry.UTF8Entry(null);
    }

    @Test
    public void utf8EntryShouldSpecifyTagAndValue() {
        assertEquals(ConstantPoolEntryTag.UTF8, utf8Entry.getTag());
        assertEquals("foobar", utf8Entry.getValue());
    }

    @Test
    public void utf8EntryShouldBeEqualToItSelf() {
        assertEquals(utf8Entry, utf8Entry);
        assertEquals(utf8Entry.hashCode(), utf8Entry.hashCode());
    }

    @Test
    public void utf8EntryShouldBeEqualToInstanceWithEqualValue() {
        final ConstantPoolEntry.UTF8Entry other = new ConstantPoolEntry.UTF8Entry("foobar");

        assertEquals(utf8Entry, other);
        assertEquals(utf8Entry.hashCode(), other.hashCode());
    }

    @Test
    public void utf8EntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(utf8Entry, null);
        assertNotEquals(utf8Entry, "foo");
    }

    @Test
    public void utf8EntryToStringValueShouldContainValue() {
        assertThat(utf8Entry.toString(), containsString("foobar"));
    }

    @Test(expected = AssertionError.class)
    public void methodHandleEntryCannotHaveNullReferenceKind() {
        new ConstantPoolEntry.MethodHandleEntry(null, 0);
    }

    @Test(expected = AssertionError.class)
    public void methodHandleEntryCannotHaveNegativeReferenceIndex() {
        new ConstantPoolEntry.MethodHandleEntry(ReferenceKind.GET_FIELD, -1);
    }

    @Test
    public void methodHandleEntryShouldHaveKindIndexAndTag() {
        assertEquals(ReferenceKind.GET_FIELD, methodHandleEntry.getReferenceKind());
        assertEquals(1234, methodHandleEntry.getReferenceIndex());
        assertEquals(ConstantPoolEntryTag.METHOD_HANDLE, methodHandleEntry.getTag());
    }

    @Test
    public void methodHandleShouldBeEqualToItSelf() {
        assertEquals(methodHandleEntry, methodHandleEntry);
        assertEquals(methodHandleEntry.hashCode(), methodHandleEntry.hashCode());
    }

    @Test
    public void methodHandleEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(methodHandleEntry, null);
        assertNotEquals(methodHandleEntry, "foo");
    }

    @Test
    public void methodHandleEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.MethodHandleEntry other = new ConstantPoolEntry.MethodHandleEntry(ReferenceKind.GET_FIELD, 1234);

        assertEquals(methodHandleEntry, other);
        assertEquals(methodHandleEntry.hashCode(), other.hashCode());
    }

    @Test
    public void methodHandleEntryToStringValueShouldContainPropertyValues() {
        assertThat(methodHandleEntry.toString(), containsString("GET_FIELD"));
        assertThat(methodHandleEntry.toString(), containsString("1234"));
    }

    @Test(expected = AssertionError.class)
    public void methodTypeCannotHaveNegativeDescriptorIndex() {
        new ConstantPoolEntry.MethodTypeEntry(-1);
    }

    @Test
    public void methodTypeEntryShouldHaveDescriptorIndexAndTag() {
        assertEquals(1234, methodTypeEntry.getDescriptorIndex());
        assertEquals(ConstantPoolEntryTag.METHOD_TYPE, methodTypeEntry.getTag());
    }

    @Test
    public void methodTypeShouldBeEqualToItSelf() {
        assertEquals(methodTypeEntry, methodTypeEntry);
        assertEquals(methodTypeEntry.hashCode(), methodTypeEntry.hashCode());
    }

    @Test
    public void methodTypeEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(methodTypeEntry, null);
        assertNotEquals(methodTypeEntry, "foo");
    }

    @Test
    public void methodTypeEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.MethodTypeEntry other = new ConstantPoolEntry.MethodTypeEntry(1234);

        assertEquals(methodTypeEntry, other);
        assertEquals(methodTypeEntry.hashCode(), other.hashCode());
    }

    @Test
    public void methodTypeEntryToStringValueShouldContainDescriptorIndex() {
        assertThat(methodTypeEntry.toString(), containsString("1234"));
    }

    @Test(expected = AssertionError.class)
    public void invokeDynamicEntryCannotHaveNegativeBootstrapMethodAttributeIndex() {
        new ConstantPoolEntry.InvokeDynamicEntry(-1, 0);
    }

    @Test(expected = AssertionError.class)
    public void invokeDynamicEntryCannotHaveNegativeNameAndTypeIndex() {
        new ConstantPoolEntry.InvokeDynamicEntry(0, -1);
    }

    @Test
    public void invokeDynamicEntryShouldHaveInitializedProperties() {
        assertEquals(1234, invokeDynamicEntry.getBootstrapMethodAttributeIndex());
        assertEquals(2345, invokeDynamicEntry.getNameAndTypeIndex());
        assertEquals(ConstantPoolEntryTag.INVOKE_DYNAMIC, invokeDynamicEntry.getTag());
    }

    @Test
    public void invokeDynamicEntryShouldBeEqualToItSelf() {
        assertEquals(invokeDynamicEntry, invokeDynamicEntry);
        assertEquals(invokeDynamicEntry.hashCode(), invokeDynamicEntry.hashCode());
    }

    @Test
    public void invokeDynamicEntryShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(invokeDynamicEntry, null);
        assertNotEquals(invokeDynamicEntry, "foo");
    }

    @Test
    public void invokeDynamicEntriesWithEqualPropertiesShouldBeEqual() {
        final ConstantPoolEntry.InvokeDynamicEntry other = new ConstantPoolEntry.InvokeDynamicEntry(1234, 2345);

        assertEquals(invokeDynamicEntry, other);
        assertEquals(invokeDynamicEntry.hashCode(), other.hashCode());
    }

    @Test
    public void invokeDynamicEntryToStringValueShouldContainPropertyValues() {
        assertThat(invokeDynamicEntry.toString(), containsString("1234"));
        assertThat(invokeDynamicEntry.toString(), containsString("2345"));
    }

    @Test
    public void asShouldNotAcceptNullType() {
        assertThrown(() -> invokeDynamicEntry.as(null), AssertionError.class);
    }

    @Test
    public void asShouldFailIfTypeIsInvalid() {
        assertThrown(() -> invokeDynamicEntry.as(ConstantPoolEntry.UTF8Entry.class), IllegalArgumentException.class);
    }

    @Test
    public void asShouldReturnSameInstanceIfCompatible() {
        final ConstantPoolEntry.InvokeDynamicEntry castInstance = invokeDynamicEntry.as(ConstantPoolEntry.InvokeDynamicEntry.class);

        assertEquals(invokeDynamicEntry, castInstance);
    }

}
