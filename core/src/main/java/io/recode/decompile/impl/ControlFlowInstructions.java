package io.recode.decompile.impl;

import io.recode.decompile.*;
import io.recode.util.Methods;
import io.recode.util.Types;
import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.classfile.ExceptionTableEntry;
import io.recode.model.Expression;
import io.recode.model.Goto;
import io.recode.model.impl.GotoImpl;
import io.recode.model.impl.ReturnImpl;
import io.recode.model.impl.ReturnValueImpl;
import io.recode.util.Priority;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

import static io.recode.decompile.DecompilationContextQueries.lastStatement;
import static io.recode.decompile.DecompilerDelegates.forQuery;

/**
 * The <code>ControlFlowInstructions</code> handles instructions related to control flow in the java byte
 * code format. This includes return statements and branching, as well as transformations that need to be
 * applied to re-engineer the syntax tree from the binary code format.
 */
public final class ControlFlowInstructions implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.return_).then(return_());
        configurationBuilder.on(ByteCode.ireturn).then(ireturn());
        configurationBuilder.on(ByteCode.lreturn).then(lreturn());
        configurationBuilder.on(ByteCode.freturn).then(freturn());
        configurationBuilder.on(ByteCode.dreturn).then(dreturn());
        configurationBuilder.on(ByteCode.areturn).then(areturn());
        configurationBuilder.on(ByteCode.goto_).then(goto_());

        configurationBuilder.after(ByteCode.goto_)
                .withPriority(Priority.HIGH)
                .then(forQuery(lastStatement().as(Goto.class)).apply(tryCatch()));
    }

    private static DecompilerElementDelegate<Goto> tryCatch() {
        return new DecompilerElementDelegate<Goto>() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode, Goto gotoElement) throws IOException {
                final Optional<ExceptionTableEntry> exceptionTableEntry = Methods.getExceptionTableEntryForCatchLocation(
                        context.getMethod(),
                        gotoElement.getMetaData().getProgramCounter());

                if (exceptionTableEntry.isPresent()) {
                    if (exceptionTableEntry.get().getStartPC() <= context.getStartPC() + 1) {
                        // Decompiling a try-catch body. In this case, the try exists prior to the code being decompiled.
                        // This occurs when decompiling a line surrounded by a try-catch. It's not possible to decompile
                        // past this jump, so we need to escape.

                        context.getStatements().last().remove();
                        context.abort();
                        return;
                    }

                    // throw new UnsupportedOperationException("Try-catch not implemented");
                    // TODO Implement try-catch
                }
            }
        };
    }

    public static DecompilerDelegate goto_() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final int programCounter = context.getProgramCounter().get();
                final int relativeOffset = codeStream.nextSignedShort();
                final int absoluteOffset = programCounter + relativeOffset;

                if (absoluteOffset < context.getStartPC()) {
                    // This occurs when decompiling e.g. the body of a loop. The end of the loop will jump back to
                    // the start of the body, which will be before the decompilation when a.g. a particular line
                    // is being decompiled. It is not possible to proceed ahead of the jump back.

                    context.abort();
                    return;
                }

                context.enlist(new GotoImpl(absoluteOffset));
            }
        };
    }

    public static DecompilerDelegate return_() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                context.reduceAll();
                context.enlist(new ReturnImpl());
            }
        };
    }

    public static DecompilerDelegate ireturn() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression returnValue = context.getStack().pop();

                switch (returnValue.getType().getTypeName()) {
                    case "boolean":
                    case "byte":
                    case "short":
                    case "char":
                    case "int":
                        context.enlist(new ReturnValueImpl(returnValue));
                        break;
                    default:
                        throw invalidReturnValue(byteCode, returnValue);
                }
            }
        };
    }

    public static DecompilerDelegate lreturn() {
        return xreturn(long.class);
    }

    public static DecompilerDelegate freturn() {
        return xreturn(float.class);
    }

    public static DecompilerDelegate dreturn() {
        return xreturn(double.class);
    }

    public static DecompilerDelegate areturn() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression returnValue = context.getStack().pop();

                if (Types.isPrimitive(returnValue.getType())) {
                    throw invalidReturnValue(byteCode, returnValue);
                }

                context.enlist(new ReturnValueImpl(returnValue));
            }
        };
    }

    private static DecompilerDelegate xreturn(Type type) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression returnValue = context.getStack().pop();

                if (!returnValue.getType().equals(type)) {
                    throw invalidReturnValue(byteCode, returnValue);
                }

                context.enlist(new ReturnValueImpl(returnValue));
            }
        };
    }

    private static ClassFileFormatException invalidReturnValue(int byteCode, Expression returnValue) {
        return new ClassFileFormatException("Invalid return value on stack: "
                + ByteCode.toString(byteCode) + " can't return " + returnValue);
    }
}
