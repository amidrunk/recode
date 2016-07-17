package io.recode.decompile;

import io.recode.classfile.ByteCode;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class DecompilationProgressCallbackTest {

    @Test
    public void nullCallbackShouldIgnoreCall() {
        final DecompilationContext context = mock(DecompilationContext.class);

        try {
            DecompilationProgressCallback.NULL.afterInstruction(context, ByteCode.nop);
        } catch (Exception e) {
            throw e;
        }

        verifyZeroInteractions(context);
    }
}
