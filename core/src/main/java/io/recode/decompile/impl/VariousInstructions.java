package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.decompile.DecompilerConfigurationBuilder;
import io.recode.decompile.DecompilerDelegate;
import io.recode.decompile.DecompilerDelegation;

public final class VariousInstructions implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfigurationBuilder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompiler configuration builder can't be null";

        decompilerConfigurationBuilder.on(ByteCode.nop).then(nop());
    }

    public static DecompilerDelegate nop() {
        return DecompilerDelegate.NOP;
    }
}
