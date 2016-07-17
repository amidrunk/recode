package io.recode.decompile.impl;

import io.recode.classfile.LocalVariableNotAvailableException;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilerConfigurationBuilder;
import io.recode.decompile.DecompilerDelegate;
import io.recode.decompile.DecompilerDelegation;
import io.recode.model.AST;
import io.recode.util.Methods;
import io.recode.classfile.ByteCode;
import io.recode.classfile.LocalVariable;

import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.recode.classfile.ByteCode.*;
import static io.recode.model.AST.constant;
import static java.util.stream.Collectors.joining;

public final class VariableInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(iload, lload, fload, dload, aload).then(load());
        configurationBuilder.on(istore, lstore, fstore, dstore, astore).then(store());
        configurationBuilder.on(iload_0, iload_3).then(iloadn());
        configurationBuilder.on(fload_0, fload_3).then(floadn());
        configurationBuilder.on(dload_0, dload_3).then(dloadn());
        configurationBuilder.on(lload_0, lload_3).then(lloadn());
        configurationBuilder.on(aload_0, aload_3).then(aloadn());
        configurationBuilder.on(istore_0, istore_3).then(istoren());
        configurationBuilder.on(fstore_0, fstore_3).then(fstoren());
        configurationBuilder.on(dstore_0, dstore_3).then(dstoren());
        configurationBuilder.on(lstore_0, lstore_3).then(lstoren());
        configurationBuilder.on(astore_0, astore_3).then(astoren());
    }

    public static DecompilerDelegate load() {
        return (context,codeStream,byteCode) -> {
            load(context, codeStream.nextByte());
        };
    }

    public static DecompilerDelegate store() {
        return (context,codeStream,byteCode) -> {
            store(context, codeStream.nextByte());
        };
    }

    public static DecompilerDelegate istoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.istore_0);
        };
    }

    public static DecompilerDelegate fstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.fstore_0);
        };
    }

    public static DecompilerDelegate dstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.dstore_0);
        };
    }

    public static DecompilerDelegate lstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.lstore_0);
        };
    }

    public static DecompilerDelegate astoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.astore_0);
        };
    }

    public static DecompilerDelegate iloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.iload_0);
        };
    }

    public static DecompilerDelegate floadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.fload_0);
        };
    }

    public static DecompilerDelegate dloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.dload_0);
        };
    }

    public static DecompilerDelegate lloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.lload_0);
        };
    }

    public static DecompilerDelegate aloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.aload_0);
        };
    }

    private static void store(DecompilationContext context, int index) {
        final int pc = context.getProgramCounter().get();
        final Optional<LocalVariable> localVariableOptional = Methods.findLocalVariableForIndexAndPC(context.getMethod(), index, pc + 1);

        if (!localVariableOptional.isPresent()) {
            throw localVariableNotAvailableException("Value '" + context.getStack().peek() + "' can't be stored", context, index, pc);
        }

        final LocalVariable localVariable = localVariableOptional.get();

        context.enlist(AST.set(index, localVariable.getName(), localVariable.getType(), context.pop()));
    }

    private static void load(DecompilationContext context, int index) {
        final int pc = context.getProgramCounter().get();
        final Optional<LocalVariable> localVariableOptional = Methods.findLocalVariableForIndexAndPC(context.getMethod(), index, pc);

        if (!localVariableOptional.isPresent()) {
            // There can still be a local, but it's impossible to know its typ if it is not a a "this" or a parameter...
            // TODO: Make sure this is handled for static methods and for parameters as well
            if (!Modifier.isStatic(context.getMethod().getAccessFlags())) {
                if (index == 0) {
                    context.push(AST.local("this", context.resolveType(context.getMethod().getClassFile().getName().replace('.', '/')), index));
                    return;
                }
            }

            throw localVariableNotAvailableException("Variable can't be loaded", context, index, pc);
        }

        final LocalVariable localVariable = localVariableOptional.get();

        context.push(AST.local(localVariable.getName(), localVariable.getType(), localVariable.getIndex()));
    }

    private static LocalVariableNotAvailableException localVariableNotAvailableException(String message, DecompilationContext context, int index, int pc) {
        final String description = context.getMethod().getLocalVariableTable()
                .map(localVariableTable
                        -> "[" + localVariableTable.getLocalVariables().stream()
                        .map(v -> v.getIndex() + ":" + v.getName() + "(pc:" + v.getStartPC() + "-" + (v.getStartPC() + v.getLength()) + ")")
                        .collect(joining(", ")) + "]")
                .orElse("[]");

        return new LocalVariableNotAvailableException(message + ": no variable exists at index " + index + "(pc=" + pc + "). Available variables are: " + description);
    }
}
