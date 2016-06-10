package io.recode.decompile;

import io.recode.classfile.ByteCode;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class CoreDecompilerDelegationTest {

    @Test
    public void createShouldCreateCoreConfiguration() {
        final DecompilerConfiguration configuration = CoreDecompilerDelegation.configuration();

        assertNotNull(configuration.getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.iconst_0));
    }

}