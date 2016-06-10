package io.recode.decompile.impl;

import io.recode.TypeResolver;
import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.classfile.Method;
import io.recode.decompile.*;
import io.recode.model.ElementContextMetaData;
import io.recode.model.Expression;
import io.recode.model.Statement;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StackInstructionsTest {

    private final StackInstructions stackInstructions = new StackInstructions();
    private final ProgramCounter pc = mock(ProgramCounter.class);
    private final DecompilationContext decompilationContext = new DecompilationContextImpl(mock(Decompiler.class), mock(Method.class), pc, mock(LineNumberCounter.class), mock(TypeResolver.class), 0);
    private final CodeStream codeStream = mock(CodeStream.class);
    private final Expression element1 = mock(Expression.class, withSettings().extraInterfaces(Statement.class).name("element1"));
    private final Expression element2 = mock(Expression.class, withSettings().extraInterfaces(Statement.class).name("element2"));
    private final Expression element3 = mock(Expression.class, withSettings().extraInterfaces(Statement.class).name("element3"));
    private final Expression element4 = mock(Expression.class, withSettings().extraInterfaces(Statement.class).name("element4"));

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> stackInstructions.configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForStackRelatedInstructions() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.pop));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.pop2));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dup));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dup_x1));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dup_x2));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dup2));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dup2_x1));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dup2_x2));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.swap));
    }

    @Test
    public void popShouldFailIfStackIsEmpty() throws IOException {
        assertThrown(() -> execute(ByteCode.pop), ClassFileFormatException.class);
    }

    @Test
    public void popShouldReduceStack() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        decompilationContext.push(element1);

        execute(ByteCode.pop);

        assertTrue(decompilationContext.getStackedExpressions().isEmpty());
        assertEquals(Arrays.asList(element1), decompilationContext.getStatements().all().get());
    }

    @Test
    public void popShouldFailForInvalidTypeOnStack() throws IOException {
        when(element1.getType()).thenReturn(long.class);
        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.pop), ClassFileFormatException.class);
    }

    @Test
    public void pop2ShouldReduceTwiceForComputationalCategories1() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element1.getMetaData()).thenReturn(new ElementContextMetaData(1, -1));
        when(element2.getMetaData()).thenReturn(new ElementContextMetaData(2, -1));

        decompilationContext.push(element1);
        decompilationContext.push(element2);

        execute(ByteCode.pop2);

        assertTrue(decompilationContext.getStackedExpressions().isEmpty());
        assertEquals(Arrays.asList(element1, element2), decompilationContext.getStatements().all().get());
    }

    @Test
    public void pop2ShouldFailIfLastElementIsComputationalCategory1ButNotSecondLast() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(long.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.pop2), ClassFileFormatException.class);
    }

    @Test
    public void pop2ShouldFailIfStackIsEmpty() throws IOException {
        assertThrown(() -> execute(ByteCode.pop2), ClassFileFormatException.class);
    }

    @Test
    public void pop2ShouldPopSingleElementIfElementIsOfComputationalType2() throws IOException {
        when(element1.getType()).thenReturn(long.class);

        decompilationContext.push(element1);

        execute(ByteCode.pop2);

        assertTrue(decompilationContext.getStackedExpressions().isEmpty());
        assertEquals(Arrays.asList(element1), decompilationContext.getStatements().all().get());
    }

    @Test
    public void dupShouldDuplicateStackedElement() throws IOException {
        when(element1.getType()).thenReturn(int.class);

        decompilationContext.push(element1);

        execute(ByteCode.dup);

        assertEquals(Arrays.asList(element1, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dupShouldFailIfStackIsEmpty() throws IOException {
        assertThrown(() -> execute(ByteCode.dup), ClassFileFormatException.class);
    }

    @Test
    public void dupShouldFailIfStackedElementIsIncorrect() throws IOException {
        when(element1.getType()).thenReturn(long.class);
        decompilationContext.push(element1);
        assertThrown(() -> execute(ByteCode.dup), ClassFileFormatException.class);
    }

    @Test
    public void dup_x1ShouldInsertTopElement() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup_x1);

        assertEquals(Arrays.asList(element1, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup_x1ShouldFailIfStackIsEmpty() {
        assertThrown(() -> execute(ByteCode.dup_x1), ClassFileFormatException.class);
    }

    @Test
    public void dup_x1ShouldFailIfStackContainsOneElement() {
        when(element1.getType()).thenReturn(int.class);
        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.dup_x1), ClassFileFormatException.class);
    }

    @Test
    public void dup_x2ShouldInsertTopElementForStackWithTwoElements() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(int.class);

        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup_x2);

        assertEquals(Arrays.asList(element1, element3, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup_x2ShouldFailIfStackContainsThreeElementsWhereAnyIsIncorrect() {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(long.class);

        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.dup_x2), ClassFileFormatException.class);
    }

    @Test
    public void dup_x2ShouldInsertTopElementForStackWithThreeElements() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(long.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup_x2);

        assertEquals(Arrays.asList(element1, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup_x2ShouldFailIfStackContainsTwoElementsWhereAnyIsInvalid() {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.dup_x2), ClassFileFormatException.class);
    }

    @Test
    public void dup2ShouldDuplicateTopTwoElements() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup2);

        assertEquals(Arrays.asList(element2, element1, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup2ShouldFailIfOneElementOfComputationalType1IsStacked() {
        when(element1.getType()).thenReturn(int.class);

        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.dup2), ClassFileFormatException.class);
    }

    @Test
    public void dup2ShouldDuplicateElementIfOneElementIsStacked() throws IOException {
        when(element1.getType()).thenReturn(long.class);

        decompilationContext.push(element1);

        execute(ByteCode.dup2);

        assertEquals(Arrays.asList(element1, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup2_x1ShouldDuplicateTwoElementsAndInsert() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(int.class);

        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup2_x1);

        assertEquals(Arrays.asList(element2, element1, element3, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup2_x1WithThreeStackedElementsShouldFailIfAnyIsInvalid() {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(long.class);

        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.dup2_x1), ClassFileFormatException.class);
    }

    @Test
    public void dup2_x1ShouldDuplicateOneElementIfStackSizeIsTwo() throws IOException {
        when(element1.getType()).thenReturn(long.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.dup2_x1);

        assertEquals(Arrays.asList(element1, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup2_x1ShouldFailForTwoStackedElementsWhereAnyIsInvalid() {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.dup2_x1), ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithStackSize4ShouldDuplicateTwoElementsAndInsert() throws IOException {
        decompilationContext.push(element4);
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(int.class);
        when(element4.getType()).thenReturn(int.class);

        execute(ByteCode.dup2_x2);

        assertEquals(Arrays.asList(element2, element1, element4, element3, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup2_x2WithStackSize4AndInvalidComputationalTypeShouldDuplicateTwoElementsAndInsert() throws IOException {
        decompilationContext.push(element4);
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(long.class);
        when(element3.getType()).thenReturn(int.class);
        when(element4.getType()).thenReturn(int.class);

        assertThrown(() -> execute(ByteCode.dup2_x2), ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithStackSizeTreeAndComputationalTypes211ShouldDuplicateOneElement() throws IOException {
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element1.getType()).thenReturn(long.class);
        when(element2.getType()).thenReturn(int.class);
        when(element3.getType()).thenReturn(int.class);

        execute(ByteCode.dup2_x2);

        assertEquals(Arrays.asList(element1, element3, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup2_x2WithStackSizeTreeAndComputationalTypes122ShouldDuplicateTwoElements() throws IOException {
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element3.getType()).thenReturn(long.class);
        when(element2.getType()).thenReturn(int.class);
        when(element1.getType()).thenReturn(int.class);

        execute(ByteCode.dup2_x2);

        assertEquals(Arrays.asList(element2, element1, element3, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup2_x2WithStackSize2ShouldDuplicateOneElement() throws IOException {
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element2.getType()).thenReturn(long.class);
        when(element1.getType()).thenReturn(long.class);

        execute(ByteCode.dup2_x2);

        assertEquals(Arrays.asList(element1, element2, element1), decompilationContext.getStackedExpressions());
    }

    @Test
    public void dup2_x2WithStackSize2AndInvalidStackShouldFail() throws IOException {
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element2.getType()).thenReturn(long.class);
        when(element1.getType()).thenReturn(int.class);

        assertThrown(() -> execute(ByteCode.dup2_x2), ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithStackSizeTreeAndInvalidComputationalTypesShouldFail() throws IOException {
        decompilationContext.push(element3);
        decompilationContext.push(element2);
        decompilationContext.push(element1);

        when(element3.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);
        when(element1.getType()).thenReturn(int.class);

        assertThrown(() -> execute(ByteCode.dup2_x2), ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithEmptyStackShouldFail() throws IOException {
        assertThrown(() -> execute(ByteCode.dup2_x2), ClassFileFormatException.class);
    }

    @Test
    public void dup2_x2WithInvalidStackSizeShouldFail() throws IOException {
        decompilationContext.push(element1);
        assertThrown(() -> execute(ByteCode.dup2_x2), ClassFileFormatException.class);
    }

    @Test
    public void swapShouldFailIfStackIsEmpty() throws IOException {
        assertThrown(() -> execute(ByteCode.swap), ClassFileFormatException.class);
    }

    @Test
    public void swapShouldFailIfStackContains1Value() throws IOException {
        decompilationContext.push(element1);
        assertThrown(() -> execute(ByteCode.swap), ClassFileFormatException.class);
    }

    @Test
    public void swapShouldRearrangeTopElements() throws IOException {
        when(element1.getType()).thenReturn(int.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        execute(ByteCode.swap);

        assertEquals(Arrays.asList(element1, element2), decompilationContext.getStackedExpressions());
    }

    @Test
    public void swapShouldFailIfStackContainsInvalidElements() {
        when(element1.getType()).thenReturn(long.class);
        when(element2.getType()).thenReturn(int.class);

        decompilationContext.push(element2);
        decompilationContext.push(element1);

        assertThrown(() -> execute(ByteCode.swap), ClassFileFormatException.class);
    }

    private void execute(int byteCode) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, byteCode).apply(decompilationContext, codeStream, byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder configurationBuilder = DecompilerConfigurationImpl.newBuilder();
        stackInstructions.configure(configurationBuilder);
        return configurationBuilder.build();
    }
}