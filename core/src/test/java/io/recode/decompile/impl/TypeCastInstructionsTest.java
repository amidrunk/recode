package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFile;
import io.recode.classfile.ConstantPool;
import io.recode.classfile.Method;
import io.recode.decompile.*;
import io.recode.model.AST;
import io.recode.model.TypeCast;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static io.recode.model.AST.constant;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TypeCastInstructionsTest {

    private final Method exampleMethod = mock(Method.class);

    private final ClassFile exampleClassFile = mock(ClassFile.class);

    private final ConstantPool constantPool = mock(ConstantPool.class);

    private final DecompilationContext exampleContext = mock(DecompilationContext.class);

    @Before
    public void setup() {
        when(exampleContext.getMethod()).thenReturn(exampleMethod);
        when(exampleMethod.getClassFile()).thenReturn(exampleClassFile);
        when(exampleClassFile.getConstantPool()).thenReturn(constantPool);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> new CastInstructions().configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForCheckCast() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.checkcast));
    }

    @Test
    public void checkcastExtensionShouldPushCastOntoStack() throws IOException {
        final DecompilerDelegate checkcast = CastInstructions.checkcast();

        when(constantPool.getClassName(eq(1))).thenReturn("java/lang/String");
        when(exampleContext.resolveType(eq("java/lang/String"))).thenReturn(String.class);
        when(exampleContext.pop()).thenReturn(constant("foo"));

        checkcast.apply(exampleContext, CodeStreamTestUtils.codeStream(0, 1), ByteCode.checkcast);

        verify(exampleContext).push(eq(AST.cast(constant("foo")).to(String.class)));
    }

    @Test
    public void discardImplicitCastShouldPushTargetExpressionOntoStack() throws IOException {
        final DecompilerDelegate discardImplicitCast = CastInstructions.discardImplicitCast();
        final TypeCast typeCast = AST.cast(AST.constant("foo")).to(String.class);

        when(exampleContext.peek()).thenReturn(typeCast);
        when(exampleContext.pop()).thenReturn(typeCast);

        discardImplicitCast.apply(exampleContext, CodeStreamTestUtils.codeStream(), ByteCode.pop);

        verify(exampleContext).push(eq(AST.constant("foo")));
    }

    @Test
    public void supportForIntToByteInstructionShouldBeConfigured() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(exampleContext, ByteCode.i2b));
    }

    @Test
    public void int2byteShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2b);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(byte.class)));
    }

    @Test
    public void int2charShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2c);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(char.class)));
    }

    @Test
    public void int2shortShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2s);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(short.class)));
    }

    @Test
    public void int2longShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2l);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(long.class)));
    }

    @Test
    public void int2floatShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2f);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(float.class)));
    }

    @Test
    public void int2doubleShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678));

        execute(ByteCode.i2d);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678)).to(double.class)));
    }

    @Test
    public void long2intShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678L));

        execute(ByteCode.l2i);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678L)).to(int.class)));
    }

    @Test
    public void long2floatShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678L));

        execute(ByteCode.l2f);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678L)).to(float.class)));
    }

    @Test
    public void long2doubleShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(0x12345678L));

        execute(ByteCode.l2d);

        verify(exampleContext).push(eq(AST.cast(constant(0x12345678L)).to(double.class)));
    }

    @Test
    public void float2intShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(1234f));

        execute(ByteCode.f2i);

        verify(exampleContext).push(eq(AST.cast(constant(1234f)).to(int.class)));
    }

    @Test
    public void float2longShouldPushPrimitiveCastOntoStack() throws IOException {
        when(exampleContext.pop()).thenReturn(AST.constant(1234f));

        execute(ByteCode.f2l);

        verify(exampleContext).push(eq(AST.cast(constant(1234f)).to(long.class)));
    }

    private void execute(int byteCode) throws IOException {
        configuration().getDecompilerDelegate(exampleContext, byteCode).apply(exampleContext, mock(CodeStream.class), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder configurationBuilder = DecompilerConfigurationImpl.newBuilder();

        new CastInstructions().configure(configurationBuilder);

        return configurationBuilder.build();
    }

}
