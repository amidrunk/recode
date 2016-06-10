package io.recode.decompile;

import io.recode.classfile.ByteCode;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DecompilationStateSelectorTest {

    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);

    @Test
    public void andShouldNotAcceptNullArg() {
        assertThrown(() -> DecompilationStateSelector.ALL.and(null), AssertionError.class);
    }

    @Test
    public void andShouldSelectStateIfBothSelectorsMatches() {
        final DecompilationStateSelector selector1 = (context, byteCode) -> true;
        final DecompilationStateSelector selector2 = (context, byteCode) -> true;

        assertTrue(selector1.and(selector2).select(decompilationContext, ByteCode.nop));
    }

    @Test
    public void andShouldNotSelectStateIfAnySelectorDoesNotMatch() {
        final DecompilationStateSelector selector1 = (context, byteCode) -> true;
        final DecompilationStateSelector selector2 = (context, byteCode) -> false;

        assertFalse(selector1.and(selector2).select(decompilationContext, ByteCode.nop));
        assertFalse(selector2.and(selector1).select(decompilationContext, ByteCode.nop));
    }
}