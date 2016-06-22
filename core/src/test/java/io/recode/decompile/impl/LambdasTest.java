package io.recode.decompile.impl;

import io.recode.Caller;
import io.recode.ClassModelTestUtils;
import io.recode.classfile.*;
import io.recode.classfile.Method;
import io.recode.decompile.CodePointer;
import io.recode.decompile.Decompiler;
import io.recode.decompile.Lambdas;
import io.recode.model.*;
import io.recode.model.impl.LambdaImpl;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.recode.Caller.adjacent;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LambdasTest {

    private final Decompiler decompiler = new DecompilerImpl();

    @Test
    public void getLambdaDeclarationForMethodShouldNotAcceptNullBackingMethod() {
        assertThrown(() -> Lambdas.getLambdaDeclarationForMethod(null, mock(Method.class)), AssertionError.class);
        assertThrown(() -> Lambdas.getLambdaDeclarationForMethod(decompiler, null), AssertionError.class);
    }

    @Test
    public void getLambdaDeclarationForMethodShouldNotAcceptNonLambdaBackingMethod() {
        final Method method = mock(Method.class);
        final ClassFile classFile = mock(ClassFile.class);

        when(method.getClassFile()).thenReturn(classFile);
        when(method.isLambdaBackingMethod()).thenReturn(false);

        assertThrown(() -> Lambdas.getLambdaDeclarationForMethod(decompiler, method), IllegalArgumentException.class);
    }

    @Test
    public void getLambdaDeclarationForMethodShouldResolveDeclaringLambdaMethod() throws IOException {
        final Runnable runnable = () -> {};

        final CodePointer codePointer = ClassModelTestUtils.code(adjacent(-2))[0];
        final Lambda expectedLambda = codePointer.getElement().as(VariableAssignment.class).getValue().as(Lambda.class);
        final Method backingMethod = getBackingMethod(codePointer, expectedLambda);

        final CodePointer<Lambda> actualLambdaPointer = Lambdas.getLambdaDeclarationForMethod(decompiler, backingMethod)
                .orElseThrow(() -> new AssertionError("Lambda not found"));

        assertEquals(expectedLambda, actualLambdaPointer.getElement());
        assertEquals("getLambdaDeclarationForMethodShouldResolveDeclaringLambdaMethod", actualLambdaPointer.getMethod().getName());
    }

    @Test
    public void getLambdaDeclarationForMethodShouldResolveLambdaInLambda() throws IOException {
        final Supplier<Runnable> supplier = () -> {
            return () -> {
                System.out.println("Example statement");
            };
        };

        final Caller caller = Caller.adjacent(-4);
        final Method backingMethod = ClassModelTestUtils.classFileOf(getClass()).getMethods().stream()
                .filter(m -> m.hasCodeForLineNumber(caller.getLineNumber()))
                .findFirst().get();

        final CodePointer<Lambda> declarationCodePointer = Lambdas.getLambdaDeclarationForMethod(decompiler, backingMethod).get();
        final Lambda lambda = declarationCodePointer.getElement().as(Lambda.class);

        assertEquals(Runnable.class, lambda.getFunctionalInterface());
        assertEquals("run", lambda.getFunctionalMethodName());
        assertEquals(getClass(), lambda.getDeclaringClass());
        assertFalse(lambda.getSelf().isPresent());
        assertTrue(lambda.getEnclosedVariables().isEmpty());
    }

    @Test
    public void isDeclarationOfShouldNotAcceptNullArg() {
        assertThrown(() -> Lambdas.isDeclarationOf(null), AssertionError.class);
    }

    @Test
    public void isDeclarationOfPredicateShouldMatchCorrespondingLambda() {
        final Method method = mock(Method.class);
        final String lambdaMethodName = "lambda$myMethod";
        final Lambda lambda = new LambdaImpl(Optional.<Expression>empty(), ReferenceKind.INVOKE_STATIC, Runnable.class, "run", MethodSignature.parse("()V"), getClass(), lambdaMethodName, MethodSignature.parse("()V"), Collections.emptyList());

        when(method.getName()).thenReturn(lambdaMethodName);

        final Predicate<Element> predicate = Lambdas.isDeclarationOf(method);

        assertTrue(predicate.test(lambda));
    }

    @Test
    public void isDeclarationOfPredicateShouldNotMatchDifferentLambda() {
        final Method method = mock(Method.class);
        final String lambdaMethodName = "lambda$myMethod";
        final Lambda lambda = new LambdaImpl(Optional.<Expression>empty(), ReferenceKind.INVOKE_STATIC, Runnable.class, "run", MethodSignature.parse("()V"), getClass(), "lambda$otherLambda", MethodSignature.parse("()V"), Collections.emptyList());

        when(method.getName()).thenReturn(lambdaMethodName);

        final Predicate<Element> predicate = Lambdas.isDeclarationOf(method);

        assertFalse(predicate.test(lambda));
    }

    @Test
    public void isDeclarationOfPredicateShouldNotMatchNonLambdaElementType() {
        final Predicate<Element> it = Lambdas.isDeclarationOf(mock(Method.class));

        assertFalse(it.test(AST.constant(1)));
    }

    @Test
    public void withEnclosedVariablesShouldNotAcceptInvalidParameters() {
        assertThrown(() -> Lambdas.withEnclosedVariables(null, mock(Method.class)), AssertionError.class);
        assertThrown(() -> Lambdas.withEnclosedVariables(mock(Decompiler.class), null), AssertionError.class);
    }

    @Test
    public void withEnclosedVariablesShouldReturnSameLambdaIfNoVariablesAreEnclosed() throws IOException {
        final Runnable runnable = () -> {};

        final CodePointer codePointer = ClassModelTestUtils.code(adjacent(-2))[0];
        final Method backingMethod = getBackingMethodInClosestLambda(codePointer);
        final Method complementedMethod = Lambdas.withEnclosedVariables(decompiler, backingMethod);

        assertSame(backingMethod, complementedMethod);
    }

    @Test
    public void withEnclosedVariablesAndNoParametersShouldReturnMethodWithClosuresInVariableTable() throws IOException {
        final String variable = new String("foo");
        final Runnable runnable = () -> { System.out.println(variable); };

        final CodePointer codePointer = ClassModelTestUtils.code(Caller.adjacent(-2))[0];
        final Method complementedMethod = complementWithEnclosedVariables(codePointer);
        final Optional<LocalVariableTable> localVariableTable = complementedMethod.getLocalVariableTable();

        final List<LocalVariable> vars = localVariableTable.get().getLocalVariables();

        assertEquals(1, vars.size());
        assertEquals("variable", vars.get(0).getName());
        assertEquals(0, vars.get(0).getIndex());
        assertEquals(String.class, vars.get(0).getType());
    }

    @Test
    public void withEnclosedVariablesShouldReturnMethodWithClosuresAndParameters() throws IOException {
        final String other = new String("foo");
        final Function<String, Integer> f = string -> string.length() + other.length();

        final CodePointer codePointer = ClassModelTestUtils.code(Caller.adjacent(-2))[0];
        final Method complementedMethod = complementWithEnclosedVariables(codePointer);
        final Optional<LocalVariableTable> localVariableTable = complementedMethod.getLocalVariableTable();

        final List<LocalVariable> vars = localVariableTable.get().getLocalVariables();

        assertEquals(2, vars.size());
        assertEquals("other", vars.get(0).getName());
        assertEquals(0, vars.get(0).getIndex());
        assertEquals(String.class, vars.get(0).getType());
        assertEquals("string", vars.get(1).getName());
        assertEquals(1, vars.get(1).getIndex());
        assertEquals(String.class, vars.get(1).getType());
    }

    @Test
    public void backingMethodCanBeRetrievedForLambda() throws IOException {
        final Runnable runnable = () -> System.out.println("Hello World!");

        final CodePointer codePointer = ClassModelTestUtils.code(Caller.adjacent(-2))[0];

        final VariableAssignment assignment = codePointer.getElement().as(VariableAssignment.class);
        final Lambda lambda = assignment.getValue().as(Lambda.class);
        final Method backingMethod = Lambdas.getBackingMethod(codePointer.forElement(lambda));

        final Decompiler decompiler = new DecompilerImpl();
        final Element[] methodBody = decompiler.parse(backingMethod, new InputStreamCodeStream(backingMethod.getCode().getCode()));

        assertArrayEquals(new Element[]{
                AST.call(AST.field(System.class, PrintStream.class, "out"), "println", void.class, AST.constant("Hello World!")),
                AST.$return()
        }, methodBody);
    }

    private Method complementWithEnclosedVariables(CodePointer codePointer) throws IOException {
        final Method backingMethod = getBackingMethodInClosestLambda(codePointer);
        return Lambdas.withEnclosedVariables(decompiler, backingMethod);
    }

    private Method getBackingMethodInClosestLambda(CodePointer codePointer) {
        final Lambda lambda = SyntaxTreeVisitor.search(codePointer.getElement(), e -> e.getElementType() == ElementType.LAMBDA).get().as(Lambda.class);
        return getBackingMethod(codePointer, lambda);
    }

    private Method getBackingMethod(CodePointer codePointer, Lambda expectedLambda) {
        return codePointer.getMethod().getClassFile().getMethods().stream()
                .filter(m -> m.getName().equals(expectedLambda.getBackingMethodName()))
                .findFirst()
                .get();
    }
}
