package io.recode.codegeneration;

import io.recode.classfile.Method;
import io.recode.decompile.CodePointer;
import io.recode.decompile.impl.CodePointerImpl;
import io.recode.model.AST;
import io.recode.model.Constant;
import io.recode.model.ElementType;
import org.junit.Test;

import java.util.function.Predicate;

import static io.recode.codegeneration.impl.TestUtils.assertThrown;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ElementSelectorTest {

    private final Method method = mock(Method.class);

    @Test
    public void forTypeShouldNotAcceptNullElementType() {
        assertThrown(() -> ElementSelector.forType(null), AssertionError.class);
    }

    @Test
    public void selectorForTypeShouldMatchEqualElementType() {
        final ElementSelector<Constant> selector = ElementSelector.<Constant>forType(ElementType.CONSTANT);
        final CodePointer<Constant> codePointer = new CodePointerImpl<>(mock(Method.class), AST.constant(1));

        assertTrue(selector.matches(codePointer));
    }

    @Test
    public void selectorForTypeShouldNotMatchDifferentElementType() {
        final ElementSelector<Constant> selector = ElementSelector.<Constant>forType(ElementType.CONSTANT);
        final CodePointer codePointer = new CodePointerImpl<>(method, AST.$return());

        assertFalse(selector.matches(codePointer));
    }

    @Test
    public void elementSelectorForTypeShouldNotAcceptNullCodePointerWhenMatching() {
        assertThrown(() -> ElementSelector.forType(ElementType.CONSTANT).matches(null), AssertionError.class);
    }

    @Test
    public void elementSelectorForTypeWithPredicateShouldNotCallPredicateIfElementTypeIsIncorrect() {
        final Predicate predicate = mock(Predicate.class);
        final ElementSelector selector = ElementSelector.forType(ElementType.CONSTANT).where(predicate);

        assertFalse(selector.matches(new CodePointerImpl<>(method, AST.$return())));

        verifyZeroInteractions(predicate);
    }

    @Test
    public void elementSelectorForTypeWithPredicateShouldNotAcceptNullPredicate() {
        assertThrown(() -> ElementSelector.forType(ElementType.CONSTANT).where(null), AssertionError.class);
    }

    @Test
    public void elementSelectorForTypeWithPredicateShouldNotMatchIfPredicateDoesNotMatch() {
        final Predicate predicate = mock(Predicate.class);
        final ElementSelector selector = ElementSelector.forType(ElementType.CONSTANT).where(predicate);
        final CodePointerImpl<Constant> codePointer = new CodePointerImpl<>(method, AST.constant(1));

        when(predicate.test(eq(codePointer))).thenReturn(false);

        assertFalse(selector.matches(codePointer));

        verify(predicate).test(eq(codePointer));
    }

    @Test
    public void elementSelectorForTypeWithPredicateShouldMatchIfPredicateMatches() {
        final Predicate predicate = mock(Predicate.class);
        final ElementSelector selector = ElementSelector.forType(ElementType.CONSTANT).where(predicate);
        final CodePointerImpl<Constant> codePointer = new CodePointerImpl<>(method, AST.constant(1));

        when(predicate.test(eq(codePointer))).thenReturn(true);

        assertTrue(selector.matches(codePointer));

        verify(predicate).test(eq(codePointer));
    }
}
