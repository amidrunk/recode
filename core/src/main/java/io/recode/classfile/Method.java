package io.recode.classfile;

import io.recode.util.Range;
import io.recode.model.Signature;

import java.io.InputStream;
import java.util.Optional;

public interface Method extends Member {

    CodeAttribute getCode();

    InputStream getCodeForLineNumber(int lineNumber);

    LocalVariable getLocalVariableForIndex(int index);

    Optional<LocalVariableTable> getLocalVariableTable();

    Optional<LineNumberTable> getLineNumberTable();

    Signature getSignature();

    Method withLocalVariableTable(LocalVariableTable localVariableTable);

    boolean hasCodeForLineNumber(int lineNumber);

    boolean  isLambdaBackingMethod();

    Range getCodeRangeForLineNumber(int lineNumber);

}
