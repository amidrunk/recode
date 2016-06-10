package io.recode.classfile.impl;

import io.recode.classfile.*;
import io.recode.util.Range;
import io.recode.model.Signature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

public final class DefaultMethod implements Method {

    public static final int LAMBDA_MODIFIERS = 0x00001000 | Modifier.PRIVATE;

    private final Supplier<ClassFile> classFile;

    private final int accessFlags;

    private final String name;

    private final Signature signature;

    private final Attribute[] attributes;

    public DefaultMethod(Supplier<ClassFile> classFile, int accessFlags, String name, Signature signature, Attribute[] attributes) {
        assert classFile != null : "Class file can't be null";
        assert name != null : "name can't be null";
        assert signature != null : "signature can't be null";
        assert attributes != null : "attributes can't be null";

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
    public Method withLocalVariableTable(LocalVariableTable localVariableTable) {
        assert localVariableTable != null : "Local variable table can't be null";

        final Attribute[] newAttributes = Arrays.stream(attributes).map(a -> {
            if (!CodeAttribute.ATTRIBUTE_NAME.equals(a.getName())) {
                return a;
            }

            return ((CodeAttribute) a).withLocalVariableTable(localVariableTable);
        }).toArray(Attribute[]::new);

        return new DefaultMethod(classFile, accessFlags, name, signature, newAttributes);
    }

    @Override
    public boolean hasCodeForLineNumber(int lineNumber) {
        return getRequiredLineNumberTable().getEntries().stream()
                .filter(e -> e.getLineNumber() == lineNumber)
                .findAny()
                .isPresent();
    }

    @Override
    public boolean isLambdaBackingMethod() {
        if ((accessFlags & LAMBDA_MODIFIERS) != LAMBDA_MODIFIERS) {
            return false;
        }

        if (!getName().startsWith("lambda$")) {
            return false;
        }

        return true;
    }

    @Override
    public List<Attribute> getAttributes() {
        return Arrays.asList(attributes);
    }

    @Override
    public CodeAttribute getCode() {
        final Optional<Attribute> optionalCodeAttribute = Arrays.asList(attributes).stream()
                .filter(a -> a.getName().equals(CodeAttribute.ATTRIBUTE_NAME))
                .findFirst();

        return (CodeAttribute) optionalCodeAttribute
                .orElseThrow(() -> new IllegalStateException("Code attribute is not present for method '" + getName() + "'"));
    }

    @Override
    public Range getCodeRangeForLineNumber(int lineNumber) {
        final LineNumberTable lineNumberTable = getRequiredLineNumberTable();
        final LineNumberTableEntry[] entriesForLineNumber = collectEntriesForLineNumber(lineNumberTable, Arrays.asList(lineNumber));
        final LineNumberTableEntry startEntry = entriesForLineNumber[0];

        final Optional<LineNumberTableEntry> endEntry = lineNumberTable.getEntries().stream()
                .filter(e -> e.getStartPC() > entriesForLineNumber[entriesForLineNumber.length - 1].getStartPC())
                .findFirst();

        return new Range(startEntry.getStartPC(), endEntry.isPresent() ? endEntry.get().getStartPC() - 1 : getCode().getCodeLength());
    }

    @Override
    @Deprecated
    public InputStream getCodeForLineNumber(int lineNumber) {
        final Range codeRangeForLineNumber = getCodeRangeForLineNumber(lineNumber);

        try (InputStream inputStream = getCode().getCode()) {
            final int numberOfByteCodes = codeRangeForLineNumber.getTo() - codeRangeForLineNumber.getFrom() + 1;

            inputStream.skip(codeRangeForLineNumber.getFrom());

            if (inputStream.available() == numberOfByteCodes) {
                return inputStream;
            }

            final byte[] buffer = new byte[numberOfByteCodes];

            inputStream.read(buffer);

            return new ByteArrayInputStream(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<LineNumberTable> getLineNumberTable() {
        return (Optional) getCode().getAttributes().stream()
                    .filter(a -> a.getName().equals(LineNumberTable.ATTRIBUTE_NAME))
                    .findFirst();
    }

    protected LineNumberTable getRequiredLineNumberTable() {
        return getLineNumberTable()
                .orElseThrow(() -> new IllegalStateException("Line numbers are not present for method '" + getName() + "'"));
    }

    @Override
    public LocalVariable getLocalVariableForIndex(int index) {
        assert index >= 0 : "Index must be positive";

        final Optional<Attribute> attribute = getCode().getAttributes().stream()
                .filter(a -> a.getName().equals(LocalVariableTable.ATTRIBUTE_NAME))
                .findFirst();

        if (!attribute.isPresent()) {
            throw new IllegalStateException("Local variable table is not present in method '" + getName() + "'");
        }

        final LocalVariableTable localVariableTable = (LocalVariableTable) attribute.get();

        final Optional<LocalVariable> localVariable = localVariableTable.getLocalVariables().stream()
                .filter(v -> v.getIndex() == index)
                .findFirst();

        if (!localVariable.isPresent()) {
            throw new LocalVariableNotAvailableException("No local variable exists for index " + index + " in method " + getClassFile().getName() + "." + getName());
        }

        return localVariable.get();
    }

    @Override
    public Optional<LocalVariableTable> getLocalVariableTable() {
        return getCode().getAttributes().stream()
                .filter(a -> a.getName().equals(LocalVariableTable.ATTRIBUTE_NAME))
                .map(a -> (LocalVariableTable) a)
                .findFirst();
    }

    private LineNumberTableEntry[] collectEntriesForLineNumber(LineNumberTable lineNumberTable, List<Integer> lineNumbers) {
        final LineNumberTableEntry[] entriesForLineNumber = lineNumberTable.getEntries().stream()
                .filter(e -> lineNumbers.contains(e.getLineNumber()))
                .toArray(LineNumberTableEntry[]::new);

        if (entriesForLineNumber.length == 0) {
            throw new IllegalStateException("No code exists at " + lineNumbers + " " + getClassFile().getName() + "::" + getName());
        }

        final LineNumberTableEntry[] allEntries = lineNumberTable.getEntries().stream()
                .filter(e -> e.getStartPC() >= entriesForLineNumber[0].getStartPC() && e.getStartPC() <= entriesForLineNumber[entriesForLineNumber.length - 1].getStartPC())
                .toArray(LineNumberTableEntry[]::new);

        if (allEntries.length > entriesForLineNumber.length) {
            final Integer[] newLineNumbers = Arrays.stream(allEntries).map(LineNumberTableEntry::getLineNumber)
                    .distinct()
                    .toArray(Integer[]::new);

            return collectEntriesForLineNumber(lineNumberTable, Arrays.asList(newLineNumbers));
        }

        return allEntries;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultMethod)) {
            return false;
        }

        final DefaultMethod other = (DefaultMethod) obj;

        return other.accessFlags == accessFlags
                && other.name.equals(name)
                && other.signature.equals(signature)
                && Arrays.equals(other.attributes, attributes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{
                accessFlags,
                name.hashCode(),
                signature.hashCode(),
                Arrays.hashCode(attributes)
        });
    }

    @Override
    public String toString() {
        return "DefaultMethod{" +
                "accessFlags=" + accessFlags + ", " +
                "name=\"" + name + "\", " +
                "signature=\"" + signature + "\", " +
                "attributes=" + Arrays.asList(attributes) +
                "}";
    }
}
