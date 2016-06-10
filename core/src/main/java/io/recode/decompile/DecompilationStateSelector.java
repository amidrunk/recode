package io.recode.decompile;

@FunctionalInterface
public interface DecompilationStateSelector {

    DecompilationStateSelector ALL = (context, byteCode) -> true;

    boolean select(DecompilationContext context, int byteCode);

    default DecompilationStateSelector and(DecompilationStateSelector other) {
        assert other != null : "Other can't be null";

        return (context,byteCode) -> DecompilationStateSelector.this.select(context, byteCode) && other.select(context, byteCode);
    }

}
