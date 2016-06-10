package io.recode.decompile;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class DecompilationProgressCallbackTest {

    @Test
    public void nullCallbackShouldIgnoreCall() {
        final DecompilationContext context = mock(DecompilationContext.class);

        try {
            DecompilationProgressCallback.NULL.afterInstruction(context);
        } catch (Exception e) {
            throw e;
        }

        verifyZeroInteractions(context);
    }
}
