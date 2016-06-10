package io.recode.classfile.impl;

import io.recode.util.io.ByteBufferInputStream;
import io.recode.classfile.Attribute;
import io.recode.classfile.CodeAttribute;
import io.recode.classfile.ExceptionTableEntry;
import io.recode.classfile.LocalVariableTable;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CodeAttributeImpl implements CodeAttribute {

    private final int maxStack;

    private final int maxLocals;

    private final ByteBuffer byteCode;

    private final List<ExceptionTableEntry> exceptionTable;

    private final List<Attribute> attributes;

    public CodeAttributeImpl(int maxStack, int maxLocals, ByteBuffer byteCode,
                             List<ExceptionTableEntry> exceptionTable, List<Attribute> attributes) {
        assert maxStack >= 0 : "Max-stack must be positive";
        assert maxLocals >= 0 : "Max-locals must be positive";
        assert byteCode != null : "Byte code can't be null";
        assert exceptionTable != null : "Exception table can't be null";
        assert attributes != null : "Attributes can't be null";

        this.maxStack = maxStack;
        this.maxLocals = maxLocals;
        this.byteCode = byteCode.asReadOnlyBuffer();
        this.exceptionTable = exceptionTable;
        this.attributes = attributes;
    }

    @Override
    public int getMaxStack() {
        return maxStack;
    }

    @Override
    public int getMaxLocals() {
        return maxLocals;
    }

    @Override
    public InputStream getCode() {
        return new ByteBufferInputStream(byteCode.asReadOnlyBuffer());
    }

    @Override
    public int getCodeLength() {
        return byteCode.remaining();
    }

    @Override
    public List<ExceptionTableEntry> getExceptionTable() {
        return Collections.unmodifiableList(exceptionTable);
    }

    @Override
    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    @Override
    public CodeAttribute withLocalVariableTable(LocalVariableTable localVariableTable) {
        assert localVariableTable != null : "Local variable table can't be null";

        final Attribute[] newAttributes = getAttributes().stream()
                .filter(a -> !LocalVariableTable.ATTRIBUTE_NAME.equals(a.getName()))
                .toArray(n -> new Attribute[n + 1]);

        newAttributes[newAttributes.length - 1] = localVariableTable;

        return new CodeAttributeImpl(maxStack, maxLocals, byteCode, exceptionTable, Arrays.asList(newAttributes));
    }
}
