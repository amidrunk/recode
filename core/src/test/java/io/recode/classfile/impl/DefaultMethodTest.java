package io.recode.classfile.impl;

import io.recode.classfile.*;
import io.recode.model.MethodSignature;
import io.recode.model.Signature;
import io.recode.util.Range;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static io.recode.test.Assertions.assertThrown;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class DefaultMethodTest {

    private final Supplier<ClassFile> classFileSupplier = mock(Supplier.class);

    private final LocalVariableTable localVariableTable = new LocalVariableTableImpl(new LocalVariable[]{
            new LocalVariableImpl(0, 10, "foo", String.class, 0)
    });

    private final Signature exampleSignature = mock(Signature.class);

    private final DefaultMethod method = new DefaultMethod(classFileSupplier, 1234, "foo", exampleSignature, new Attribute[]{localVariableTable});

    @Test
    public void constructorShouldNotAcceptNullClassFileSupplier() {
        assertThrown(() -> new DefaultMethod(null, 0, "foo", exampleSignature, new Attribute[0]), AssertionError.class);
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullName() {
        new DefaultMethod(classFileSupplier, 0, null, exampleSignature, new Attribute[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullSignature() {
        new DefaultMethod(classFileSupplier, 0, "foo", null, new Attribute[]{});
    }

    @Test(expected = AssertionError.class)
    public void constructorShouldNotAcceptNullAttributes() {
        new DefaultMethod(classFileSupplier, 0, "foo", exampleSignature, null);
    }

    @Test
    public void constructorShouldRetainParameters() {
        final ClassFile classFile = mock(ClassFile.class);
        when(classFileSupplier.get()).thenReturn(classFile);

        assertEquals(classFile, method.getClassFile());
        assertEquals(1234, method.getAccessFlags());
        assertEquals("foo", method.getName());
        assertEquals(exampleSignature, method.getSignature());
        assertArrayEquals(new Attribute[]{localVariableTable}, method.getAttributes().toArray());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(method, method);
        assertEquals(method.hashCode(), method.hashCode());
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertNotEquals(method, null);
        assertNotEquals(method, "foo");
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final DefaultMethod other = new DefaultMethod(classFileSupplier, 1234, "foo", exampleSignature, new Attribute[]{localVariableTable});

        assertEquals(1234, other.getAccessFlags());
        assertEquals("foo", other.getName());
        assertEquals(exampleSignature, other.getSignature());
        assertArrayEquals(new Attribute[]{localVariableTable}, other.getAttributes().toArray());
    }

    @Test
    public void getCodeShouldFailIfCodeAttributeIsNotPresent() {
        assertThrown(() -> new DefaultMethod(classFileSupplier, 0, "foo", exampleSignature, new Attribute[0]).getCode(), IllegalStateException.class);
    }

    @Test
    public void getCodeShouldReturnCodeAttributeIfExists() {
        final Attribute otherAttribute = mock(Attribute.class);
        final CodeAttribute codeAttribute = mock(CodeAttribute.class);

        when(otherAttribute.getName()).thenReturn("OtherAttribute");
        when(codeAttribute.getName()).thenReturn(CodeAttribute.ATTRIBUTE_NAME);

        final DefaultMethod method = new DefaultMethod(classFileSupplier, 0, "foo", exampleSignature, new Attribute[]{otherAttribute, codeAttribute});

        assertEquals(codeAttribute, method.getCode());
    }

    @Test
    public void toStringValueShouldContainPropertyValues() {
        assertThat(method.toString(), containsString("1234"));
        assertThat(method.toString(), containsString("foo"));
        assertThat(method.toString(), containsString(exampleSignature.toString()));
        assertThat(method.toString(), containsString(localVariableTable.toString()));
    }

    @Test
    public void getCodeForLineNumberShouldFailIfLineNumberTableIsNotPresent() {
        assertThrown(() -> method.getCodeForLineNumber(1), IllegalStateException.class);
    }

    @Test
    public void getLocalVariableForIndexShouldFailForNegativeIndex() {
        assertThrown(() -> method.getLocalVariableForIndex(-1), AssertionError.class);
    }

    @Test
    public void getLocalVariableShouldFailIfIndexIsOutOfBounds() {
        assertThrown(() -> method.getLocalVariableForIndex(1), IllegalStateException.class);
    }

    @Test
    public void getLocalVariableShouldReturnMatchingVariable() {
        final DefaultMethod method = methodWithCodeAttributes(new LocalVariableTableImpl(new LocalVariable[]{
                new LocalVariableImpl(0, 0, "foo", String.class, 0)
        }));

        final LocalVariable localVariable = method.getLocalVariableForIndex(0);

        assertEquals("foo", localVariable.getName());
    }

    @Test
    public void getLocalVariableTableShouldReturnNonPresentOptionalIfLocalVariableTableDoesNotExist() {
        final Optional<LocalVariableTable> localVariableTable = methodWithCodeAttributes().getLocalVariableTable();

        assertFalse(localVariableTable.isPresent());
    }

    @Test
    public void getLocalVariableTableShouldReturnAttributeIfExists() {
        final LocalVariableTableImpl localVariableTable = new LocalVariableTableImpl(new LocalVariable[0]);
        final DefaultMethod method = methodWithCodeAttributes(localVariableTable);

        assertTrue(method.getLocalVariableTable().isPresent());
        assertEquals(localVariableTable, method.getLocalVariableTable().get());
    }

    @Test
    public void withLocalVariableTableShouldNotAcceptNullTable() {
        assertThrown(() -> methodWithCodeAttributes().withLocalVariableTable(null), AssertionError.class);
    }

    @Test
    public void withLocalVariableTableShouldAddVariableTableIfNotExists() {
        final LocalVariableTableImpl newTable = new LocalVariableTableImpl(new LocalVariable[0]);
        final Method newMethod = methodWithCodeAttributes().withLocalVariableTable(newTable);

        assertTrue(newMethod.getLocalVariableTable().isPresent());
        assertSame(newTable, newMethod.getLocalVariableTable().get());
    }

    @Test
    public void withLocalVariableTableShouldReplaceExistingVariableTable() {
        final LocalVariableTableImpl newTable = new LocalVariableTableImpl(new LocalVariable[0]);
        final Method newMethod = methodWithCodeAttributes(new LocalVariableTableImpl(new LocalVariable[0])).withLocalVariableTable(newTable);

        assertTrue(newMethod.getLocalVariableTable().isPresent());
        assertSame(newTable, newMethod.getLocalVariableTable().get());
    }

    @Test
    public void hasCodeForLineNumberShouldReturnTrueIfCorrespondingLineNumberTableEntryExists() {
        final DefaultMethod method = methodWithCodeAttributes(new LineNumberTableImpl(new LineNumberTableEntry[]{
                new LineNumberTableEntryImpl(0, 1234)
        }, new Range(1234, 1234)));

        assertTrue(method.hasCodeForLineNumber(1234));
    }

    @Test
    public void hasCodeForLineNumberShouldReturnFalseIfNoCorrespondingLineNumberEntryExists() {
        final DefaultMethod method = methodWithCodeAttributes(new LineNumberTableImpl(new LineNumberTableEntry[]{
                new LineNumberTableEntryImpl(0, 1234)
        }, new Range(1234, 1234)));

        assertFalse(method.hasCodeForLineNumber(1235));
    }

    @Test
    public void hasCodeForLineNumberShouldFailIfLineNumberTableAttributeDoesNotExists() {
        assertThrown(() -> methodWithCodeAttributes().hasCodeForLineNumber(1234), IllegalStateException.class);
    }

    @Test
    public void getLineNumberTableShouldFailIfNoLineNumberTableExists() {
        assertThrown(() -> methodWithCodeAttributes().getRequiredLineNumberTable(), IllegalStateException.class);
    }

    @Test
    public void isLambdaBackingMethodShouldReturnTrueIfNameIndicatesLambdaAndModifiersMatchers() {
        final DefaultMethod method = new DefaultMethod(() -> null, 4106, "lambda$myLambdaMethod", MethodSignature.parse("()I"), new Attribute[0]);

        assertTrue(method.isLambdaBackingMethod());
    }

    @Test
    public void isLambdaBackingMethodShouldReturnFalseIfNameIndicatesLambdaButModifiersAreIncorrect() {
        final DefaultMethod method = new DefaultMethod(() -> null, 196, "lambda$myLambdaMethod", MethodSignature.parse("()I"), new Attribute[0]);

        assertFalse(method.isLambdaBackingMethod());
    }

    @Test
    public void isLambdaBackingMethodShouldReturnFalseIfNameDoesNotIndicateLambda() {
        final DefaultMethod method = new DefaultMethod(() -> null, 4106, "myLambdaMethod", MethodSignature.parse("()I"), new Attribute[0]);

        assertFalse(method.isLambdaBackingMethod());
    }

    private DefaultMethod methodWithCodeAttributes(Attribute... attributes) {
        return new DefaultMethod(classFileSupplier, 0, "foo", exampleSignature, new Attribute[]{
                new CodeAttributeImpl(
                        0, 0,
                        ByteBuffer.wrap(new byte[0]),
                        Collections.emptyList(),
                        Arrays.asList(
                                attributes
                        ))
        });
    }
}
