package io.recode.decompile.impl;

import io.recode.Caller;
import io.recode.classfile.ByteCode;
import io.recode.classfile.LineNumberTable;
import io.recode.classfile.LineNumberTableEntry;
import io.recode.classfile.ReferenceKind;
import io.recode.decompile.*;
import io.recode.model.*;
import io.recode.model.impl.ArrayLoadImpl;
import io.recode.model.impl.ConstantImpl;
import io.recode.model.impl.VariableAssignmentImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.recode.Caller.adjacent;
import static io.recode.Caller.me;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class CodeLocationDecompilerImplTest {

    private final CodeLocationDecompiler codeLocationDecompiler = new CodeLocationDecompilerImpl();

    @Test
    public void decompileCallerShouldNotAcceptInvalidArguments() {
        assertThrown(() -> codeLocationDecompiler.decompileCodeLocation(null, mock(DecompilationProgressCallback.class)), AssertionError.class);
        assertThrown(() -> codeLocationDecompiler.decompileCodeLocation(me(), null), AssertionError.class);
    }

    @Test
    public void decompileCallerCanDecompileSimpleStatement() throws IOException {
        int n = 100;

        final Element[] elements = decompileCaller(adjacent(-2));

        assertArrayEquals(new Element[]{
                new VariableAssignmentImpl(new ConstantImpl(100, int.class), 1, "n", int.class)
        }, elements);
    }

    @Test
    public void decompileCallerCanDecompileSimpleLambda() throws IOException {
        Supplier<String> s = () -> "foo";

        final Element[] elements = decompileCaller(adjacent(-2));
        final Lambda lambda = assignedLambda("s", elements);

        assertEquals(MethodSignature.parse("()Ljava/lang/String;"), lambda.getBackingMethodSignature());
        assertTrue(lambda.getEnclosedVariables().isEmpty());
        assertEquals(ReferenceKind.INVOKE_STATIC, lambda.getReferenceKind());
    }

    @Test
    public void decompileCallerCanDecompileLambdaWithEnclosedVariable() throws IOException {
        final String str = new String("str");
        final Supplier<String> s = () -> str;

        final Element[] elements = decompileCaller(adjacent(-2));
        final Lambda lambda = assignedLambda("s", elements);

        final LocalVariableReference localVariableReference = lambda.getEnclosedVariables().get(0);

        assertEquals(MethodSignature.parse("(Ljava/lang/String;)Ljava/lang/String;"), lambda.getBackingMethodSignature());
        assertEquals(1, lambda.getEnclosedVariables().size());
        assertEquals("str", localVariableReference.getName());
        assertEquals(String.class, localVariableReference.getType());
        assertEquals(ReferenceKind.INVOKE_STATIC, lambda.getReferenceKind());
    }

    @Test
    public void decompileCallerCanDecompileLambdaWithParametersAndEnclosedVariable() throws IOException {
        final List myList = Arrays.asList("foo");
        final Consumer<String> a = s -> myList.toString();

        final Element[] elements = decompileCaller(adjacent(-2));
        final Lambda lambda = assignedLambda("a", elements);

        assertEquals(MethodSignature.parse("(Ljava/util/List;Ljava/lang/String;)V"), lambda.getBackingMethodSignature());
        assertEquals(1, lambda.getEnclosedVariables().size());
        assertEquals("myList", lambda.getEnclosedVariables().get(0).getName());
    }

    @Test
    public void nestedLambdaWithEnclosedVariablesCanBeDecompiled() throws IOException {
        int expectedLength = 3;

        given("foo").then(foo -> {
            given("bar").then(bar -> {
                assertEquals(expectedLength, bar.length());
            });
        });

        final Caller caller = Caller.adjacent(-6);
        final Element[] elements = decompileCaller(caller);

        assertEquals("then", elements[0].as(MethodCall.class).getMethodName());
        assertEquals("given", elements[0].as(MethodCall.class).getTargetInstance().as(MethodCall.class).getMethodName());
    }

    @Test
    public void multiLineExpressionsCanBeHandled() throws IOException {
        String str = new String("foo")
                .toString();

        final Element[] elements = decompileCaller(Caller.adjacent(-3));

        assertArrayEquals(new Element[]{
                AST.set(1, "str", String.class, AST.call(AST.newInstance(String.class, AST.constant("foo")), "toString", String.class))
        }, elements);
    }

    @Test
    public void intArrayElementReferenceCanBeDecompiled() throws Exception {
        final int[] array = {1, 2, 3, 4};
        final int n = array[0];

        final Element[] elements = decompileCaller(Caller.adjacent(-2));

        assertArrayEquals(new Element[]{
                new VariableAssignmentImpl(
                        new ArrayLoadImpl(AST.local("array", int[].class, 1), AST.constant(0), int.class),
                        2, "n", int.class
                )
        }, elements);
    }

    @Test
    @Ignore
    // TODO: If (1) iinc and (2) stacked expression is non-int variable reference (3) we're trying to escape
    public void expressionInLoopCanBeDecompiled() throws IOException {
        for (int i = 0; i < 1; i++) {
            assertEquals("foo", "foo");
        }

        final Element[] elements = decompileCaller(Caller.adjacent(-3));

        expect(elements.length).toBe(1);

        final MethodCall it = elements[0].as(MethodCall.class);

        assertEquals("assertEquals", it.getMethodName());
        assertArrayEquals(new Object[]{AST.constant("foo"), AST.constant("foo")}, it.getParameters().toArray());
    }

    private<T> GivenContinuation<T> given(T instance) {
        return consumer-> consumer.accept(instance);
    }

    private interface GivenContinuation<T> {

        void then(Consumer<T> consumer);
    }

    public static class Loop {

        public Loop loop(String argument) {
            return this;
        }

    }

    private<T> ExpectContinuation<T> expect(T instance) {
        return actual -> {
            if (!Objects.equals(instance, actual)) {
                throw new AssertionError("Failure: " + instance + " != " + actual);
            }
        };
    }

    private interface ExpectContinuation<T> {
        void toBe(T value);
    }

    @Test
    @Ignore("This must be fixed")
    public void multiLineStatementInForEachCanBeDecompiled() throws IOException {
        final Integer[] integers = {1, 2, 3, 4};

        for (Integer n : integers) {
            expect(n)
                    .toBe(n);
        }

        final Element[] elements = decompileCaller(Caller.adjacent(-5));

        expect(elements.length).toBe(1);

        System.out.println(Arrays.asList(elements));
    }

    public void nestedLambdaWithEnclosedVariablesCanBeDecompiled(String str) {

    }

    private Lambda assignedLambda(String variableName, Element[] elements) {
        assertEquals(1, elements.length);
        assertEquals(ElementType.VARIABLE_ASSIGNMENT, elements[0].getElementType());

        final VariableAssignment variableAssignment = (VariableAssignment) elements[0];

        assertEquals(variableName, variableAssignment.getVariableName());
        assertEquals(ElementType.LAMBDA, variableAssignment.getValue().getElementType());

        return (Lambda) variableAssignment.getValue();
    }

    private Element[] decompileCaller(Caller caller) throws IOException {
        return Arrays.stream(codeLocationDecompiler.decompileCodeLocation(caller))
                .map(CodePointer::getElement)
                .toArray(Element[]::new);
    }

}
