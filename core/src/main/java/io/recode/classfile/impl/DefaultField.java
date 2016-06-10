package io.recode.classfile.impl;

import io.recode.classfile.Attribute;
import io.recode.classfile.ClassFile;
import io.recode.classfile.Field;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class DefaultField implements Field {

    private final Supplier<ClassFile> classFile;

    private final int accessFlags;

    private final String name;

    private final Type type;

    private final Attribute[] attributes;

    public DefaultField(Supplier<ClassFile> classFile, int accessFlags, String name, Type type, Attribute[] attributes) {
        assert classFile != null : "Class file can't be null";
        assert name != null : "name can't be null";
        assert type != null : "Type can't be null";
        assert attributes != null : "attributes can't be null";

        this.classFile = classFile;
        this.accessFlags = accessFlags;
        this.name = name;
        this.type = type;
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
    public Type getType() {
        return type;
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

        if (!(obj instanceof DefaultField)) {
            return false;
        }

        final DefaultField other = (DefaultField) obj;

        return accessFlags == other.accessFlags
                && name.equals(other.name)
                && type.equals(other.type)
                && Arrays.equals(attributes, other.attributes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{
                accessFlags,
                name.hashCode(),
                type.hashCode(),
                Arrays.hashCode(attributes)
        });
    }

    @Override
    public String toString() {
        return "DefaultField{"
                + "accessFlags=" + accessFlags + ", "
                + "name=\"" + name + "\", "
                + "type=\"" + type + "\", "
                + "attributes=" + Arrays.asList(attributes)
                + "}";
    }
}
