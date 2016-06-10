package io.recode.decompile.impl;

import io.recode.Caller;
import io.recode.classfile.ReferenceKind;
import io.recode.decompile.CodeLocationDecompiler;
import io.recode.decompile.CodePointer;
import io.recode.model.*;
import io.recode.model.impl.LocalVariableReferenceImpl;
import io.recode.util.Arrays2;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class InvokeDynamicExtensionsDecompilationTest {

    private final CodeLocationDecompiler codeLocationDecompiler = new CodeLocationDecompilerImpl();

    private final TestTarget testTarget = new TestTarget();

    @Test
    public void instanceMethodReferenceWithSameSignatureAsInterfaceCanBeDecompiled() throws IOException {
        final Runnable runnable = testTarget::run1;
        final Element element = Arrays2.single(codeLocationDecompiler.decompileCodeLocation(Caller.adjacent(-1)), CodePointer::getElement);

        final VariableAssignment assignment = element.as(VariableAssignment.class);

        assertEquals(Runnable.class, assignment.getVariableType());
        assertEquals("runnable", assignment.getVariableName());

        final Lambda lambda = assignment.getValue().as(Lambda.class);

        assertEquals(Runnable.class, lambda.getFunctionalInterface());
        assertEquals("run1", lambda.getBackingMethodName());
        assertEquals(MethodSignature.parse("()V"), lambda.getBackingMethodSignature());
        assertEquals(TestTarget.class, lambda.getDeclaringClass());
        assertEquals(Optional.of(AST.field(AST.local("this", getClass(), 0), TestTarget.class, "testTarget")), lambda.getSelf());
        assertEquals(MethodSignature.parse("()V"), lambda.getInterfaceMethodSignature());
        assertTrue(lambda.getEnclosedVariables().isEmpty());
        assertEquals(ReferenceKind.INVOKE_VIRTUAL, lambda.getReferenceKind());
    }

    @Test
    public void staticMethodReferenceWithSameSignatureAsInterfaceCanBeDecompiled() throws IOException {
        final Runnable runnable = TestTarget::run2;
        final Element element = Arrays2.single(codeLocationDecompiler.decompileCodeLocation(Caller.adjacent(-1)), CodePointer::getElement);

        final VariableAssignment assignment = element.as(VariableAssignment.class);

        assertEquals(Runnable.class, assignment.getVariableType());
        assertEquals("runnable", assignment.getVariableName());

        final Lambda lambda = assignment.getValue().as(Lambda.class);

        assertEquals(TestTarget.class, lambda.getDeclaringClass());
        assertEquals("run2", lambda.getBackingMethodName());
        assertFalse(lambda.getSelf().isPresent());
        assertEquals(Runnable.class, lambda.getFunctionalInterface());
        assertEquals("run", lambda.getFunctionalMethodName());
        assertEquals(MethodSignature.parse("()V"), lambda.getBackingMethodSignature());
        assertEquals(MethodSignature.parse("()V"), lambda.getInterfaceMethodSignature());
        assertTrue(lambda.getEnclosedVariables().isEmpty());
        assertEquals(ReferenceKind.INVOKE_STATIC, lambda.getReferenceKind());
    }

    @Test
    public void staticMethodReferenceWithDifferentSignatureFromInterfaceCanBeDecompiled() throws IOException {
        final Function<Element, ElementType> function = Element::getElementType;

        final Element element = Arrays2.single(codeLocationDecompiler.decompileCodeLocation(Caller.adjacent(-2)), CodePointer::getElement);

        final VariableAssignment assignment = element.as(VariableAssignment.class);

        assertEquals(Function.class, assignment.getVariableType());
        assertEquals("function", assignment.getVariableName());

        final Lambda lambda = assignment.getValue().as(Lambda.class);

        assertEquals("getElementType", lambda.getBackingMethodName());
        assertEquals(MethodSignature.parse("()Lio/recode/model/ElementType;"), lambda.getBackingMethodSignature());
        assertEquals(Element.class, lambda.getDeclaringClass());
        assertFalse(lambda.getSelf().isPresent());
        assertEquals(Function.class, lambda.getFunctionalInterface());
        assertEquals("apply", lambda.getFunctionalMethodName());
        assertEquals(MethodSignature.parse("(Ljava/lang/Object;)Ljava/lang/Object;"), lambda.getInterfaceMethodSignature());
        assertEquals(ReferenceKind.INVOKE_INTERFACE, lambda.getReferenceKind());
    }

    @Test
    public void lambdaWithVariableAndFieldReference() throws IOException {
        final String str1 = new String("_suffix");
        final Supplier<String> supplier = () -> testTarget.getClass().getName() + str1;
        final Element element = Arrays2.single(codeLocationDecompiler.decompileCodeLocation(Caller.adjacent(-1)), CodePointer::getElement);

        final VariableAssignment assignment = element.as(VariableAssignment.class);

        assertEquals(Supplier.class, assignment.getVariableType());
        assertEquals("supplier", assignment.getVariableName());

        final Lambda lambda = assignment.getValue().as(Lambda.class);

        assertEquals(MethodSignature.parse("(Ljava/lang/String;)Ljava/lang/String;"), lambda.getBackingMethodSignature());
        assertEquals(ReferenceKind.INVOKE_SPECIAL, lambda.getReferenceKind());
        assertEquals(getClass(), lambda.getDeclaringClass());
        assertEquals(Optional.of(AST.local("this", getClass(), 0)), lambda.getSelf());
        assertArrayEquals(new Object[] { new LocalVariableReferenceImpl("str1", String.class, 1) }, lambda.getEnclosedVariables().toArray());
    }

    public static final class TestTarget {

        public void run1() {
        }

        public static void run2() {
        }
    }
}
