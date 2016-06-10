package io.recode.classfile.impl;

import io.recode.classfile.LocalVariable;
import io.recode.classfile.LocalVariableTable;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LocalVariableTableImplTest {

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new LocalVariableTableImpl(null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() throws IOException {
        final LocalVariable localVariable = Mockito.mock(LocalVariable.class);
        final LocalVariableTableImpl localVariableTable = new LocalVariableTableImpl(new LocalVariable[]{localVariable});

        assertArrayEquals(new Object[]{localVariable}, localVariableTable.getLocalVariables().toArray());
        assertEquals(LocalVariableTable.ATTRIBUTE_NAME, localVariableTable.getName());
    }
}
