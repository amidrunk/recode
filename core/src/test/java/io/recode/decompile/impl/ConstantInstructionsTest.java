package io.recode.decompile.impl;

import io.recode.classfile.*;
import io.recode.decompile.CodeStream;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilerConfiguration;
import io.recode.decompile.DecompilerConfigurationBuilder;
import io.recode.model.AST;
import io.recode.model.Constant;
import io.recode.model.impl.BasicModelFactory;
import io.recode.model.impl.ConstantImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ConstantInstructionsTest {

    private final ConstantInstructions delegation = new ConstantInstructions();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final Method method = mock(Method.class);
    private final ClassFile classFile = mock(ClassFile.class);
    private final ConstantPool constantPool = mock(ConstantPool.class);
    private final CodeStream codeStream = mock(CodeStream.class);

    @Before
    public void setup() {
        when(decompilationContext.getMethod()).thenReturn(method);
        when(decompilationContext.getModelFactory()).thenReturn(new BasicModelFactory());
        when(method.getClassFile()).thenReturn(classFile);
        when(classFile.getConstantPool()).thenReturn(constantPool);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> delegation.configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForLoadConstantInstructionsFromConstantPool() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.ldc));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.ldcw));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.ldc2w));
    }

    @Test
    public void configureShouldConfigureSupportForPushingNullConstant() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.aconst_null));
    }

    @Test
    public void configureShouldConfigureSupportForStaticDoubleConstants() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dconst_0));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dconst_1));
    }

    @Test
    public void configureShouldConfigureSupportForStaticFloatConstants() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.fconst_0));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.fconst_1));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.fconst_2));
    }

    @Test
    public void configureShouldConfigureSupportForIntegerConstants() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iconst_m1));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iconst_0));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iconst_1));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iconst_2));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iconst_3));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iconst_4));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iconst_5));
    }

    @Test
    public void configureShouldConfigureSupportForLongConstants() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.lconst_0));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.lconst_1));
    }

    @Test
    public void configureShouldConfigureSupportForConstantPushInstructions() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.bipush));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.sipush));
    }

    @Test
    public void bipushShouldPushSignExtendedByte() throws IOException {
        when(codeStream.nextByte()).thenReturn(100);
        execute(ByteCode.bipush);
        verify(decompilationContext).push(eq(new ConstantImpl(100, int.class)));
    }

    @Test
    public void sipushShouldPushSignExtendedShort() throws IOException {
        when(codeStream.nextSignedShort()).thenReturn(1234);
        execute(ByteCode.sipush);
        verify(decompilationContext).push(eq(new ConstantImpl(1234, int.class)));
    }

    @Test
    public void lconstInstructionsShouldPushLong() throws IOException {
        execute(ByteCode.lconst_0);
        execute(ByteCode.lconst_1);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(0L, long.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(1L, int.class)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void iconstInstructionsShouldPushInteger() throws IOException {
        execute(ByteCode.iconst_m1);
        execute(ByteCode.iconst_0);
        execute(ByteCode.iconst_1);
        execute(ByteCode.iconst_2);
        execute(ByteCode.iconst_3);
        execute(ByteCode.iconst_4);
        execute(ByteCode.iconst_5);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(-1, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(0, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(1, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(2, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(3, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(4, int.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(5, int.class)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void fconstInstructionsShouldPushFloat() throws IOException {
        execute(ByteCode.fconst_0);
        execute(ByteCode.fconst_1);
        execute(ByteCode.fconst_2);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(0f, float.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(1f, float.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(2f, float.class)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void dconstInstructionsShouldPushDouble() throws IOException {
        execute(ByteCode.dconst_0);
        execute(ByteCode.dconst_1);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(0d, double.class)));
        inOrder.verify(decompilationContext).push(eq(new ConstantImpl(1d, double.class)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void aconst_nullShouldPushNull() throws IOException {
        execute(ByteCode.aconst_null);

        verify(decompilationContext).push(new ConstantImpl(null, Object.class));
    }

    @Test
    public void ldcCanPushConstantIntegerFromConstantPool() throws Exception {
        verifyLDC(99, new ConstantPoolEntry.IntegerEntry(1234), AST.constant(1234));
    }

    @Test
    public void ldcCanPushConstantFloatFromConstantPool() throws Exception {
        verifyLDC(99, new ConstantPoolEntry.FloatEntry(1234f), AST.constant(1234f));
    }

    @Test
    public void ldcCanPushConstantStringFromConstantPool() throws Exception {
        when(constantPool.getString(eq(101))).thenReturn("foo");
        verifyLDC(99, new ConstantPoolEntry.StringEntry(101), AST.constant("foo"));
    }

    @Test
    public void ldcCanPushConstantClassFromConstantPool() throws Exception {
        when(constantPool.getString(eq(101))).thenReturn("java/lang/String");
        when(decompilationContext.resolveType(eq("java/lang/String"))).thenReturn(String.class);
        verifyLDC(99, new ConstantPoolEntry.ClassEntry(101), AST.constant(String.class));
    }

    @Test
    public void ldcShouldFailForInvalidConstantPoolEntry() throws Exception {
        assertThrown(() -> verifyLDC(99, new ConstantPoolEntry.DoubleEntry(1234d), AST.constant(1234d)), ClassFileFormatException.class);
    }

    @Test
    public void ldcwCanPushConstantIntegerFromConstantPool() throws Exception {
        verifyLDCW(1, 99, new ConstantPoolEntry.IntegerEntry(1234), AST.constant(1234));
    }

    @Test
    public void ldcwCanPushConstantFloatFromConstantPool() throws Exception {
        verifyLDCW(1, 99, new ConstantPoolEntry.FloatEntry(1234f), AST.constant(1234f));
    }

    @Test
    public void ldcwCanPushConstantStringFromConstantPool() throws Exception {
        when(constantPool.getString(eq(101))).thenReturn("foo");
        verifyLDCW(1, 99, new ConstantPoolEntry.StringEntry(101), AST.constant("foo"));
    }

    @Test
    public void ldcwCanPushConstantClassFromConstantPool() throws Exception {
        when(constantPool.getString(eq(101))).thenReturn("java/lang/String");
        when(decompilationContext.resolveType(eq("java/lang/String"))).thenReturn(String.class);
        verifyLDCW(1, 99, new ConstantPoolEntry.ClassEntry(101), AST.constant(String.class));
    }

    @Test
    public void ldcwShouldFailForInvalidConstantPoolEntry() throws Exception {
        assertThrown(() -> verifyLDCW(1, 99, new ConstantPoolEntry.DoubleEntry(1234d), AST.constant(1234d)), ClassFileFormatException.class);
    }

    @Test
    public void ldc2wCanPushDoubleFromConstantPool() throws Exception {
        verifyLDC2W(1, 100, new ConstantPoolEntry.DoubleEntry(1234d), AST.constant(1234d));
    }

    @Test
    public void ldc2wCanPushLongFromConstantPool() throws Exception {
        verifyLDC2W(1, 100, new ConstantPoolEntry.LongEntry(1234L), AST.constant(1234L));
    }

    @Test
    public void ldc2wShouldFailForInvalidConstantPoolEntry() throws Exception {
        assertThrown(() -> verifyLDC2W(1, 99, new ConstantPoolEntry.IntegerEntry(1234), AST.constant(1234d)), ClassFileFormatException.class);
    }

    private void verifyLDC(int index, ConstantPoolEntry entry, Constant constant) throws IOException {
        when(constantPool.getEntry(eq(index))).thenReturn(entry);

        configuration().getDecompilerDelegate(decompilationContext, ByteCode.ldc).apply(decompilationContext, CodeStreamTestUtils.codeStream(index), ByteCode.ldc);

        verify(decompilationContext).push(constant);
    }

    private void verifyLDCW(int indexh, int indexl, ConstantPoolEntry entry, Constant constant) throws IOException {
        when(constantPool.getEntry(eq(indexh << 8 | indexl))).thenReturn(entry);

        configuration().getDecompilerDelegate(decompilationContext, ByteCode.ldcw).apply(decompilationContext, CodeStreamTestUtils.codeStream(indexh, indexl), ByteCode.ldcw);

        verify(decompilationContext).push(constant);
    }

    private void verifyLDC2W(int indexh, int indexl, ConstantPoolEntry entry, Constant constant) throws IOException {
        when(constantPool.getEntry(eq(indexh << 8 | indexl))).thenReturn(entry);

        configuration().getDecompilerDelegate(decompilationContext, ByteCode.ldc2w).apply(decompilationContext, CodeStreamTestUtils.codeStream(indexh, indexl), ByteCode.ldc2w);

        verify(decompilationContext).push(constant);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();

        delegation.configure(builder);

        return builder.build();
    }

    private void execute(int instruction) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, instruction).apply(decompilationContext, codeStream, instruction);
    }
}