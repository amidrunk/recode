package io.recode.classfile.impl;

import io.recode.classfile.*;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CodeAttributeImplTest {

    private final ByteBuffer buffer = mock(ByteBuffer.class);

    private final List<ExceptionTableEntry> emptyExceptionTable = Collections.<ExceptionTableEntry>emptyList();

    private final List<Attribute> emptyAttributes = Collections.<Attribute>emptyList();

    @Before
    public void setup() {
        when(buffer.asReadOnlyBuffer()).thenReturn(buffer);
    }

    @Test
    public void constructorShouldNotAcceptNegativeMaxStack() {
        assertThrown(() -> new CodeAttributeImpl(-1, 1, buffer, emptyExceptionTable, emptyAttributes), AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNegativeMaxLocals() {
        assertThrown(() -> new CodeAttributeImpl(1, -1, buffer, emptyExceptionTable, emptyAttributes), AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullByteCode() {
        assertThrown(() -> new CodeAttributeImpl(1, 1, null, emptyExceptionTable, emptyAttributes), AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullExceptionTable() {
        assertThrown(() -> new CodeAttributeImpl(1, 2, buffer, null, emptyAttributes), AssertionError.class);
    }

    @Test
    public void constructorShouldNotAcceptNullAttributes() {
        assertThrown(() -> new CodeAttributeImpl(1, 2, buffer, emptyExceptionTable, null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainProvidedParameters() throws IOException {
        final byte[] byteCode = {2, 3, 4};
        final ExceptionTableEntry exceptionTableEntry = mock(ExceptionTableEntry.class);
        final Attribute codeAttribute = mock(Attribute.class);
        final CodeAttributeImpl attribute = new CodeAttributeImpl(1, 2, ByteBuffer.wrap(byteCode), Arrays.asList(exceptionTableEntry), Arrays.asList(codeAttribute));

        assertEquals(1, attribute.getMaxStack());
        assertEquals(2, attribute.getMaxLocals());
        assertArrayEquals(byteCode, IOUtils.toByteArray(attribute.getCode()));
        assertArrayEquals(new Object[]{exceptionTableEntry}, attribute.getExceptionTable().toArray());
        assertArrayEquals(new Object[]{codeAttribute}, attribute.getAttributes().toArray());
    }

    @Test
    public void withLocalAttributeTableShouldNotAcceptNullTable() throws Exception {
        final CodeAttributeImpl attribute = new CodeAttributeImpl(0, 0, buffer, emptyExceptionTable, emptyAttributes);

        assertThrown(() -> attribute.withLocalVariableTable(null), AssertionError.class);
    }

    @Test
    public void withLocalAttributeTableShouldAddAttributeIfNotExists() throws Exception {
        final Attribute otherAttribute = mock(Attribute.class);
        final LocalVariableTable newTable = mock(LocalVariableTable.class);

        final CodeAttribute newAttribute = new CodeAttributeImpl(0, 0, buffer, emptyExceptionTable, Arrays.asList(otherAttribute))
                .withLocalVariableTable(newTable);

        assertArrayEquals(new Object[]{otherAttribute, newTable}, newAttribute.getAttributes().toArray());
    }

    @Test
    public void withLocalAttributeTableShouldReplaceExistingAttributeTable() throws Exception {
        final Attribute otherAttribute = mock(Attribute.class);
        final LocalVariableTable oldTable = new LocalVariableTableImpl(new LocalVariable[0]);
        final LocalVariableTable newTable = mock(LocalVariableTable.class);
        final CodeAttribute attr = new CodeAttributeImpl(0, 0, buffer, emptyExceptionTable, Arrays.asList(oldTable, otherAttribute))
                .withLocalVariableTable(newTable);

        assertArrayEquals(new Object[]{otherAttribute, newTable}, attr.getAttributes().toArray());
    }

}
