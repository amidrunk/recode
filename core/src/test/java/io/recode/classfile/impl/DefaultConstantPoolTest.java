package io.recode.classfile.impl;

import io.recode.classfile.*;
import org.junit.Test;

import java.util.Arrays;

import static io.recode.classfile.ConstantPoolEntry.*;
import static io.recode.test.Assertions.assertThrown;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class DefaultConstantPoolTest {

    @Test
    public void constantPoolBuilderShouldNotAcceptNullEntryWhenAdding() {
        assertThrown(() -> new DefaultConstantPool.Builder()
                .addEntry(null), AssertionError.class);
    }

    @Test
    public void builderShouldCreateConstantPoolWithAddedEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertArrayEquals(new ConstantPoolEntry[]{
                new UTF8Entry("foobar")
        }, constantPool.getEntries().toArray());
    }

    @Test
    public void longEntryShouldOccupyTwoEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.LongEntry(1234L))
                .create();

        assertArrayEquals(new ConstantPoolEntry[]{
                new ConstantPoolEntry.LongEntry(1234L), null
        }, constantPool.getEntries().toArray());
    }

    @Test
    public void doubleEntryShouldOccupyTwoEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.DoubleEntry(1234d))
                .create();

        assertArrayEquals(new ConstantPoolEntry[]{
                new ConstantPoolEntry.DoubleEntry(1234d), null
        }, constantPool.getEntries().toArray());
    }

    @Test
    public void constantPoolsWithEqualEntriesShouldBeEqual() {
        final DefaultConstantPool pool1 = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        final DefaultConstantPool pool2 = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        assertEquals(pool1, pool2);
        assertEquals(pool1.hashCode(), pool2.hashCode());
    }

    @Test
    public void toStringValueShouldContainEntries() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertThat(constantPool.toString(), containsString("foobar"));
    }

    @Test
    public void constantPoolShouldNotBeEqualToNullOrDifferentType() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertNotEquals(constantPool, null);
        assertNotEquals(constantPool, "foo");
    }

    @Test
    public void constantPoolShouldBeEqualToItSelf() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertEquals(constantPool, constantPool);
        assertEquals(constantPool.hashCode(), constantPool.hashCode());
    }

    @Test
    public void getClassNameShouldFailIfIndexIsInvalid() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        try {
            constantPool.getClassName(-1);
            fail();
        } catch (AssertionError e) {
        }

        try {
            constantPool.getClassName(2);
            fail();
        } catch (AssertionError e) {
        }
    }

    @Test
    public void getClassNameShouldFailIfEntryTypesAreNotCorrect() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foobar"));

        assertThrown(() -> constantPool.getClassName(1), ClassFileFormatException.class);
    }

    @Test
    public void getClassNameShouldReturnNameOfClass() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .addEntry(new ClassEntry(1))
                .create();

        assertEquals("foobar", constantPool.getClassName(2));
    }

    @Test
    public void getStringShouldNotAcceptInvalidIndex() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        try {
            constantPool.getString(-1);
            fail();
        } catch (AssertionError e) {
        }

        try {
            constantPool.getString(2);
            fail();
        } catch (AssertionError e) {
        }
    }

    @Test
    public void getStringShouldReturnUTF8Value() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foobar"))
                .create();

        assertEquals("foobar", constantPool.getString(1));
    }

    @Test
    public void getStringShouldNotAcceptInvalidEntryType() {
        final DefaultConstantPool constantPool = createConstantPool(new ClassEntry(1));

        assertThrown(() -> constantPool.getString(1), ClassFileFormatException.class);
    }

    @Test
    public void getEntryShouldFailForNegativeOrZeroIndex() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        assertThrown(() -> constantPool.getEntry(-1), AssertionError.class);
        assertThrown(() -> constantPool.getEntry(0), AssertionError.class);
    }

    @Test
    public void getEntryShouldFailIfIndexIsOutOfBounds() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        assertThrown(() -> constantPool.getEntry(2), IndexOutOfBoundsException.class);
    }

    @Test
    public void getEntryShouldReturnEntryAtIndex() {
        final UTF8Entry entry = new UTF8Entry("foo");
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(entry)
                .create();

        assertEquals(entry, constantPool.getEntry(1));
    }

    @Test
    public void getEntriesShouldNotAcceptNullArg() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .create();

        assertThrown(() -> constantPool.getEntries(null), AssertionError.class);
    }

    @Test
    public void getEntriesShouldFailIfAnyIndexIsInvalid() {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder()
                .addEntry(new UTF8Entry("foo"))
                .create();

        assertThrown(() -> constantPool.getEntries(new int[]{1, 2}), IndexOutOfBoundsException.class);
    }

    @Test
    public void getEntriesShouldReturnMatchingEntries() {
        final ConstantPoolEntry[] expectedEntries = {new UTF8Entry("foo"), new UTF8Entry("bar")};
        final DefaultConstantPool constantPool = createConstantPool(expectedEntries);

        assertArrayEquals(expectedEntries, constantPool.getEntries(new int[]{1, 2}));
    }

    @Test
    public void getEntryWithTypeShouldNotAcceptNullType() {
        assertThrown(() -> createConstantPool(new UTF8Entry("foo")).getEntry(1, null), AssertionError.class);
    }

    @Test
    public void getEntryWithTypeShouldFailForIncorrectType() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> constantPool.getEntry(1, NameAndTypeEntry.class), IllegalArgumentException.class);
    }

    @Test
    public void getEntryShouldReturnMatchingEntry() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));
        final UTF8Entry entry = constantPool.getEntry(1, UTF8Entry.class);

        assertEquals(new UTF8Entry("foo"), entry);
    }

    @Test
    public void getFieldDescriptorShouldFailIfEntryIsNotAFieldRef() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> constantPool.getFieldRefDescriptor(1), IllegalArgumentException.class);
    }

    @Test
    public void getFieldDescriptorShouldResolveEntriesAndReturnDescriptor() {
        final DefaultConstantPool constantPool = createConstantPool(
                new FieldRefEntry(2, 3),
                new ClassEntry(4),
                new NameAndTypeEntry(5, 6),
                new UTF8Entry("MyClass"),
                new UTF8Entry("myField"),
                new UTF8Entry("I")
        );

        final FieldRefDescriptor fieldRefDescriptor = constantPool.getFieldRefDescriptor(1);

        assertEquals("MyClass", fieldRefDescriptor.getClassName());
        assertEquals("myField", fieldRefDescriptor.getName());
        assertEquals("I", fieldRefDescriptor.getDescriptor());
    }

    @Test
    public void getLongShouldFailIfEntryIsNotALongEntry() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> constantPool.getLong(1), IllegalArgumentException.class);
    }

    @Test
    public void getLongShouldReturnValueOfLongEntry() {
        final DefaultConstantPool constantPool = createConstantPool(new LongEntry(1234L));

        assertEquals(1234L, constantPool.getLong(1));
    }

    @Test
    public void getNameAndTypeDescriptorShouldFailIfIndexIsOtherEntry() {
        final DefaultConstantPool pool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> pool.getNameAndTypeDescriptor(1), IllegalArgumentException.class);
    }

    @Test
    public void getNameAndTypeDescriptorShouldReturnNameAndTypeValues() {
        final DefaultConstantPool pool = createConstantPool(
                new NameAndTypeEntry(2, 3),
                new UTF8Entry("foo"), new UTF8Entry("()V"));

        final NameAndTypeDescriptor d = pool.getNameAndTypeDescriptor(1);

        assertEquals("foo", d.getName());
        assertEquals("()V", d.getDescriptor());
    }

    @Test
    public void getInterfaceMethodRefDescriptorShouldFailIfEntryIsOfOtherType() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> constantPool.getInterfaceMethodRefDescriptor(1), IllegalArgumentException.class);
    }

    @Test
    public void getInterfaceMethodRefShouldReturnDescriptorForValidEntry() {
        final DefaultConstantPool constantPool = createConstantPool(
                new InterfaceMethodRefEntry(2, 3),
                new ClassEntry(4),
                new NameAndTypeEntry(5, 6),
                new UTF8Entry("ExampleClass"),
                new UTF8Entry("exampleMethod"),
                new UTF8Entry("()V")
        );

        final InterfaceMethodRefDescriptor descriptor = constantPool.getInterfaceMethodRefDescriptor(1);

        assertEquals("ExampleClass", descriptor.getClassName());
        assertEquals("exampleMethod", descriptor.getMethodName());
        assertEquals("()V", descriptor.getDescriptor());
    }

    @Test
    public void getInvokeDynamicDescriptorShouldReturnValidEntry() {
        final DefaultConstantPool constantPool = createConstantPool(
                new InvokeDynamicEntry(1234, 2),
                new NameAndTypeEntry(3, 4),
                new UTF8Entry("call"),
                new UTF8Entry("()V")
        );

        final InvokeDynamicDescriptor descriptor = constantPool.getInvokeDynamicDescriptor(1);

        assertEquals(1234, descriptor.getBootstrapMethodAttributeIndex());
        assertEquals("call", descriptor.getMethodName());
        assertEquals("()V", descriptor.getMethodDescriptor());
    }

    @Test
    public void getInvokeDynamicEntryShouldFailIfEntryIsOtherType() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> constantPool.getInvokeDynamicDescriptor(1), IllegalArgumentException.class);
    }

    @Test
    public void getMethodHandleDescriptorShouldFailIfEntryIsOfIncorrectType() {
        final DefaultConstantPool pool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> pool.getMethodHandleDescriptor(1), IllegalArgumentException.class);
    }

    @Test
    public void getMethodHandleDescriptorShouldReturnDescriptor() {
        final DefaultConstantPool constantPool = createConstantPool(
                new MethodHandleEntry(ReferenceKind.GET_FIELD, 2),
                new MethodRefEntry(3, 4),
                new ClassEntry(5),
                new NameAndTypeEntry(6, 7),
                new UTF8Entry("Foo"),
                new UTF8Entry("bar"),
                new UTF8Entry("()V")
        );

        final MethodHandleDescriptor descriptor = constantPool.getMethodHandleDescriptor(1);

        assertEquals(ReferenceKind.GET_FIELD, descriptor.getReferenceKind());
        assertEquals("Foo", descriptor.getClassName());
        assertEquals("bar", descriptor.getMethodName());
        assertEquals("()V", descriptor.getMethodDescriptor());
    }

    @Test
    public void getMethodTypeDescriptorShouldFailIfEntryIsIncorrect() {
        final DefaultConstantPool pool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> pool.getMethodTypeDescriptor(1), IllegalArgumentException.class);
    }

    @Test
    public void getMethodTypeDescriptorShouldCreateDescriptorFromEntry() {
        final DefaultConstantPool constantPool = createConstantPool(new MethodTypeEntry(2), new UTF8Entry("()V"));

        final MethodTypeDescriptor descriptor = constantPool.getMethodTypeDescriptor(1);

        assertEquals("()V", descriptor.getDescriptor());
    }

    @Test
    public void getDescriptorsShouldNotAcceptNullIndices() {
        final DefaultConstantPool pool = createConstantPool();

        assertThrown(() -> pool.getDescriptors(null), AssertionError.class);
    }

    @Test
    public void getDescriptorsCanCreateSupportedDescriptors() {
        final DefaultConstantPool pool = createConstantPool(
                new MethodHandleEntry(ReferenceKind.INVOKE_STATIC, 2),
                new MethodRefEntry(3, 4),
                new ClassEntry(5),
                new NameAndTypeEntry(6, 7),
                new UTF8Entry("ExampleClass"),
                new UTF8Entry("exampleMethod"),
                new UTF8Entry("()V"),
                new MethodTypeEntry(7)
        );

        final ConstantPoolEntryDescriptor[] descriptors1 = pool.getDescriptors(new int[]{1});

        assertEquals(ConstantPoolEntryTag.METHOD_HANDLE, descriptors1[0].getTag());

        final MethodHandleDescriptor methodHandle = (MethodHandleDescriptor) descriptors1[0];

        assertEquals("ExampleClass", methodHandle.getClassName());
        assertEquals(ReferenceKind.INVOKE_STATIC, methodHandle.getReferenceKind());
        assertEquals("exampleMethod", methodHandle.getMethodName());
        assertEquals("()V", methodHandle.getMethodDescriptor());

        final ConstantPoolEntryDescriptor[] descriptors2 = pool.getDescriptors(new int[]{8});

        assertEquals(ConstantPoolEntryTag.METHOD_TYPE, descriptors2[0].getTag());

        final MethodTypeDescriptor methodType = (MethodTypeDescriptor) descriptors2[0];

        assertEquals("()V", methodType.getDescriptor());
    }

    @Test
    public void getMethodRefDescriptorShouldFailIfEntryIsOfIncorrectType() {
        final DefaultConstantPool pool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> pool.getMethodRefDescriptor(1), IllegalArgumentException.class);
    }

    @Test
    public void getMethodRefDescriptorShouldReturnDescriptorForMethodRefEntry() {
        final DefaultConstantPool constantPool = createConstantPool(
                new MethodRefEntry(2, 3),
                new ClassEntry(4),
                new NameAndTypeEntry(5, 6),
                new UTF8Entry("Foo"),
                new UTF8Entry("bar"),
                new UTF8Entry("()V")
        );

        final MethodRefDescriptor descriptor = constantPool.getMethodRefDescriptor(1);

        assertEquals("Foo", descriptor.getClassName());
        assertEquals("bar", descriptor.getMethodName());
        assertEquals("()V", descriptor.getDescriptor());
    }

    @Test
    public void getDescriptorShouldNotAcceptInvalidArguments() {
        final DefaultConstantPool cp = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> cp.getDescriptor(0, MethodRefDescriptor.class), AssertionError.class);
        assertThrown(() -> cp.getDescriptor(1, null), AssertionError.class);
    }

    @Test
    public void getDescriptorShouldFailIfEntryTypeIsNotCorrect() {
        final DefaultConstantPool constantPool = createConstantPool(new UTF8Entry("foo"));

        assertThrown(() -> constantPool.getDescriptor(1, MethodRefDescriptor.class), IllegalArgumentException.class);
    }

    @Test
    public void getDescriptorShouldReturnMatchingDescriptor() {
        final DefaultConstantPool constantPool = createConstantPool(
                new FieldRefEntry(2, 3),
                new ClassEntry(4),
                new NameAndTypeEntry(5, 6),
                new UTF8Entry("Foo"),
                new UTF8Entry("bar"),
                new UTF8Entry("I")
        );

        final FieldRefDescriptor descriptor = constantPool.getDescriptor(1, FieldRefDescriptor.class);

        assertEquals("Foo", descriptor.getClassName());
        assertEquals("bar", descriptor.getName());
        assertEquals("I", descriptor.getDescriptor());
    }

    private DefaultConstantPool createConstantPool(ConstantPoolEntry ... entries) {
        final DefaultConstantPool.Builder builder = new DefaultConstantPool.Builder();

        Arrays.stream(entries).forEach(builder::addEntry);

        return builder.create();
    }

}
