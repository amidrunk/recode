package io.recode.util;

import io.recode.classfile.*;
import io.recode.classfile.impl.*;
import io.recode.model.MethodSignature;
import io.recode.model.Signature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodsTest {

    private final ClassFile classFile = Mockito.mock(ClassFile.class);

    private final Method methodWithoutLineNumbers = mock(Method.class, "methodWithoutLineNumbers");
    private final Method methodWithLineNumbers1 = mock(Method.class, "methodWithLineNumbers1");
    private final Method methodWithLineNumbers2 = mock(Method.class, "methodWithLineNumbers2");

    @Before
    public void setup() {
        when(methodWithoutLineNumbers.getLineNumberTable()).thenReturn(Optional.<LineNumberTable>empty());

        when(methodWithLineNumbers1.getLineNumberTable()).thenReturn(Optional.of(new LineNumberTableImpl(new LineNumberTableEntry[0], new Range(10, 13))));
        when(methodWithLineNumbers2.getLineNumberTable()).thenReturn(Optional.of(new LineNumberTableImpl(new LineNumberTableEntry[0], new Range(20, 22))));

        when(classFile.getMethods()).thenReturn(Arrays.asList(
                methodWithoutLineNumbers,
                methodWithLineNumbers1,
                methodWithLineNumbers2));
    }

    @Test
    public void findMethodForNameAndSignatureShouldRejectInvalidArguments() {
        assertThrown(() -> Methods.findMethodForNameAndSignature(null, "foo", mock(Signature.class)), AssertionError.class);
        assertThrown(() -> Methods.findMethodForNameAndSignature(getClass(), null, mock(Signature.class)), AssertionError.class);
        assertThrown(() -> Methods.findMethodForNameAndSignature(getClass(), "", mock(Signature.class)), AssertionError.class);
        assertThrown(() -> Methods.findMethodForNameAndSignature(getClass(), "foo", null), AssertionError.class);
    }

    @Test
    public void findMethodForNameAndSignatureShouldReturnEmptyOptionalIfMethodIsNotFound() {
        assertFalse(Methods.findMethodForNameAndSignature(getClass(), "setup", MethodSignature.parse("()I")).isPresent());
    }

    @Test
    public void findMethodForNameAndSignatureShouldReturnMatchingMethod() throws Exception {
        final java.lang.reflect.Method expectedMethod = getClass().getDeclaredMethod("setup");

        assertEquals(expectedMethod, Methods.findMethodForNameAndSignature(getClass(), "setup", MethodSignature.parse("()V")).get());
    }

    @Test
    public void findMethodForLineNumberShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Methods.findMethodForLineNumber(null, 123), AssertionError.class);
        assertThrown(() -> Methods.findMethodForLineNumber(classFile, -1), AssertionError.class);
    }

    @Test
    public void findMethodsForLineNumberShouldReturnNonPresentOptionalIfMethodIsNotFound() {
        when(classFile.getMethods()).thenReturn(Arrays.asList(
                methodWithoutLineNumbers,
                methodWithLineNumbers1,
                methodWithLineNumbers2
        ));

        assertFalse(Methods.findMethodForLineNumber(classFile, 123).isPresent());
    }

    @Test
    public void findMethodsForLineNumberShouldReturnMatchingMethod() {
        assertEquals(Optional.of(methodWithLineNumbers1), Methods.findMethodForLineNumber(classFile, 10));
        assertEquals(Optional.of(methodWithLineNumbers1), Methods.findMethodForLineNumber(classFile, 11));
        assertEquals(Optional.of(methodWithLineNumbers1), Methods.findMethodForLineNumber(classFile, 12));
        assertEquals(Optional.of(methodWithLineNumbers1), Methods.findMethodForLineNumber(classFile, 13));

        assertEquals(Optional.of(methodWithLineNumbers2), Methods.findMethodForLineNumber(classFile, 20));
        assertEquals(Optional.of(methodWithLineNumbers2), Methods.findMethodForLineNumber(classFile, 21));
        assertEquals(Optional.of(methodWithLineNumbers2), Methods.findMethodForLineNumber(classFile, 22));
    }

    @Test
    public void getExceptionTableEntryForCatchLocationShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Methods.getExceptionTableEntryForCatchLocation(null, 123), AssertionError.class);
        assertThrown(() -> Methods.getExceptionTableEntryForCatchLocation(methodWithLineNumbers1, -1), AssertionError.class);
    }

    @Test
    public void getExceptionTableEntryForCatchLocationShouldReturnNonPresentOptionalIfNoExceptionTableExists() {
        when(methodWithLineNumbers1.getCode()).thenReturn(mock(CodeAttribute.class));

        assertFalse(Methods.getExceptionTableEntryForCatchLocation(methodWithLineNumbers1, 1234).isPresent());
    }

    @Test
    public void getExceptionTableEntryForCatchLocationShouldReturnNonPresentOptionalForNonCatchLocation() {
        final CodeAttribute codeAttribute = mock(CodeAttribute.class);

        when(codeAttribute.getExceptionTable()).thenReturn(Arrays.asList(new ExceptionTableEntryImpl(0, 10, 15, Exception.class)));
        when(methodWithLineNumbers1.getCode()).thenReturn(codeAttribute);

        assertFalse(Methods.getExceptionTableEntryForCatchLocation(methodWithLineNumbers1, 9).isPresent());
    }

    @Test
    public void getExceptionTableEntryForCatchLocationShouldReturnMatchingEntry() {
        final CodeAttribute codeAttribute = mock(CodeAttribute.class);
        final ExceptionTableEntry expectedEntry = new ExceptionTableEntryImpl(0, 10, 15, Exception.class);

        when(codeAttribute.getExceptionTable()).thenReturn(Arrays.asList(expectedEntry));
        when(methodWithLineNumbers1.getCode()).thenReturn(codeAttribute);

        assertEquals(Optional.of(expectedEntry), Methods.getExceptionTableEntryForCatchLocation(methodWithLineNumbers1, 10));
    }

    @Test
    public void findLocalVariableForIndexAndPCShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Methods.findLocalVariableForIndexAndPC(null, 0, 0), AssertionError.class);
        assertThrown(() -> Methods.findLocalVariableForIndexAndPC(mock(Method.class), -1, 0), AssertionError.class);
        assertThrown(() -> Methods.findLocalVariableForIndexAndPC(mock(Method.class), 0, -1), AssertionError.class);
    }

    @Test
    public void findLocalVariableForIndexAndPCShouldReturnNonPresentOptionalIfNoVariableTableExists() {
        when(methodWithLineNumbers1.getLocalVariableTable()).thenReturn(Optional.empty());

        final Optional<LocalVariable> optionalLocal = Methods.findLocalVariableForIndexAndPC(methodWithLineNumbers1, 0, 0);

        assertFalse(optionalLocal.isPresent());
    }

    @Test
    public void findLocalVariableShouldReturnMatchingVariable() {
        final LocalVariableImpl variable1 = new LocalVariableImpl(0, 10, "foo", String.class, 0);
        final LocalVariableImpl variable2 = new LocalVariableImpl(11, 20, "bar", int.class, 0);

        when(methodWithLineNumbers1.getLocalVariableTable()).thenReturn(Optional.of(new LocalVariableTableImpl(new LocalVariable[]{
                variable1,
                variable2
        })));

        assertEquals(Optional.of(variable1), Methods.findLocalVariableForIndexAndPC(methodWithLineNumbers1, 0, 0));
        assertEquals(Optional.of(variable2), Methods.findLocalVariableForIndexAndPC(methodWithLineNumbers1, 0, 11));
    }

    @Test
    public void findMethodForNameAndLineNumberShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Methods.findMethodForNameAndLineNumber(null, "foo", 1234), AssertionError.class);
        assertThrown(() -> Methods.findMethodForNameAndLineNumber(classFile, null, 1234), AssertionError.class);
        assertThrown(() -> Methods.findMethodForNameAndLineNumber(classFile, "", 1234), AssertionError.class);
        assertThrown(() -> Methods.findMethodForNameAndLineNumber(classFile, "", 1234), AssertionError.class);
        assertThrown(() -> Methods.findMethodForNameAndLineNumber(classFile, "foo", -1), AssertionError.class);
    }

    @Test
    public void findMethodForNameAndLineNumberShouldReturnNonPresentOptionalIfNoMatchingMethodNameExists() {
        final List<Method> methods = Arrays.asList(method("foo", 0, 10));

        when(classFile.getMethods()).thenReturn(methods);

        assertFalse(Methods.findMethodForNameAndLineNumber(classFile, "bar", 2).isPresent());
    }

    @Test
    public void containsLineNumberShouldNotAcceptInvalidArguments() {
        assertThrown(() -> Methods.containsLineNumber(null, 1234), AssertionError.class);
        assertThrown(() -> Methods.containsLineNumber(mock(Method.class), -1), AssertionError.class);
    }

    @Test
    public void containsLineNumberShouldFailIfMethodDoesNotContainLineNumberTable() {
        final Method method = mock(Method.class);

        when(method.getLineNumberTable()).thenReturn(Optional.empty());

        assertThrown(() -> Methods.containsLineNumber(method, 100), IllegalArgumentException.class);
    }

    @Test
    public void containsLineNumberShouldReturnTrueIfLineNumberTableContainsLineNumber() {
        final Method method = mock(Method.class);
        final LineNumberTable table = mock(LineNumberTable.class);

        when(table.getEntries()).thenReturn(Arrays.asList(
                new LineNumberTableEntryImpl(0, 10),
                new LineNumberTableEntryImpl(5, 11)));

        when(method.getLineNumberTable()).thenReturn(Optional.of(table));

        assertFalse(Methods.containsLineNumber(method, 9));
        assertTrue(Methods.containsLineNumber(method, 10));
        assertTrue(Methods.containsLineNumber(method, 11));
        assertFalse(Methods.containsLineNumber(method, 12));
    }

    private Method method(String name, int firstLineNumber, int lastLineNumber) {
        final Method method = mock(Method.class);
        final LineNumberTable lineNumberTable = mock(LineNumberTable.class);

        when(lineNumberTable.getEntries()).thenReturn(Arrays.asList(
                new LineNumberTableEntryImpl(0, firstLineNumber),
                new LineNumberTableEntryImpl(0, lastLineNumber)
            ));
        when(method.getName()).thenReturn(name);
        when(method.getLineNumberTable()).thenReturn(Optional.of(lineNumberTable));

        return method;
    }
}
