package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilerConfiguration;
import io.recode.decompile.DecompilerConfigurationBuilder;
import io.recode.model.Expression;
import io.recode.util.Iterators;
import io.recode.util.SingleThreadedStack;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static io.recode.model.AST.*;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BinaryOperationsTest {

    private final BinaryOperations delegation = new BinaryOperations();
    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);
    private final SingleThreadedStack<Expression> stack = new SingleThreadedStack<>();

    @Before
    public void setup() {
        when(decompilationContext.getStack()).thenReturn(stack);
    }

    @Test
    public void configureShouldNotAcceptNullArg() {
        assertThrown(() -> delegation.configure(null), AssertionError.class);
    }

    @Test
    public void supportForArithmeticIntegerOperatorsShouldBeConfigured() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.iadd));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.isub));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.imul));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.idiv));
    }

    @Test
    public void supportForAllArithmeticFloatOperatorsShouldBeConfigured() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.fadd));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.fsub));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.fmul));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.fdiv));
    }

    @Test
    public void supportForAllArithmeticDoubleOperatorsShouldBeConfigured() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dadd));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dsub));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dmul));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.ddiv));
    }

    @Test
    public void supportForAllArithmeticLongOperatorsShouldBeConfigured() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.ladd));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.lsub));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.lmul));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.ldiv));
    }

    @Test
    public void iaddShouldPushAdditionOfStackedOperands() throws IOException {
        stack.push(constant(1));
        stack.push(constant(2));

        execute(ByteCode.iadd);

        assertEquals(Arrays.asList(add(constant(1), constant(2), int.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void isubShouldPushSubtractionOfOperands() throws IOException {
        stack.push(constant(1));
        stack.push(constant(2));

        execute(ByteCode.isub);

        assertEquals(Arrays.asList(sub(constant(1), constant(2), int.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void imulShouldPushMultiplicationOfOperands() throws IOException {
        stack.push(constant(1));
        stack.push(constant(2));

        execute(ByteCode.imul);

        assertEquals(Arrays.asList(mul(constant(1), constant(2), int.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void idivShouldPushDivisionOfOperands() throws IOException {
        stack.push(constant(1));
        stack.push(constant(2));

        execute(ByteCode.idiv);

        assertEquals(Arrays.asList(div(constant(1), constant(2), int.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void faddShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1f));
        stack.push(constant(2f));

        execute(ByteCode.fadd);

        assertEquals(Arrays.asList(add(constant(1f), constant(2f), float.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void fsubShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1f));
        stack.push(constant(2f));

        execute(ByteCode.fsub);

        assertEquals(Arrays.asList(sub(constant(1f), constant(2f), float.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void fmulShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1f));
        stack.push(constant(2f));

        execute(ByteCode.fmul);

        assertEquals(Arrays.asList(mul(constant(1f), constant(2f), float.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void fdivShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1f));
        stack.push(constant(2f));

        execute(ByteCode.fdiv);

        assertEquals(Arrays.asList(div(constant(1f), constant(2f), float.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void daddShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1d));
        stack.push(constant(2d));

        execute(ByteCode.dadd);

        assertEquals(Arrays.asList(add(constant(1d), constant(2d), double.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void dsubShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1d));
        stack.push(constant(2d));

        execute(ByteCode.dsub);

        assertEquals(Arrays.asList(sub(constant(1d), constant(2d), double.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void dmulShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1d));
        stack.push(constant(2d));

        execute(ByteCode.dmul);

        assertEquals(Arrays.asList(mul(constant(1d), constant(2d), double.class)), Iterators.toList(stack.iterator() ));
    }

    @Test
    public void ddivShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1d));
        stack.push(constant(2d));

        execute(ByteCode.ddiv);

        assertEquals(Arrays.asList(div(constant(1d), constant(2d), double.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void laddShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1l));
        stack.push(constant(2l));

        execute(ByteCode.ladd);

        assertEquals(Arrays.asList(add(constant(1l), constant(2l), long.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void lsubShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1l));
        stack.push(constant(2l));

        execute(ByteCode.lsub);

        assertEquals(Arrays.asList(sub(constant(1l), constant(2l), long.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void lmulShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1l));
        stack.push(constant(2l));

        execute(ByteCode.lmul);

        assertEquals(Arrays.asList(mul(constant(1l), constant(2l), long.class)), Iterators.toList(stack.iterator()));
    }

    @Test
    public void ldivShouldPushSubtractionOfFloats() throws IOException {
        stack.push(constant(1l));
        stack.push(constant(2l));

        execute(ByteCode.ldiv);

        assertEquals(Arrays.asList(div(constant(1l), constant(2l), long.class)), Iterators.toList(stack.iterator()));
    }

    private void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, byteCode)
                .apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();
        delegation.configure(builder);
        return builder.build();
    }
}