package io.recode.decompile;

import java.io.IOException;

/**
 * <p>
 * A <code>DecompilerDelegate</code> is responsible for handling some part of a decompilation on behalf of
 * a <code>Decompiler</code>. The delegate is executed under the assumption that a precondition is met,
 * typically specified in the <code>DecompilerConfiguration</code>.
 * </p>
 * <p>
 * <p>
 * <b>Example</b><br />
 * Overriding handling of <code>nop</code>-instructions
 * <pre>{@code
 * compilerConfiguration.on(ByteCode.nop)
 *      .withPriority(Priority.HIGH)
 *      .then((context, codeStream, byteCode) -> {
 *         System.out.println("nop-handling overridden");
 *      });
 * }
 * </pre></p>
 */
@FunctionalInterface
public interface DecompilerDelegate {

    /**
     * A decompiler delegate that does nothing during delegation.
     */
    DecompilerDelegate NOP = new DecompilerDelegate() {
        @Override
        public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
        }
    };

    /**
     * Applies the decompiler delegate. The delegate should apply its handling of the specified byte code by
     * modifying the context accordingly. For example, handling of a <code>dup</code>-instruction would
     * peek the currently stacked value and push it.
     *
     * @param context    The decompilation context that encapsulates the state of the decompiler. Should be modified
     *                   to implement the handling of the byte code.
     * @param codeStream The code stream that contains the code and embedded data. Use this to find e.g. constant
     *                   pool indices subsequent to the instruction.
     * @param byteCode   The byte code that this delegate is being invoke in regards to.
     * @throws IOException Thrown if reading from the <code>codeStream</code> failed.
     */
    void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException;

}
