package io.recode.classfile;

import java.util.List;
import java.util.Optional;

public interface ClassFile {

    int getMinorVersion();

    int getMajorVersion();

    ConstantPool getConstantPool();

    int getAccessFlags();

    String getName();

    String getSuperClassName();

    List<String> getInterfaceNames();

    List<Field> getFields();

    List<Method> getMethods();

    List<Constructor> getConstructors();

    List<Attribute> getAttributes();

    Optional<BootstrapMethodsAttribute> getBootstrapMethodsAttribute();

}
