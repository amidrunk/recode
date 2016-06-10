package io.recode.decompile;

import io.recode.classfile.ByteCode;
import io.recode.model.AST;
import io.recode.model.ElementType;
import io.recode.model.Statement;
import io.recode.util.Stack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Arrays;
import java.util.function.Predicate;

import static io.recode.model.AST.constant;
import static io.recode.model.ModelQueries.equalTo;
import static io.recode.test.Assertions.assertThrown;
import static io.recode.util.Sequences.emptySequence;
import static io.recode.util.Sequences.sequenceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class DecompilationStateSelectorsTest {

    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final Stack stack = mock(Stack.class);

    @Before
    public void setup() {
        when(decompilationContext.getStack()).thenReturn(stack);
    }

    @Test
    public void atLeastOneStatementShouldNotMatchContextWithoutStatements() {
        when(decompilationContext.getStatements()).thenReturn(emptySequence());

        final DecompilationStateSelector selector = DecompilationStateSelectors.atLeastOneStatement();

        assertFalse(selector.select(decompilationContext, ByteCode.nop));
    }

    @Test
    public void atLeastOneStatementShouldMatchContextWithNonEmptyStatements() {
        when(decompilationContext.getStatements()).thenReturn(sequenceOf(mock(Statement.class)));

        final DecompilationStateSelector selector = DecompilationStateSelectors.atLeastOneStatement();

        assertTrue(selector.select(decompilationContext, ByteCode.nop));
    }


    @Test
    public void elementIsStackedShouldNotAcceptNullArg() {
        assertThrown(() -> DecompilationStateSelectors.elementIsStacked(null), AssertionError.class);
    }

    @Test
    public void elementIsStackedShouldNotSelectContextWithEmptyStack() {
        when(stack.isEmpty()).thenReturn(true);

        assertFalse(DecompilationStateSelectors.elementIsStacked(ElementType.CONSTANT).select(decompilationContext, ByteCode.nop));
    }

    @Test
    public void elementIsStackedShouldNotSelectContextWithIncorrectElement() {
        when(stack.isEmpty()).thenReturn(false);
        when(stack.peek()).thenReturn(AST.cast(constant(1)).to(byte.class));

        assertFalse(DecompilationStateSelectors.elementIsStacked(ElementType.CONSTANT).select(decompilationContext, ByteCode.nop));
    }

    @Test
    public void elementIsStackedShouldSelectContextWithMatchingElementOnStack() {
        when(stack.isEmpty()).thenReturn(false);
        when(stack.peek()).thenReturn(constant(1));

        assertTrue(DecompilationStateSelectors.elementIsStacked(ElementType.CONSTANT).select(decompilationContext, ByteCode.nop));
    }

    @Test
    public void stackSizeIsAtLeastShouldNotAcceptZeroOrNegativeCount() {
        assertThrown(() -> DecompilationStateSelectors.stackSizeIsAtLeast(-1), AssertionError.class);
        assertThrown(() -> DecompilationStateSelectors.stackSizeIsAtLeast(0), AssertionError.class);
    }

    @Test
    public void stackSizeIsAtLeastShouldNotMatchStackWithFewerElements() {
        final DecompilationStateSelector selector = DecompilationStateSelectors.stackSizeIsAtLeast(2);

        when(stack.size()).thenReturn(0);
        assertFalse(selector.select(decompilationContext, ByteCode.nop));

        when(stack.size()).thenReturn(1);
        assertFalse(selector.select(decompilationContext, ByteCode.nop));

        when(stack.size()).thenReturn(2);
        assertTrue(selector.select(decompilationContext, ByteCode.nop));

        when(stack.size()).thenReturn(3);
        assertTrue(selector.select(decompilationContext, ByteCode.nop));
    }

    @Test
    public void elementsAreStackedWithPredicatesShouldNotAcceptNullPredicates() {
        assertThrown(() -> DecompilationStateSelectors.elementsAreStacked((Predicate[]) null), AssertionError.class);
    }

    @Test
    public void elementsAreStackedWithPredicateShouldNotMatchSmallerStack() {
        when(stack.size()).thenReturn(1);

        assertFalse(DecompilationStateSelectors.elementsAreStacked(equalTo(constant(1)), equalTo(constant(2))).select(decompilationContext, ByteCode.nop));
    }

    @Test
    public void elementsAreStackedWithPredicatesShouldNotMatchIfAnyPredicateDoesNotMatch() {
        when(stack.size()).thenReturn(2);
        when(stack.tail(Matchers.eq(-2))).thenReturn(Arrays.asList(constant(1), constant(2)));

        assertFalse(DecompilationStateSelectors.elementsAreStacked(equalTo(constant(1)), equalTo(constant(3))).select(decompilationContext, ByteCode.nop));
    }

    @Test
    public void elementsAreStackedWithPredicatesShouldMatchIfAllPredicatesMatch() {
        when(stack.size()).thenReturn(2);
        when(stack.tail(Matchers.eq(-2))).thenReturn(Arrays.asList(constant(1), constant(2)));

        assertTrue(DecompilationStateSelectors.elementsAreStacked(equalTo(constant(1)), equalTo(constant(2))).select(decompilationContext, ByteCode.nop));
    }
}