package io.recode.decompile.impl;

import io.recode.TypeResolver;
import io.recode.classfile.ByteCode;
import io.recode.classfile.Method;
import io.recode.decompile.*;
import io.recode.model.*;
import io.recode.model.impl.BranchImpl;
import io.recode.model.impl.CompareImpl;
import io.recode.util.Iterators;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import static io.recode.model.AST.constant;
import static io.recode.model.AST.local;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BooleanOperationsTest {

    private final BooleanOperations booleanOperations = new BooleanOperations();

    private final ProgramCounter pc = mock(ProgramCounter.class);

    private final DecompilationContext decompilationContext = new DecompilationContextImpl(mock(Decompiler.class), mock(Method.class), pc, mock(LineNumberCounter.class), mock(TypeResolver.class), 0);

    @Before
    public void setup() {
        when(pc.get()).thenReturn(1234);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> booleanOperations.configure(null), AssertionError.class);
    }

    @Test
    public void integerConstantAsBooleanShouldBeCoercedToBoolean() throws IOException {
        final MethodCall originalCall = AST.call(Object.class, "setBoolean", MethodSignature.parse("(Z)V"), AST.constant(1));

        decompilationContext.getStack().push(originalCall);

        after(ByteCode.invokeinterface);

        final MethodCall expectedCall = originalCall.withParameters(Arrays.<Expression>asList(AST.constant(true)));

        assertEquals(Arrays.asList(expectedCall), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void integerConstantAssignedToBooleanShouldBeCoercedToBoolean() throws IOException {
        decompilationContext.enlist(AST.set(local("foo", boolean.class, 1)).to(constant(1)));

        after(ByteCode.istore_1);

        assertEquals(Arrays.asList(
                AST.set(local("foo", boolean.class, 1)).to(constant(true))
        ), decompilationContext.getStatements().all().get());
    }

    @Test
    public void ifneShouldPushIntNeComparisonToStack() throws IOException {
        decompilationContext.push(constant(1));

        execute(ByteCode.ifne, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1), OperatorType.NE, constant(0), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void ifeqShouldPushIntNeComparisonToStack() throws IOException {
        decompilationContext.push(constant(1));

        execute(ByteCode.ifeq, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1), OperatorType.EQ, constant(0), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void ifltShouldPushIntNeComparisonToStack() throws IOException {
        decompilationContext.push(constant(1));

        execute(ByteCode.iflt, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1), OperatorType.LT, constant(0), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void ifgeShouldPushIntNeComparisonToStack() throws IOException {
        decompilationContext.push(constant(1));

        execute(ByteCode.ifge, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1), OperatorType.GE, constant(0), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void ifgtShouldPushIntNeComparisonToStack() throws IOException {
        decompilationContext.push(constant(1));

        execute(ByteCode.ifgt, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1), OperatorType.GT, constant(0), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void ifleShouldPushIntNeComparisonToStack() throws IOException {
        decompilationContext.push(constant(1));

        execute(ByteCode.ifle, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1), OperatorType.LE, constant(0), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void if_icmpneShouldPushIntComparisonToStack() throws IOException {
        decompilationContext.push(constant(1234));
        decompilationContext.push(constant(2345));

        execute(ByteCode.if_icmpne, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1234), OperatorType.NE, constant(2345), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void if_cmpeqShouldPushIntComparisonToStack() throws IOException {
        decompilationContext.push(constant(1234));
        decompilationContext.push(constant(2345));

        execute(ByteCode.if_icmpeq, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1234), OperatorType.EQ, constant(2345), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void if_icmpgeShouldPushIntComparisonToStack() throws IOException {
        decompilationContext.push(constant(1234));
        decompilationContext.push(constant(2345));

        execute(ByteCode.if_icmpge, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1234), OperatorType.GE, constant(2345), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void if_icmpleShouldPushIntComparisonToStack() throws IOException {
        decompilationContext.push(constant(1234));
        decompilationContext.push(constant(2345));

        execute(ByteCode.if_icmple, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1234), OperatorType.LE, constant(2345), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void if_icmpltShouldPushIntComparisonToStack() throws IOException {
        decompilationContext.push(constant(1234));
        decompilationContext.push(constant(2345));

        execute(ByteCode.if_icmplt, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant(1234), OperatorType.LT, constant(2345), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void if_acmpeqShouldPushObjectComparisonToStack() throws IOException {
        decompilationContext.push(constant("foo"));
        decompilationContext.push(constant("bar"));

        execute(ByteCode.if_acmpeq, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant("foo"), OperatorType.EQ, constant("bar"), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void if_acmpneShouldPushObjectComparisonToStack() throws IOException {
        decompilationContext.push(constant("foo"));
        decompilationContext.push(constant("bar"));

        execute(ByteCode.if_acmpne, 0, 100);

        assertEquals(Arrays.asList(new BranchImpl(constant("foo"), OperatorType.NE, constant("bar"), 1334)), decompilationContext.getStatements().all().get());
    }

    @Test
    public void lcmpShouldPushCompareOntoStack() throws IOException {
        decompilationContext.push(constant(1L));
        decompilationContext.push(constant(2L));

        execute(ByteCode.lcmp);

        assertEquals(Arrays.asList(new CompareImpl(constant(1L), constant(2L))), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void fcmplShouldPushCompareOntoStack() throws IOException {
        decompilationContext.push(constant(1f));
        decompilationContext.push(constant(2f));

        execute(ByteCode.fcmpl);

        assertEquals(Arrays.asList(new CompareImpl(constant(1f), constant(2f))), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void fcmpgShouldPushCompareOntoStack() throws IOException {
        decompilationContext.push(constant(1f));
        decompilationContext.push(constant(2f));

        execute(ByteCode.fcmpg);

        assertEquals(Arrays.asList(new CompareImpl(constant(1f), constant(2f))), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void dcmplShouldPushCompareOntoStack() throws IOException {
        decompilationContext.push(constant(1d));
        decompilationContext.push(constant(2d));

        execute(ByteCode.dcmpl);

        assertEquals(Arrays.asList(new CompareImpl(constant(1d), constant(2d))), Iterators.toList(decompilationContext.getStack().iterator()));
    }

    @Test
    public void dcmpgShouldPushCompareOntoStack() throws IOException {
        decompilationContext.push(constant(1d));
        decompilationContext.push(constant(2d));

        execute(ByteCode.dcmpg);

        assertEquals(Arrays.asList(new CompareImpl(constant(1d), constant(2d))), Iterators.toList(decompilationContext.getStack().iterator()));
    }


    private void after(int byteCode, int ... code) throws IOException {
        final Iterator<DecompilerDelegate> delegates = configuration().getCorrectionalDecompilerEnhancements(decompilationContext, byteCode);

        while (delegates.hasNext()) {
            delegates.next().apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
        }
    }

    public void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(decompilationContext, byteCode)
                .apply(decompilationContext, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder configurationBuilder = DecompilerConfigurationImpl.newBuilder();
        booleanOperations.configure(configurationBuilder);
        return configurationBuilder.build();
    }

}