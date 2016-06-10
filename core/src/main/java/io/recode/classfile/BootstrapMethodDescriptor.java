package io.recode.classfile;

public interface BootstrapMethodDescriptor extends ConstantPoolEntryDescriptor {

    MethodRefDescriptor getBootstrapMethodRefDescriptor();

    ConstantPoolEntryDescriptor[] getBootstrapArgumentsDescriptor();

}
