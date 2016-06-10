package io.recode.classfile.impl;

import io.recode.Caller;
import io.recode.classfile.*;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class ClassFileReaderImplTest {

    private final ClassFileReader classFileReader = new ClassFileReaderImpl();

    @Test(expected = AssertionError.class)
    public void readShouldNotAcceptNullInputStream() throws IOException {
        classFileReader.read(null);
    }

    @Test
    public void readerShouldResolveVersion() {
        final ClassFile classFile = classFileOf(getClass());

        assertEquals(0, classFile.getMinorVersion());
        assertTrue(classFile.getMajorVersion() >= 52);
    }

    @Test
    public void readShouldFailIfStreamDoesNotStartWithMagicNumber() throws IOException {
        try {
            classFileReader.read(new ByteArrayInputStream("foobar".getBytes()));
            fail();
        } catch (ClassFormatError classFormatError) {
            assertThat(classFormatError.getMessage(), containsString("0xCAFEBABE"));
        }
    }

    @Test
    public void constantPoolShouldContainConstantsInClass() throws Exception {
        final ConstantPool constantPool = classFileOf(getClass()).getConstantPool();

        assertThat(constantPool.getEntries(), hasItem(new ConstantPoolEntry.UTF8Entry("foobar")));
        assertThat(constantPool.getEntries(), hasItem(new ConstantPoolEntry.UTF8Entry("constantPoolShouldContainConstantsInClass")));
    }

    @Test
    public void classNameAndSuperClassAndInterfacesShouldBeResolved() throws Exception {
        final ClassFile classFile = classFileOf(String.class);

        assertTrue(Modifier.isPublic(classFile.getAccessFlags()));
        assertTrue(Modifier.isFinal(classFile.getAccessFlags()));
        assertEquals(String.class.getName(), classFile.getName());
        assertEquals(Object.class.getName(), classFile.getSuperClassName());
        assertArrayEquals(new String[]{
                "java.io.Serializable",
                "java.lang.Comparable",
                "java.lang.CharSequence"
        }, classFile.getInterfaceNames().toArray());
    }

    @Test
    public void fieldsShouldBeRead() {
        final ClassFile classFile = classFileOf(String.class);

        assertTrue(classFile.getFields().stream().filter(f -> f.getName().equals("value")).findFirst().isPresent());
        assertTrue(classFile.getFields().stream().filter(f -> f.getName().equals("hash")).findFirst().isPresent());
        assertTrue(classFile.getFields().stream().filter(f -> f.getName().equals("serialVersionUID")).findFirst().isPresent());
        assertTrue(classFile.getFields().stream().filter(f -> f.getName().equals("serialPersistentFields")).findFirst().isPresent());
    }

    @Test
    public void methodsShouldBeRead() {
        final ClassFile classFile = classFileOf(String.class);

        assertTrue(classFile.getMethods().stream().filter(m -> m.getName().equals("substring")).findFirst().isPresent());
        assertTrue(classFile.getMethods().stream().filter(m -> m.getName().equals("toString")).findFirst().isPresent());
        assertTrue(classFile.getMethods().stream().filter(m -> m.getName().equals("length")).findFirst().isPresent());
    }

    @Test
    public void constructorsShouldBeRead() {
        final ClassFile classFile = classFileOf(getClass());
        final Optional<Constructor> result = classFile.getConstructors().stream()
                .filter(c -> c.getSignature().toString().equals("()V"))
                .findFirst();

        assertTrue(result.isPresent());
    }

    @Test
    public void methodBodyShouldBeRead() throws IOException {
        final ClassFile classFile = classFileOf(getClass());

        final Method thisMethod = classFile.getMethods().stream()
                .filter(m -> m.getName().equals("methodBodyShouldBeRead"))
                .findFirst()
                .get();

        final CodeAttribute codeAttribute = thisMethod.getCode();

        assertNotNull(codeAttribute);
        assertNotEquals(0, IOUtils.toByteArray(codeAttribute.getCode()).length);
        assertTrue(codeAttribute.getMaxStack() > 0);
        assertTrue(codeAttribute.getMaxLocals() > 0);
    }

    @Test
    public void tryCatchShouldBeIncludedInMethod() {
        final Method methodWithTryCatch = getMethod("methodWithTryCatch");

        final List<ExceptionTableEntry> exceptionTable = methodWithTryCatch.getCode().getExceptionTable();
        assertEquals(1, exceptionTable.size());

        final ExceptionTableEntry entry = exceptionTable.get(0);

        assertEquals(RuntimeException.class, entry.getCatchType());
    }

    @Test
    public void localVariablesShouldBeParsed() {
        final Method method = getMethod("methodWithLocals");
        final LocalVariableTable localVariableTable = (LocalVariableTable) method.getCode().getAttributes().stream()
                .filter(a -> a.getName().equals(LocalVariableTable.ATTRIBUTE_NAME))
                .findFirst().get();

        final LocalVariable str1Variable = localVariableTable.getLocalVariables().stream().filter(lv -> lv.getName().equals("str1")).findFirst().get();
        final LocalVariable i1Variable = localVariableTable.getLocalVariables().stream().filter(lv -> lv.getName().equals("i1")).findFirst().get();

        assertEquals("str1", str1Variable.getName());
        assertEquals(String.class, str1Variable.getType());

        assertEquals("i1", i1Variable.getName());
        assertEquals(int.class, i1Variable.getType());
    }

    @Test
    public void lineNumbersShouldBeParsed() {
        final LineNumberTable lineNumberTable = (LineNumberTable) getMethod("lineNumbersShouldBeParsed").getCode().getAttributes().stream()
                .filter(a -> a.getName().equals(LineNumberTable.ATTRIBUTE_NAME))
                .findFirst().get();

        assertEquals(1L, lineNumberTable.getEntries().stream().filter(e -> e.getLineNumber() == Caller.me().getLineNumber()).count());
        assertEquals(1L, lineNumberTable.getEntries().stream().filter(e -> e.getLineNumber() == Caller.me().getLineNumber()).count());
    }

    @Test
    public void bootstrapMethodAttributeShouldBeRead() {
        final ClassFile classFile = classFileOf(ExampleClass.class);
        final BootstrapMethodsAttribute attribute = (BootstrapMethodsAttribute) classFile.getAttributes().stream()
                .filter(a -> a.getName().equals(BootstrapMethodsAttribute.ATTRIBUTE_NAME))
                .findFirst().get();

        assertFalse(attribute.getBootstrapMethods().isEmpty());
    }

    private void methodWithLocals() {
        final String str1 = "foo";
        final int i1 = 1234;
    }

    private void methodWithTryCatch() {
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
        }
    }

    private Method getMethod(String name) {
        final ClassFile classFile = classFileOf(getClass());
        return classFile.getMethods().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst().get();
    }

    protected ClassFile classFileOf(Class<?> clazz) {
        try {
            return classFileReader.read(clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class ExampleClass {

        public void methodWithLambdaDeclarationAndCall() {
            final Supplier<String> supplier = () -> "Hello World!";
        }

    }
}
