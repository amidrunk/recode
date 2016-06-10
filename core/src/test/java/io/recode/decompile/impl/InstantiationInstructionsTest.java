package io.recode.decompile.impl;

import io.recode.Caller;
import io.recode.ClassModelTestUtils;
import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFile;
import io.recode.classfile.ConstantPoolEntry;
import io.recode.classfile.Method;
import io.recode.classfile.impl.DefaultConstantPool;
import io.recode.decompile.*;
import io.recode.model.Element;
import io.recode.model.ElementType;
import io.recode.model.NewInstance;
import io.recode.model.VariableAssignment;
import io.recode.model.impl.ConstantImpl;
import io.recode.model.impl.InstanceAllocationImpl;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class InstantiationInstructionsTest {

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> new InstantiationInstructions().configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForNewByteCode() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.new_));
    }

    @Test
    public void newInstanceShouldPushInstanceAllocationOntoStack() throws IOException {
        final DecompilationContext context = mock(DecompilationContext.class);
        final InputStreamCodeStream codeStream = new InputStreamCodeStream(new ByteArrayInputStream(new byte[]{(byte) 0, (byte) 1, (byte) ByteCode.dup}), mock(ProgramCounter.class));

        final Method method = mock(Method.class);
        final ClassFile classFile = mock(ClassFile.class);

        when(classFile.getConstantPool()).thenReturn(new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.ClassEntry(2))
                .addEntry(new ConstantPoolEntry.UTF8Entry("java/lang/String")).create());

        when(method.getClassFile()).thenReturn(classFile);
        when(context.getMethod()).thenReturn(method);
        when(context.resolveType(eq("java/lang/String"))).thenReturn(String.class);

        InstantiationInstructions.newInstance().apply(context, codeStream, ByteCode.new_);

        verify(context).push(eq(new InstanceAllocationImpl(String.class)));
        verify(context).resolveType(eq("java/lang/String"));
    }

    @Test
    public void newByteCodeShouldBeSupportedInByteCode() {
        final String str = new String("str");

        final CodePointer[] codePointers = ClassModelTestUtils.code(Caller.adjacent(-2));
        assertEquals(1, codePointers.length);

        final Element element = codePointers[0].getElement();
        assertEquals(ElementType.VARIABLE_ASSIGNMENT, element.getElementType());

        final VariableAssignment variableAssignment = (VariableAssignment) element;
        assertEquals("str", variableAssignment.getVariableName());
        assertEquals(ElementType.NEW, variableAssignment.getValue().getElementType());

        final NewInstance newInstance = (NewInstance) variableAssignment.getValue();

        assertEquals(String.class, newInstance.getType());
        assertArrayEquals(new Object[]{new ConstantImpl("str", String.class)}, newInstance.getParameters().toArray());
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder configurationBuilder = DecompilerConfigurationImpl.newBuilder();

        new InstantiationInstructions().configure(configurationBuilder);

        return configurationBuilder.build();
    }
}
