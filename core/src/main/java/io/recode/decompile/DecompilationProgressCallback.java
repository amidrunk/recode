package io.recode.decompile;

public interface DecompilationProgressCallback {

    DecompilationProgressCallback NULL = new DecompilationProgressCallbackAdapter();

    void beforeInstruction(DecompilationContext context);

    void preparingInstruction(DecompilationContext context, int instruction);

    void afterInstruction(DecompilationContext context);

}
