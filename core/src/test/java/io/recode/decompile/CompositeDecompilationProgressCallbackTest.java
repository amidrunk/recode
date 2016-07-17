package io.recode.decompile;

import io.recode.classfile.ByteCode;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static io.recode.test.Assertions.assertThrown;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

public class CompositeDecompilationProgressCallbackTest {

    private final DecompilationProgressCallback callback1 = mock(DecompilationProgressCallback.class);
    private final DecompilationProgressCallback callback2 = mock(DecompilationProgressCallback.class);
    private final CompositeDecompilationProgressCallback callback = new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{callback1, callback2});
    private final DecompilationContext context = mock(DecompilationContext.class);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new CompositeDecompilationProgressCallback(null), AssertionError.class);
        assertThrown(() -> new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{null}), AssertionError.class);
    }

    @Test
    public void beforeInstructionShouldDelegateToTargetCallbacks() {
        callback.beforeInstruction(context, ByteCode.aaload);

        final InOrder inOrder = Mockito.inOrder(callback1, callback2);

        inOrder.verify(callback1).beforeInstruction(eq(context), eq(ByteCode.aaload));
        inOrder.verify(callback2).beforeInstruction(eq(context), eq(ByteCode.aaload));
    }

    @Test
    public void afterInstructionShouldDelegateToTargetCallbacks() {
        callback.afterInstruction(context, ByteCode.aaload);

        final InOrder inOrder = Mockito.inOrder(callback1, callback2);

        inOrder.verify(callback1).afterInstruction(eq(context), eq(ByteCode.aaload));
        inOrder.verify(callback2).afterInstruction(eq(context), eq(ByteCode.aaload));
    }
}
