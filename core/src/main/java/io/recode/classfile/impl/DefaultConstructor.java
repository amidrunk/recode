package io.recode.classfile.impl;

import io.recode.classfile.Attribute;
import io.recode.classfile.ClassFile;
import io.recode.classfile.Constructor;
import io.recode.model.Signature;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class DefaultConstructor implements Constructor {

    private final Supplier<ClassFile> classFile;

    private final int accessFlags;

    private final String name;

    private final Signature signature;

    private final Attribute[] attributes;

    public DefaultConstructor(Supplier<ClassFile> classFile, int accessFlags, String name, Signature signature, Attribute[] attributes) {
        assert name != null : "Constructor name can't be null (should be \"<init>\")";
        assert name.equals("<init>") : "Constructor name '" + name + "' is not valid; should be '<init>'";
        assert signature != null : "Signature can't be null";
        assert attributes != null : "Attributes can't be null";

        this.classFile = classFile;
        this.accessFlags = accessFlags;
        this.name = name;
        this.signature = signature;
        this.attributes = attributes;
    }

    @Override
    public ClassFile getClassFile() {
        return classFile.get();
    }

    @Override
    public int getAccessFlags() {
        return accessFlags;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Signature getSignature() {
        return signature;
    }

    @Override
    public List<Attribute> getAttributes() {
        return Arrays.asList(attributes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultConstructor)) {
            return false;
        }

        final DefaultConstructor other = (DefaultConstructor) obj;

        return name.equals(other.name)
                && signature.equals(other.signature)
                && accessFlags == other.accessFlags
                && Arrays.equals(attributes, other.attributes);
    }

    @Override
    public int hashCode() {
        return Arrays.asList(name, signature, accessFlags, Arrays.hashCode(attributes)).hashCode();
    }

    @Override
    public String toString() {
        return "DefaultConstructor{" +
                "name=\"" + name + "\", " +
                "signature=\"" + signature + "\", " +
                "accessFlags=\"" + accessFlags + "\", " +
                "attributes=" + Arrays.asList(attributes) + "}";
    }
}
