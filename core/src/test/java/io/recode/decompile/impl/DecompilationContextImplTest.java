package io.recode.decompile.impl;

import io.recode.TypeResolver;
import io.recode.classfile.Method;
import io.recode.decompile.Decompiler;
import io.recode.decompile.LineNumberCounter;
import io.recode.decompile.ProgramCounter;
import io.recode.model.*;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.NoSuchElementException;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DecompilationContextImplTest {

    private final ProgramCounter programCounter = new ProgramCounterImpl();

    private final Decompiler decompiler = mock(Decompiler.class);

    private final Method method = mock(Method.class);

    private final TypeResolver typeResolver = mock(TypeResolver.class);

    private final LineNumberCounter lineNumberCounter = mock(LineNumberCounter.class);

    private final DecompilationContextImpl context = new DecompilationContextImpl(decompiler, method, programCounter, lineNumberCounter, typeResolver, 1234);

    @Test
    public void constructorShouldNotAcceptInvalidParameters() {
        assertThrown(() -> new DecompilationContextImpl(null, method, programCounter, lineNumberCounter, typeResolver, 0), AssertionError.class);
        assertThrown(() -> new DecompilationContextImpl(decompiler, null, programCounter, lineNumberCounter, typeResolver, 0), AssertionError.class);
        assertThrown(() -> new DecompilationContextImpl(decompiler, method, programCounter, null, typeResolver, 0), AssertionError.class);
        assertThrown(() -> new DecompilationContextImpl(decompiler, method, null, lineNumberCounter, typeResolver, 0), AssertionError.class);
        assertThrown(() -> new DecompilationContextImpl(decompiler, method, programCounter, lineNumberCounter, null, 0), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        assertEquals(decompiler, context.getDecompiler());
        assertEquals(method, context.getMethod());
        assertEquals(programCounter, context.getProgramCounter());
        assertEquals(lineNumberCounter, context.getLineNumberCounter());
        assertEquals(1234, context.getStartPC());
    }

    @Test
    public void resolveTypeShouldNotAcceptNullType() {
        assertThrown(() -> context.resolveType(null), AssertionError.class);
        assertThrown(() -> context.resolveType(""), AssertionError.class);
    }

    @Test
    public void resolveTypeShouldReturnTypeFromTypeResolver() {
        when(typeResolver.resolveType(eq("java.lang.String"))).thenReturn(String.class);

        final Type type = context.resolveType("java/lang/String");

        assertEquals(String.class, type);

        verify(typeResolver).resolveType(eq("java.lang.String"));
    }

    @Test
    public void enlistedStatementsShouldInitiallyBeEmpty() {
        assertTrue(context.getStatements().isEmpty());
    }

    @Test
    public void popShouldFailIfNoExpressionIsAvailableOnStack() {
        assertThrown(context::pop, IllegalStateException.class);
    }

    @Test
    public void popShouldReturnLastPushedExpression() {
        final Expression expression1 = mock(Expression.class);
        final Expression expression2 = mock(Expression.class);

        context.push(expression1);
        context.push(expression2);

        assertEquals(expression2, context.pop());
        assertEquals(expression1, context.pop());
    }

    @Test
    public void pushShouldNotAcceptNullElement() {
        assertThrown(() -> context.push(null), AssertionError.class);
    }

    @Test
    public void enlistShouldNotAcceptNullArg() {
        assertThrown(() -> context.enlist(null), AssertionError.class);
    }

    @Test
    public void statementsShouldContainEnlistedStatements() {
        final Statement statement1 = mock(Statement.class);
        final Statement statement2 = mock(Statement.class);

        context.enlist(statement1);
        assertArrayEquals(new Object[]{statement1}, context.getStatements().toArray());

        context.enlist(statement2);
        assertArrayEquals(new Object[]{statement1, statement2}, context.getStatements().toArray());
    }

    @Test
    public void reduceShouldReturnFalseIfStackIsEmpty() {
        assertFalse(context.reduce());
    }

    @Test
    public void reduceShouldFailIfStackContainsNonStatement() {
        context.push(mock(Expression.class));
        assertThrown(context::reduce, IllegalStateException.class);
    }

    @Test
    public void reduceShouldPopAndEnlistStackedStatement() {
        final Expression expression = mock(Expression.class);
        final Expression statement = mock(MethodCall.class);

        context.push(expression);
        context.push(statement);
        context.reduce();

        assertArrayEquals(new Object[]{statement}, context.getStatements().toArray());
        assertEquals(expression, context.pop());
    }

    @Test
    public void reduceAllShouldReturnFalseIfStackIsEmpty() {
        assertFalse(context.reduceAll());
    }

    @Test
    public void reduceAllShouldFailIfStackContainsNonStatement() {
        context.push(mock(Expression.class));

        assertThrown(context::reduceAll, IllegalStateException.class);
    }

    @Test
    public void reduceAllShouldReduceAllStatementsToStatementList() {
        final MethodCall statement1 = mock(MethodCall.class, "s1");
        final MethodCall statement2 = mock(MethodCall.class, "s2");

        when(statement1.getMetaData()).thenReturn(new ElementContextMetaData(0, -1));
        when(statement2.getMetaData()).thenReturn(new ElementContextMetaData(1, -1));

        context.push(statement1);
        context.push(statement2);

        assertTrue(context.reduceAll());
        assertArrayEquals(new Object[]{statement1, statement2}, context.getStatements().toArray());
    }

    @Test
    public void hasStackedExpressionsShouldReturnTrueIfStackContainsExpressions() {
        context.push(mock(Expression.class));
        assertTrue(context.hasStackedExpressions());
    }

    @Test
    public void hasStackedExpressionsShouldReturnFalseIfStackContainsNoExpressions() {
        assertFalse(context.hasStackedExpressions());
    }

    @Test
    public void removeStatementShouldFailForInvalidIndex() {
        assertThrown(() -> context.removeStatement(-1), AssertionError.class);
        assertThrown(() -> context.removeStatement(1), NoSuchElementException.class);
    }

    @Test
    public void removeStatementShouldRemoveStatementAtIndex() {
        context.enlist(mock(Statement.class));
        context.removeStatement(0);

        assertTrue(context.getStatements().isEmpty());
    }

    @Test
    public void reduceShouldRetainExecutionOrder() {
        final MethodCall methodCall1 = mock(MethodCall.class, "s1");
        final MethodCall methodCall2 = mock(MethodCall.class, "s2");

        when(methodCall1.getMetaData()).thenReturn(new ElementContextMetaData(0, -1));
        when(methodCall2.getMetaData()).thenReturn(new ElementContextMetaData(2, -1));

        context.push(methodCall1);
        context.push(methodCall2);

        context.reduce();
        context.reduceAll();

        final Statement[] expectedOrder = {methodCall1, methodCall2};

        assertArrayEquals(expectedOrder, context.getStatements().toArray());
    }

    @Test
    public void getStackedExpressionsShouldReturnEmptyCollectionIfNoExpressionsAreStacked() {
        assertTrue(context.getStackedExpressions().isEmpty());
    }

    @Test
    public void getStackedExpressionsShouldReturnExpressionsOnStack() {
        final Expression expression = mock(Expression.class);

        context.push(expression);

        assertArrayEquals(new Object[]{expression}, context.getStackedExpressions().toArray());
    }

    @Test
    public void peekShouldFailIfStackIsEmpty() {
        assertThrown(context::peek, IllegalStateException.class);
    }

    @Test
    public void peekShouldReturnTopStackElementWithoutChangingTheStack() {
        final Expression stackedExpression = mock(Expression.class);

        context.push(stackedExpression);

        assertEquals(stackedExpression, context.peek());
        assertArrayEquals(new Object[]{stackedExpression}, context.getStackedExpressions().toArray());
    }

    @Test
    public void isAbortedShouldByDefaultBeFalse() {
        assertFalse(context.isAborted());
    }

    @Test
    public void abortShouldSetTheDecompilationToAborted() {
        context.abort();

        assertTrue(context.isAborted());
    }

    @Test
    public void getStackSizeShouldReturn0ForEmptyStack() {
        assertEquals(0, context.getStackSize());
    }

    @Test
    public void getStackSizeShouldReturnNumberOfElements() {
        context.push(AST.constant(1));
        assertEquals(1, context.getStackSize());

        context.push(AST.constant(1));
        assertEquals(2, context.getStackSize());
    }

    @Test
    public void isStackCompliantWithComputationalTypesShouldReturnFalseIfStackContainsFewerElements() {
        context.push(AST.constant(1));
        assertFalse(context.isStackCompliantWithComputationalCategories(1, 2));
    }

    @Test
    public void isStackCompliantWithComputationalTypesShouldReturnFalseIfTypesAreNotOfMatchingType() {
        context.push(AST.constant(1));
        context.push(AST.constant(2));
        context.push(AST.constant(3));

        assertFalse(context.isStackCompliantWithComputationalCategories(1, 2));
        assertFalse(context.isStackCompliantWithComputationalCategories(2, 1));
        assertFalse(context.isStackCompliantWithComputationalCategories(1, 2, 1));
    }

    @Test
    public void isStackCompliantWithComputationalTypesShouldReturnTrueIfStackComplies() {
        context.push(AST.constant(1));
        context.push(AST.constant(2L));
        context.push(AST.constant(3));

        assertTrue(context.isStackCompliantWithComputationalCategories(1, 2, 1));
        assertTrue(context.isStackCompliantWithComputationalCategories(2, 1));
        assertTrue(context.isStackCompliantWithComputationalCategories(1));
    }

    @Test
    public void isStackCompliantWithComputationalTypesShouldReturnTrueIfStackCompliesNonSymmetric() {
        context.push(AST.constant(1L));
        context.push(AST.constant(2));
        context.push(AST.constant(3));

        assertTrue(context.isStackCompliantWithComputationalCategories(2, 1, 1));
        assertTrue(context.isStackCompliantWithComputationalCategories(1, 1));
        assertTrue(context.isStackCompliantWithComputationalCategories(1));
    }

    @Test
    public void isStackCompliantWithComputationalTypesShouldNotAcceptNullTypes() {
        assertThrown(() -> context.isStackCompliantWithComputationalCategories((int[]) null), AssertionError.class);
    }
}
