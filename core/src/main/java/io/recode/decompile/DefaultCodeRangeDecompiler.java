package io.recode.decompile;

import io.recode.classfile.LineNumberTable;
import io.recode.classfile.LineNumberTableEntry;
import io.recode.classfile.Method;
import io.recode.decompile.impl.InputStreamCodeStream;
import io.recode.decompile.impl.ProgramCounterImpl;
import io.recode.model.Element;
import io.recode.util.InputStreams;
import io.recode.util.Range;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DefaultCodeRangeDecompiler implements CodeRangeDecompiler {

    private final Decompiler decompiler;

    public DefaultCodeRangeDecompiler(Decompiler decompiler) {
        assert decompiler != null : "decompiler can't be null";
        this.decompiler = decompiler;
    }

    @Override
    public Element[] decompileRange(Method method, int fromLineNumber, int toLineNumber) throws IOException {
        assert method != null : "method can't be null";
        assert fromLineNumber >= 0 : "fromLineNumber must be positive";
        assert toLineNumber >= fromLineNumber : "toLineNumber must be greater than or equal to fromLineNumber";

        final LineNumberTable lineNumberTable = method.getLineNumberTable()
                .orElseThrow(() -> new IllegalArgumentException("The provided method does not contain a line number table"));

        final Range sourceFileRange = lineNumberTable.getSourceFileRange();

        if (fromLineNumber < sourceFileRange.getFrom()) {
            throw new IllegalArgumentException("starting line number precedes method line number range " +
                    "[" + sourceFileRange.getFrom() + ", " + sourceFileRange.getTo() + "]");
        }

        if (toLineNumber > sourceFileRange.getTo()) {
            throw new IllegalArgumentException("end line number excedes method line number range " +
                    "[" + sourceFileRange.getFrom() + ", " + sourceFileRange.getTo() + "]");
        }

        int startProgramCounter = -1;
        int endProgramCounter = Integer.MAX_VALUE;

        for (final LineNumberTableEntry entry : lineNumberTable.getEntries()) {
            if (startProgramCounter == -1 && entry.getLineNumber() >= fromLineNumber) {
                startProgramCounter = entry.getStartPC();
            } else if (entry.getLineNumber() > toLineNumber) {
                endProgramCounter = entry.getStartPC() - 1;
                break;
            }
        }

        final InputStream in = InputStreams.range(method.getCode().getCode(), startProgramCounter, endProgramCounter + 1);
        final CodeStream codeStream = new InputStreamCodeStream(in, new ProgramCounterImpl(startProgramCounter));

        return decompiler.parse(method, codeStream);
    }
}
