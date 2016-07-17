package io.recode.decompile;

import io.recode.CodeLocation;

import java.io.IOException;

/**
 * A <code>CodeLocationDecompiler</code> decompiles a specific line in a class file and returns an
 * abstract syntax tree of the calling code.
 *
 * TODO: Should be possible to tweak the decompilation so empty stack decompilations can work
 * e.g. it should be possible to decompile ".doStuff(xyz)" where the stack would be empty without
 * decompiling the preceding statement. Need to pre-populate the stack... Should also be possible
 * to search for the next instruction (the argument would be a push for instance).
 */
public interface CodeLocationDecompiler {

    CodePointer[] decompileCodeLocation(CodeLocation codeLocation) throws IOException;

    CodePointer[] decompileCodeLocation(CodeLocation codeLocation, DecompilationProgressCallback callback) throws IOException;

}
