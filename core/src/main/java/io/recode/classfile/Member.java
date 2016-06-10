package io.recode.classfile;

import java.util.List;

public interface Member {

    int getAccessFlags();

    String getName();

    List<Attribute> getAttributes();

    ClassFile getClassFile();

}
