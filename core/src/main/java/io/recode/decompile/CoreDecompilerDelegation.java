package io.recode.decompile;

import io.recode.decompile.impl.*;

public final class CoreDecompilerDelegation implements DecompilerDelegation {

    private final DecompilerDelegation[] delegations = new DecompilerDelegation[]{
            new VariableInstructions(),
            new ArrayInstructions(),
            new InstantiationInstructions(),
            new InvokeDynamicInstructions(),
            new MethodCallInstructions(),
            new FieldInstructions(),
            new CastInstructions(),
            new BinaryOperations(),
            new ConstantInstructions(),
            new StackInstructions(),
            new VariousInstructions(),
            new UnaryOperations(),
            new ControlFlowInstructions(),
            new BooleanOperations()
    };

    @Override
    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        for (DecompilerDelegation delegation : delegations) {
            delegation.configure(configurationBuilder);
        }
    }

    public static DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder configurationBuilder = DecompilerConfigurationImpl.newBuilder();

        new CoreDecompilerDelegation().configure(configurationBuilder);

        return configurationBuilder.build();
    }
}
