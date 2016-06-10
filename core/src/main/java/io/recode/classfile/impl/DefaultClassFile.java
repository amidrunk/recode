package io.recode.classfile.impl;

import io.recode.classfile.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class DefaultClassFile implements ClassFile {

    private final int minorVersion;

    private final int majorVersion;

    private final ConstantPool constantPool;

    private final int accessFlags;

    private final String className;

    private final String superClassName;

    private final String[] interfaceNames;

    private final Field[] fields;

    private final Constructor[] constructors;

    private final Method[] methods;

    private final Attribute[] attributes;

    private DefaultClassFile(int minorVersion,
                             int majorVersion,
                             ConstantPool constantPool,
                             int accessFlags,
                             String className,
                             String superClassName,
                             String[] interfaceNames,
                             Field[] fields,
                             Constructor[] constructors,
                             Method[] methods,
                             Attribute[] attributes) {
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.constantPool = constantPool;
        this.accessFlags = accessFlags;
        this.className = className;
        this.superClassName = superClassName;
        this.interfaceNames = interfaceNames;
        this.fields = fields;
        this.constructors = constructors;
        this.methods = methods;
        this.attributes = attributes;
    }

    @Override
    public int getMinorVersion() {
        return minorVersion;
    }

    @Override
    public int getMajorVersion() {
        return majorVersion;
    }

    @Override
    public ConstantPool getConstantPool() {
        return constantPool;
    }

    @Override
    public int getAccessFlags() {
        return accessFlags;
    }


    @Override
    public String getName() {
        return className;
    }

    @Override
    public String getSuperClassName() {
        return superClassName;
    }

    @Override
    public List<String> getInterfaceNames() {
        return Arrays.asList(interfaceNames);
    }

    @Override
    public List<Field> getFields() {
        return Arrays.asList(fields);
    }

    @Override
    public List<Method> getMethods() {
        return Arrays.asList(methods);
    }

    @Override
    public List<Constructor> getConstructors() {
        return Arrays.asList(constructors);
    }

    @Override
    public List<Attribute> getAttributes() {
        return Arrays.asList(attributes);
    }

    @Override
    public Optional<BootstrapMethodsAttribute> getBootstrapMethodsAttribute() {
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(BootstrapMethodsAttribute.ATTRIBUTE_NAME)) {
                return Optional.of((BootstrapMethodsAttribute) attribute);
            }
        }

        return Optional.empty();
    }

    // TODO Just create a simple builder instead
    public static WithConstantPoolWord fromVersion(int minorVersion, int majorVersion) {
        return constantPool
                -> (accessFlags, className, superClassName, interfaceNames)
                -> (fields)
                -> (constructors)
                -> (methods)
                -> (attributes)
                -> ()
                -> new DefaultClassFile(minorVersion, majorVersion, constantPool, accessFlags, className, superClassName, interfaceNames, fields, constructors, methods, attributes);
    }

    @FunctionalInterface
    public static interface WithConstantPoolWord {

        WithClassSignatureWord withConstantPool(ConstantPool constantPool);

    }

    @FunctionalInterface
    public static interface WithClassSignatureWord {

        WithFieldsWord withSignature(int accessFlags, String className, String superClassName, String[] interfaceNames);

    }

    @FunctionalInterface
    public static interface WithFieldsWord {

        WithConstructorsWord withFields(Field[] fields);

    }

    @FunctionalInterface
    public static interface WithConstructorsWord {

        WithMethodsWord withConstructors(Constructor[] constructors);

    }

    @FunctionalInterface
    public static interface WithMethodsWord {

        WithAttributesWord withMethods(Method[] fields);

    }

    @FunctionalInterface
    public static interface WithAttributesWord {

        CreateClassFileWord withAttributes(Attribute[] attributes);

    }

    @FunctionalInterface
    public static interface CreateClassFileWord {

        ClassFile create();

    }

}
