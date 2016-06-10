package io.recode.decompile.impl;

import io.recode.TypeResolver;
import io.recode.classfile.ByteCode;
import io.recode.classfile.LocalVariable;
import io.recode.classfile.Method;
import io.recode.decompile.*;
import io.recode.model.AST;
import io.recode.model.Affix;
import io.recode.model.Increment;
import io.recode.model.LocalVariableReference;
import io.recode.model.impl.IncrementImpl;
import io.recode.model.impl.LocalVariableReferenceImpl;
import io.recode.util.Iterators;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import static io.recode.model.AST.*;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnaryOperationsTest {

    private final UnaryOperations unaryOperations = new UnaryOperations();
    private final Method method = mock(Method.class);
    private final DecompilationContext decompilationContext = new DecompilationContextImpl(mock(Decompiler.class), method, mock(ProgramCounter.class), mock(LineNumberCounter.class), mock(TypeResolver.class), 0);
    private final CodeStream codeStream = mock(CodeStream.class);
    private final LocalVariable localVariable = mock(LocalVariable.class);

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> unaryOperations.configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForUnaryOperators() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iinc));
    }

    @Test
    public void iincShouldIncreaseValueOfVariable() throws Exception {
        when(method.getLocalVariableForIndex(eq(1))).thenReturn(localVariable);
        when(localVariable.getName()).thenReturn("foo");
        when(localVariable.getType()).thenReturn(int.class);

        execute(ByteCode.iinc, 1, 2);

        assertEquals(Arrays.asList(new IncrementImpl(new LocalVariableReferenceImpl("foo", int.class, 1), AST.constant(2), int.class, Affix.UNDEFINED)),
                decompilationContext.getStackedExpressions());
    }

    @Test
    public void postfixIncrementShouldBeCorrectedAfterIncrement() throws IOException {
        final LocalVariableReference local = AST.local("foo", int.class, 1);
        final Increment originalIncrement = new IncrementImpl(local, AST.constant(1), int.class, Affix.UNDEFINED);

        decompilationContext.push(local);
        decompilationContext.push(originalIncrement);

        configuration().getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iinc)
                .next()
                .apply(decompilationContext, codeStream, ByteCode.iinc);

        assertEquals(Arrays.asList(new IncrementImpl(local, AST.constant(1), int.class, Affix.POSTFIX)), decompilationContext.getStackedExpressions());
    }

    @Test
    public void prefixIncrementShouldBeCorrectedAfterIncrementAndLoad() throws IOException {
        final LocalVariableReference local = AST.local("foo", int.class, 1);
        final Increment originalIncrement = new IncrementImpl(local, AST.constant(1), int.class, Affix.UNDEFINED);

        decompilationContext.push(originalIncrement);
        decompilationContext.push(local);

        configuration().getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iload)
                .next()
                .apply(decompilationContext, codeStream, ByteCode.iinc);

        assertEquals(Arrays.asList(new IncrementImpl(local, AST.constant(1), int.class,  Affix.PREFIX)), decompilationContext.getStackedExpressions());
    }

    @Test
    public void correctionalPrefixIncrementEnhancementShouldNotApplyIfLoadIsNotPrecededByIncrementOfVariable() throws IOException {
        final LocalVariableReference local = AST.local("foo", int.class, 1);
        final Increment originalIncrement = new IncrementImpl(AST.local("bar", int.class, 2), AST.constant(1), int.class, Affix.UNDEFINED);

        decompilationContext.push(originalIncrement);
        decompilationContext.push(local);

        assertFalse(configuration().getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iload).hasNext());
        assertEquals(Arrays.asList(originalIncrement, local), decompilationContext.getStackedExpressions());
    }

    @Test
    public void correctionalPrefixIncrementEnhancementShouldIgnoreQualifiedIncrement() throws IOException {
        final LocalVariableReference local = AST.local("foo", int.class, 1);
        final Increment originalIncrement = new IncrementImpl(local, AST.constant(1), int.class, Affix.POSTFIX);

        decompilationContext.push(originalIncrement);
        decompilationContext.push(local);

        assertFalse(configuration().getCorrectionalDecompilerEnhancements(decompilationContext, ByteCode.iload).hasNext());
        assertEquals(Arrays.asList(originalIncrement, local), decompilationContext.getStackedExpressions());
    }

    @Test
    public void prefixByteIncrementShouldBeCorrectedAfterLoad() throws IOException {
        final LocalVariableReference local = local("myVar", byte.class, 1);

        decompilationContext.enlist(set(local).to(cast(sub(local, constant(1), int.class)).to(byte.class)));
        decompilationContext.push(local);

        after(ByteCode.iload_1);

        assertEquals(Arrays.asList(new IncrementImpl(local, constant(-1), byte.class, Affix.PREFIX)), decompilationContext.getStackedExpressions());
    }

    @Test
    public void postfixByteIncrementShouldBeCorrectedAfterLoad() throws Exception {
        final LocalVariableReference local = local("myVar", byte.class, 1);

        decompilationContext.push(local);
        decompilationContext.enlist(set(local).to(cast(add(local, constant(1), int.class)).to(byte.class)));

        after(ByteCode.istore_1);

        assertEquals(Arrays.asList(new IncrementImpl(local, constant(1), byte.class, Affix.POSTFIX)), decompilationContext.getStackedExpressions());
    }

    @Test
    public void prefixFloatDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", float.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1f), float.class)));
        decompilationContext.push(sub(local, constant(1f), float.class));

        after(ByteCode.fstore_0);

        assertEquals(Arrays.asList(new IncrementImpl(local, constant(-1f), float.class, Affix.PREFIX)), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void postfixFloatDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", float.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1f), float.class)));
        decompilationContext.push(local);

        after(ByteCode.fstore_0);

        assertEquals(Arrays.asList(new IncrementImpl(local, constant(-1f), float.class, Affix.POSTFIX)), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void prefixDoubleDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", double.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1d), double.class)));
        decompilationContext.push(sub(local, constant(1d), double.class));

        after(ByteCode.dstore_0);

        assertEquals(Arrays.asList(new IncrementImpl(local, constant(-1d), double.class, Affix.PREFIX)), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void postfixDoubleDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", double.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1d), double.class)));
        decompilationContext.push(local);

        after(ByteCode.dstore_0);

        assertEquals(Arrays.asList(new IncrementImpl(local, constant(-1d), double.class, Affix.POSTFIX)), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void prefixLongDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", long.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1l), long.class)));
        decompilationContext.push(sub(local, constant(1l), long.class));

        after(ByteCode.lstore_0);

        assertEquals(Arrays.asList(new IncrementImpl(local, constant(-1l), long.class, Affix.PREFIX)), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void postfixLongDecrementShouldBeCorrectedAfterStore() throws IOException {
        final LocalVariableReference local = local("foo", long.class, 1);

        decompilationContext.enlist(set(local).to(sub(local, constant(1l), long.class)));
        decompilationContext.push(local);

        after(ByteCode.lstore_0);

        assertEquals(Arrays.asList(new IncrementImpl(local, constant(-1l), long.class, Affix.POSTFIX)), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    private void after(int byteCode) throws IOException {
        final Iterator<DecompilerDelegate> iterator = configuration().getCorrectionalDecompilerEnhancements(decompilationContext, byteCode);

        while (iterator.hasNext()) {
            iterator.next().apply(decompilationContext, codeStream, byteCode);
        }
    }

    private void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, byteCode).apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();
        unaryOperations.configure(builder);
        return builder.build();
    }

}