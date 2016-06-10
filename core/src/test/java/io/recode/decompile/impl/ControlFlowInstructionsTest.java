package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilerConfiguration;
import io.recode.decompile.DecompilerConfigurationBuilder;
import io.recode.decompile.ProgramCounter;
import io.recode.model.Expression;
import io.recode.model.LocalVariableReference;
import io.recode.model.impl.GotoImpl;
import io.recode.model.impl.ReturnImpl;
import io.recode.util.SingleThreadedStack;
import io.recode.util.Stack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;

import static io.recode.model.AST.*;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ControlFlowInstructionsTest {

    private final ControlFlowInstructions controlFlowInstructions = new ControlFlowInstructions();

    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);

    private final Stack<Expression> stack = new SingleThreadedStack<>();

    @Before
    public void setup() {
        when(decompilationContext.getStack()).thenReturn(stack);
    }

    @Test
    public void configureShouldNotAcceptNullConfiguration() {
        assertThrown(() -> controlFlowInstructions.configure(null), AssertionError.class);
    }

    @Test
    public void configurationShouldSupportReturnInstructions() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.return_));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.ireturn));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.lreturn));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.freturn));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.dreturn));
        assertNotNull(it.getDecompilerDelegate(decompilationContext, ByteCode.areturn));
    }

    @Test
    public void returnShouldReduceStackAndEnlistReturn() throws IOException {
        execute(ByteCode.return_);

        final InOrder inOrder = Mockito.inOrder(decompilationContext);

        inOrder.verify(decompilationContext).reduceAll();
        inOrder.verify(decompilationContext).enlist(eq(new ReturnImpl()));
    }

    @Test
    public void ireturnShouldEnlistReturnOfInteger() throws IOException {
        stack.push(constant(1));

        execute(ByteCode.ireturn);

        verify(decompilationContext).enlist(eq($return(constant(1))));
    }

    @Test
    public void lreturnShouldEnlistReturnOfLong() throws IOException {
        stack.push(constant(1L));

        execute(ByteCode.lreturn);

        verify(decompilationContext).enlist(eq($return(constant(1L))));
    }

    @Test
    public void freturnShouldEnlistReturnOfLong() throws IOException {
        stack.push(constant(1f));

        execute(ByteCode.freturn);

        verify(decompilationContext).enlist(eq($return(constant(1f))));
    }

    @Test
    public void dreturnShouldEnlistReturnOfLong() throws IOException {
        stack.push(constant(1d));

        execute(ByteCode.dreturn);

        verify(decompilationContext).enlist(eq($return(constant(1d))));
    }

    @Test
    public void ireturnShouldFailForInvalidReturnType() throws IOException {
        stack.push(constant(1d));

        assertThrown(() -> execute(ByteCode.ireturn), ClassFileFormatException.class);
    }

    @Test
    public void areturnShouldEnlistReturnOfLong() throws IOException {
        final LocalVariableReference local = local("foo", String.class, 1);

        stack.push(local);

        execute(ByteCode.areturn);

        verify(decompilationContext).enlist(eq($return(local)));
    }

    @Test
    public void areturnShouldFailForInvalidReturnType() {
        stack.push(constant(1));

        assertThrown(() -> execute(ByteCode.areturn), ClassFileFormatException.class);
    }

    @Test
    public void gotoShouldEnlistGotoElement() throws IOException {
        final ProgramCounter pc = mock(ProgramCounter.class);

        when(pc.get()).thenReturn(100);
        when(decompilationContext.getProgramCounter()).thenReturn(pc);

        execute(ByteCode.goto_, 0, 10);

        verify(decompilationContext).enlist(eq(new GotoImpl(110)));
    }

    private void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, byteCode)
                .apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder configurationBuilder = DecompilerConfigurationImpl.newBuilder();
        controlFlowInstructions.configure(configurationBuilder);
        return configurationBuilder.build();
    }

}