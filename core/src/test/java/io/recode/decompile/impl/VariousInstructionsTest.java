package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.decompile.CodeStream;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilerConfiguration;
import io.recode.decompile.DecompilerConfigurationBuilder;
import org.junit.Test;

import java.io.IOException;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class VariousInstructionsTest {

    private final VariousInstructions variousInstructions = new VariousInstructions();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final CodeStream codeStream = mock(CodeStream.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> variousInstructions.configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForNopInstruction() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.nop));
    }

    @Test
    public void nopInstructionShouldBeHandled() throws IOException {
        execute(ByteCode.nop);

        verifyZeroInteractions(decompilationContext);
        verifyNoMoreInteractions(codeStream);
    }

    private void execute(int instruction) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, instruction).apply(decompilationContext, codeStream, instruction);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();
        variousInstructions.configure(builder);
        return builder.build();
    }

}