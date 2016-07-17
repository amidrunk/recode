package io.recode.decompile;

import io.recode.classfile.Method;
import io.recode.model.Element;
import io.recode.util.Range;

import java.io.IOException;
import java.util.List;

/**
 * A decompiler that provides support for decompiling code between a start and end line number.
 * This is useful when interpreting calling code.
 */
public interface CodeRangeDecompiler {

    /**
     * Decompiles all lines between the provided line numbers (inclusive).
     *
     * @param method The method that should be decompiled.
     * @param fromLineNumber The starting line number. This must map to a line contained within the
     *                       method.
     * @param toLineNumber The end line number. This must map to a line contained within the method.
     * @return The statements contained between the provided lines.
     */
    Element[] decompileRange(Method method, int fromLineNumber, int toLineNumber) throws IOException;

    default Element[] decompileRange(Method method, Range range) throws IOException {
        return decompileRange(method, range.getFrom(), range.getTo());
    }

}
