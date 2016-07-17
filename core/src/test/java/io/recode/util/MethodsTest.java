package io.recode.util;

import io.recode.Caller;
import io.recode.classfile.*;
import io.recode.classfile.impl.*;
import io.recode.decompile.CodeLocationDecompiler;
import io.recode.decompile.CodePointer;
import io.recode.decompile.impl.CodeLocationDecompilerImpl;
import io.recode.decompile.impl.CodeStreamTestUtils;
import io.recode.decompile.impl.DecompilerImpl;
import io.recode.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.PrintStream;
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

    @Test
    public void getCodeForLineNumberShouldFailIfLineNumberTableIsNotPresent() {
        final Method method = mock(Method.class);
        final ClassFile classFile = mock(ClassFile.class);

        when(method.getClassFile()).thenReturn(classFile);
        when(classFile.getName()).thenReturn("AClass");

        when(method.getLineNumberTable()).thenReturn(Optional.empty());

        assertThrown(() -> Methods.getCodeForLineNumber(method, 1), IllegalArgumentException.class);
    }

    @Test
    public void isVarArgsMethodCallShouldReturnTrueIfTargetMethodIsVarArgs() {
        final MethodCall methodCall = AST.call(String.class, "format", MethodSignature.parse("(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"));

        assertTrue(Methods.isDefinitelyVarArgsMethodCall(methodCall));
    }

    @Test
    public void isVarArgsMethodCallShouldReturnFalseIfTargetMethodIsNotVarArgs() {
        final MethodCall methodCall = AST.call(String.class, "substring", MethodSignature.parse("(I)Ljava/lang/String;"));

        assertFalse(Methods.isDefinitelyVarArgsMethodCall(methodCall));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void methodCanBeResolvedForInlineLambda() throws Exception {
        final Runnable r = () -> nop();

        final CodePointer<?> codePointer = new CodeLocationDecompilerImpl().decompileCodeLocation(Caller.adjacent(-2))[0];
        final Lambda lambda = codePointer.getElement().as(VariableAssignment.class).getValue().as(Lambda.class);
        final Method backingMethod = Methods.getBackingMethod(codePointer.forElement(lambda)).get();
        final Element[] lambdaBody = new DecompilerImpl().decompile(backingMethod);

        assertArrayEquals(new Element[] {
            AST.call(AST.local("this", MethodsTest.class, 0), "nop", void.class),
            AST.$return()
        }, lambdaBody);
    }

    @Test
    public void backingMethodCanBeResolvedForInstanceMethodReference() throws Exception {
        final Runnable r = this::nop;

        final CodePointer<?> codePointer = new CodeLocationDecompilerImpl().decompileCodeLocation(Caller.adjacent(-2))[0];
        final Lambda lambda = codePointer.getElement().as(VariableAssignment.class).getValue().as(Lambda.class);
        final Method backingMethod = Methods.getBackingMethod(codePointer.forElement(lambda)).get();
        final Element[] lambdaBody = new DecompilerImpl().decompile(backingMethod);

        assertEquals("nop", backingMethod.getName());
        assertArrayEquals(new Element[] {
                AST.$return()
        }, lambdaBody);
    }

    @Test
    public void backingMethodCanBeResolvedForStaticMethodReference() throws Exception {
        final Runnable r = MethodsTest::staticNop;

        final CodePointer<?> codePointer = new CodeLocationDecompilerImpl().decompileCodeLocation(Caller.adjacent(-2))[0];
        final Lambda lambda = codePointer.getElement().as(VariableAssignment.class).getValue().as(Lambda.class);
        final Method backingMethod = Methods.getBackingMethod(codePointer.forElement(lambda)).get();
        final Element[] lambdaBody = new DecompilerImpl().decompile(backingMethod);

        assertEquals("staticNop", backingMethod.getName());
        assertArrayEquals(new Element[] {
                AST.$return()
        }, lambdaBody);
    }

    @Test
    public void backingMethodCanBeResolvedForReferenceToInstanceMethodInOtherClass() throws Exception {
        final Runnable runnable = "foo"::isEmpty;

        final CodePointer<?> codePointer = new CodeLocationDecompilerImpl().decompileCodeLocation(Caller.adjacent(-2))[0];
        final Lambda lambda = codePointer.getElement().as(VariableAssignment.class).getValue().as(Lambda.class);
        final Method backingMethod = Methods.getBackingMethod(new ClassPathClassFileResolver(new ClassFileReaderImpl()), codePointer.forElement(lambda)).get();
        final Element[] lambdaBody = new DecompilerImpl().decompile(backingMethod);

        assertEquals("isEmpty", backingMethod.getName());
        assertArrayEquals(new Element[] {
            AST.$return(AST.eq(AST.field(AST.field(AST.local("this", String.class, 0), char[].class, "value"), int.class, "length"), AST.constant(0)))
        }, lambdaBody);
    }

    @Test
    public void backingMethodCanBeResolvedForReferenceToStaticMethodInOtherClass() throws Exception {
        final Runnable runnable = System.out::println;

        final CodePointer<?> codePointer = new CodeLocationDecompilerImpl().decompileCodeLocation(Caller.adjacent(-2))[0];
        final Lambda lambda = codePointer.getElement().as(VariableAssignment.class).getValue().as(Lambda.class);
        final Method backingMethod = Methods.getBackingMethod(new ClassPathClassFileResolver(new ClassFileReaderImpl()), codePointer.forElement(lambda)).get();
        final Element[] lambdaBody = new DecompilerImpl().decompile(backingMethod);

        assertEquals("println", backingMethod.getName());
        assertArrayEquals(new Element[] {
            AST.call(AST.local("this", PrintStream.class, 0), "newLine", void.class),
            AST.$return()
        }, lambdaBody);
    }

    private void nop() {}

    private static void staticNop() {}

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
