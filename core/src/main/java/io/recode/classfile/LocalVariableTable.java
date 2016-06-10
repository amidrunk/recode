package io.recode.classfile;

import java.util.List;

public interface LocalVariableTable extends Attribute {

    String ATTRIBUTE_NAME = "LocalVariableTable";

    List<LocalVariable> getLocalVariables();

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
