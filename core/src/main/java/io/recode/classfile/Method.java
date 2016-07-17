package io.recode.classfile;

import io.recode.model.Signature;

import java.util.Optional;

public interface Method extends Member {

    CodeAttribute getCode();

    LocalVariable getLocalVariableForIndex(int index);

    Optional<LocalVariableTable> getLocalVariableTable();

    Optional<LineNumberTable> getLineNumberTable();

    Signature getSignature();

    Method withLocalVariableTable(LocalVariableTable localVariableTable);

    boolean hasCodeForLineNumber(int lineNumber);

    boolean  isLambdaBackingMethod();

}
