package io.recode.classfile;

import io.recode.util.Range;

import java.util.List;

public interface LineNumberTable extends Attribute {

    String ATTRIBUTE_NAME = "LineNumberTable";

    List<LineNumberTableEntry> getEntries();

    Range getSourceFileRange();

    default String getName() {
        return ATTRIBUTE_NAME;
    }

}
