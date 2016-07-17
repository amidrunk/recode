package io.recode.decompile;

public interface DecompilationProgressCallback {

    DecompilationProgressCallback NULL = new DecompilationProgressCallbackAdapter();

    void beforeInstruction(DecompilationContext context, int instruction);

    void afterInstruction(DecompilationContext context, int instruction);

}
