package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilerConfigurationBuilder;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class InvokeDynamicInstructionsTest {

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> new InvokeDynamicInstructions().configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldExtendBuilderWithDynamicInvokeSupport() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();

        new InvokeDynamicInstructions().configure(builder);

        assertNotNull(builder.build().getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.invokedynamic));
    }

}
