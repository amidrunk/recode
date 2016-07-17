package io.recode.classfile;

import io.recode.util.Range;

import java.util.List;

public interface LineNumberTable extends Attribute {

    String ATTRIBUTE_NAME = "LineNumberTable";

    List<LineNumberTableEntry> getEntries();

    Range getSourceFileRange();

    int getLineNumber(int pc);

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
