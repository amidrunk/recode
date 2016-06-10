package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.decompile.*;
import io.recode.model.AST;
import io.recode.model.Constant;
import io.recode.model.Expression;
import io.recode.model.LocalVariableReference;
import io.recode.model.impl.ArrayLoadImpl;
import io.recode.model.impl.ArrayStoreImpl;
import io.recode.model.impl.FieldReferenceImpl;
import io.recode.model.impl.NewArrayImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ArrayInstructionsTest {

    private final DecompilationContext context = mock(DecompilationContext.class);

    private final CodeStream code = mock(CodeStream.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> new ArrayInstructions().configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForArrayInstructions() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(context, ByteCode.anewarray));
        assertNotNull(it.getDecompilerDelegate(context, ByteCode.aaload));
        assertNotNull(it.getDecompilerDelegate(context, ByteCode.aastore));
        assertNotNull(it.getDecompilerDelegate(context, ByteCode.arraylength));
        assertNotNull(it.getDecompilerDelegate(context, ByteCode.iaload));
    }

    @Test
    public void objectArrayStoreShouldEnlistNewArrayStoreOnReferencedArray() throws IOException {
        final Expression value = mock(Expression.class, "value");
        final Expression index = mock(Expression.class, "index");
        final Expression array = mock(Expression.class, "array");

        when(context.pop()).thenReturn(value, index, array);

        ArrayInstructions.aastore().apply(context, code, ByteCode.aastore);

        verify(context).enlist(eq(new ArrayStoreImpl(array, index, value)));
    }

    @Test
    public void aaloadShouldPushObjectArrayAccessOntoStack() throws IOException {
        final Expression index = AST.constant(1);
        final Expression array = AST.local("myArray", String[].class, 1);

        when(context.pop()).thenReturn(index, array);

        ArrayInstructions.aaload().apply(context, code, ByteCode.aaload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, String.class)));
    }

    @Test
    public void newarrayShouldFailIfTypeCodeIsInvalid() throws IOException {
        when(code.nextUnsignedByte()).thenReturn(123);

        assertThrown(() -> ArrayInstructions.newarray().apply(context, code, ByteCode.newarray), ClassFileFormatException.class);
    }

    @Test
    public void newarrayCanPushBooleanArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(4);

        ArrayInstructions.newarray().apply(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(boolean[].class, boolean.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushCharArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(5);

        ArrayInstructions.newarray().apply(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(char[].class, char.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushFloatArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(6);

        ArrayInstructions.newarray().apply(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(float[].class, float.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushDoubleArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(7);

        ArrayInstructions.newarray().apply(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(double[].class, double.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushByteArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(8);

        ArrayInstructions.newarray().apply(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(byte[].class, byte.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushShortArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(9);

        ArrayInstructions.newarray().apply(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(short[].class, short.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushIntArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(10);

        ArrayInstructions.newarray().apply(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(int[].class, int.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void newarrayCanPushLongArrayOntoStack() throws Exception {
        when(context.pop()).thenReturn(AST.constant(1));
        when(code.nextUnsignedByte()).thenReturn(11);

        ArrayInstructions.newarray().apply(context, code, ByteCode.newarray);

        verify(context).push(eq(new NewArrayImpl(long[].class, long.class, AST.constant(1), Collections.emptyList())));
    }

    @Test
    public void iastoreShouldStoreIntoArrayElement() throws IOException {
        final Constant value = AST.constant(1);
        final Constant index = AST.constant(2);
        final LocalVariableReference array = AST.local("myArray", int[].class, 1);

        when(context.pop()).thenReturn(value, index, array);

        ArrayInstructions.iastore().apply(context, code, ByteCode.iastore);

        verify(context).enlist(eq(new ArrayStoreImpl(array, index, value)));
    }

    @Test
    public void arraylengthShouldPushFieldReferenceOntoStack() throws Exception {
        final LocalVariableReference array = AST.local("foo", String[].class, 1);

        when(context.pop()).thenReturn(array);

        ArrayInstructions.arraylength().apply(context, code, ByteCode.arraylength);

        verify(context).push(new FieldReferenceImpl(array, String[].class, int.class, "length"));
    }

    @Test
    public void integerArrayElementCanBeRetrieved() throws IOException {
        final DecompilerDelegate iaload = ArrayInstructions.iaload();

        when(context.pop()).thenReturn(
                AST.constant(1),
                AST.local("myArray", int[].class, 1));

        iaload.apply(context, mock(CodeStream.class), ByteCode.iaload);

        verify(context).push(eq(new ArrayLoadImpl(AST.local("myArray", int[].class, 1), AST.constant(1), int.class)));
    }

    @Test
    public void laloadShouldLoadElementFromArray() throws IOException {
        final DecompilerDelegate extension = ArrayInstructions.laload();

        final Expression index = mock(Expression.class);
        final Expression array = mock(LocalVariableReference.class);

        when(context.pop()).thenReturn(index, array);

        extension.apply(context, code, ByteCode.laload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, long.class)));
    }

    @Test
    public void faloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(LocalVariableReference.class);

        when(context.pop()).thenReturn(index, array);

        ArrayInstructions.faload().apply(context, code, ByteCode.faload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, float.class)));
    }

    @Test
    public void daloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(Expression.class);

        when(context.pop()).thenReturn(index, array);

        ArrayInstructions.daload().apply(context, code, ByteCode.daload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, double.class)));
    }

    @Test
    public void baloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(Expression.class);

        when(context.pop()).thenReturn(index, array);

        ArrayInstructions.baload().apply(context, code, ByteCode.baload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, boolean.class)));
    }

    @Test
    public void caloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(Expression.class);

        when(context.pop()).thenReturn(index, array);

        ArrayInstructions.caload().apply(context, code, ByteCode.caload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, char.class)));
    }

    @Test
    public void saloadShouldLoadElementFromArray() throws IOException {
        final Expression index = mock(Expression.class);
        final Expression array = mock(Expression.class);

        when(context.pop()).thenReturn(index, array);

        ArrayInstructions.saload().apply(context, code, ByteCode.saload);

        verify(context).push(eq(new ArrayLoadImpl(array, index, short.class)));
    }

    @Test
    public void arraylengthShouldFailIfStackedInstanceIsNotArrayType() throws IOException {
        when(context.pop()).thenReturn(AST.constant(1));

        assertThrown(() -> ArrayInstructions.arraylength().apply(context, code, ByteCode.arraylength), ClassFileFormatException.class);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder configurationBuilder = DecompilerConfigurationImpl.newBuilder();

        new ArrayInstructions().configure(configurationBuilder);

        return configurationBuilder.build();
    }
}
