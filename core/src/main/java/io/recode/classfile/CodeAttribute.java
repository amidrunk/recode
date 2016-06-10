package io.recode.classfile;

import java.io.InputStream;
import java.util.List;

public interface CodeAttribute extends Attribute {

    public static final String ATTRIBUTE_NAME = "Code";

    int getMaxStack();

    int getMaxLocals();

    InputStream getCode();

    int getCodeLength();

    List<ExceptionTableEntry> getExceptionTable();

    List<Attribute> getAttributes();

    CodeAttribute withLocalVariableTable(LocalVariableTable localVariableTable);

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
