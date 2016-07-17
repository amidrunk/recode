package io.recode.decompile.impl;

import io.recode.Caller;
import io.recode.ClassModelTestUtils;
import io.recode.classfile.*;
import io.recode.classfile.impl.*;
import io.recode.decompile.*;
import io.recode.decompile.DecompilationHistoryCallback.DecompilerState;
import io.recode.model.*;
import io.recode.model.impl.*;
import io.recode.util.Methods;
import io.recode.util.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static io.recode.Caller.adjacent;
import static io.recode.model.AST.*;
import static io.recode.model.AST.eq;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

public class DecompilerImplTest {

    private final Method exampleMethod = mock(Method.class);

    private final ClassFile exampleClassFile = mock(ClassFile.class);

    @Before
    public void setup() {
        when(exampleMethod.getClassFile()).thenReturn(exampleClassFile);
        when(exampleMethod.getLineNumberTable()).thenReturn(Optional.<LineNumberTable>empty());
    }

    @Test
    public void callbackShouldBeNotifiedBeforeInstructionIsExecuted() throws Exception {
        final Decompiler decompiler = decompilerWithDelegate(ByteCode.nop, mock(DecompilerDelegate.class));
        final Method method = methodWithLineNumberTable();
        final DecompilationProgressCallback callback = mock(DecompilationProgressCallback.class);

        decompiler.parse(method, CodeStreamTestUtils.codeStream(ByteCode.nop), callback);

        final InOrder inOrder = inOrder(callback);

        inOrder.verify(callback).beforeInstruction(any(), eq(ByteCode.nop));
        inOrder.verify(callback).afterInstruction(any(), eq(ByteCode.nop));
    }

    @Test
    public void elementShouldGetComplementedWithMetaDataWhenPushedToStack() throws IOException {
        final DecompilerDelegate delegate = mock(DecompilerDelegate.class);
        final Decompiler decompiler = decompilerWithDelegate(ByteCode.nop, delegate);
        final Method method = methodWithLineNumberTable();

        doAnswer(pushExpression(call(String.class, "valueOf", String.class))).when(delegate).apply(any(), any(), eq(ByteCode.nop));

        final Element[] elements = decompiler.parse(method, CodeStreamTestUtils.codeStream(ByteCode.nop));

        assertEquals(1, elements.length);
        assertEquals(ElementType.METHOD_CALL, elements[0].getElementType());
        assertEquals(0, elements[0].getMetaData().getProgramCounter());
        assertEquals(10, elements[0].getMetaData().getLineNumber());
    }

    @Test
    public void statementShouldGetComplementedWithMetaDataWhenEnlisted() throws IOException {
        final DecompilerDelegate delegate = mock(DecompilerDelegate.class);
        final Decompiler decompiler = decompilerWithDelegate(ByteCode.nop, delegate);
        final Method method = methodWithLineNumberTable();

        doAnswer(enlistStatement(call(String.class, "valueOf", String.class))).when(delegate).apply(any(), any(), eq(ByteCode.nop));

        final Element[] elements = decompiler.parse(method, CodeStreamTestUtils.codeStream(ByteCode.nop));

        assertEquals(1, elements.length);
        assertEquals(ElementType.METHOD_CALL, elements[0].getElementType());
        assertEquals(0, elements[0].getMetaData().getProgramCounter());
        assertEquals(10, elements[0].getMetaData().getLineNumber());
    }

    private Method methodWithLineNumberTable() {
        final Method method = mock(Method.class);

        when(method.getLineNumberTable()).thenReturn(Optional.of(new LineNumberTableImpl(new LineNumberTableEntry[]{
                new LineNumberTableEntryImpl(0, 10)
        }, Range.from(0).to(100))));
        return method;
    }

    private Decompiler decompilerWithDelegate(int instruction, DecompilerDelegate delegate) {
        return new DecompilerImpl(DecompilerConfigurationImpl.newBuilder()
                .on(instruction).then(delegate)
                .build());
    }

    private Answer enlistStatement(Statement statement) {
        return a -> {
            ((DecompilationContext) a.getArguments()[0]).getStatements().add(statement);
            return null;
        };
    }

    private Answer pushExpression(Expression expression) {
        return a -> {
            ((DecompilationContext) a.getArguments()[0]).getStack().push(expression);
            return null;
        };
    }

    @Test
    public void constructorShouldNotAcceptNullDecompilerConfiguration() {
        assertThrown(() -> new DecompilerImpl(null), AssertionError.class);
    }

    @Test
    public void advisoryDecompilerEnhancementShouldBeCalledBeforeInstructionIsProcessed() throws IOException {
        final DecompilerDelegate enhancement = mock(DecompilerDelegate.class);
        final DecompilerDelegate extension = mock(DecompilerDelegate.class);

        final DecompilerConfiguration configuration = DecompilerConfigurationImpl.newBuilder()
                .before(ByteCode.nop).then(enhancement)
                .on(ByteCode.nop).then(extension)
                .build();

        final DecompilerImpl decompiler = new DecompilerImpl(configuration);

        decompiler.parse(exampleMethod, CodeStreamTestUtils.codeStream(ByteCode.nop));

        final InOrder inOrder = Mockito.inOrder(extension, enhancement);

        inOrder.verify(enhancement).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.nop));
        inOrder.verify(extension).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.nop));
    }

    @Test
    public void correctionalDecompilerEnhancementShouldBeCalledAfterInstructionIsProcessed() throws IOException {
        final DecompilerDelegate extension = mock(DecompilerDelegate.class, "delegate");
        final DecompilerDelegate enhancement = mock(DecompilerDelegate.class, "enhancement");
        final DecompilerConfiguration configuration = DecompilerConfigurationImpl.newBuilder()
                .on(ByteCode.nop).then(extension)
                .after(ByteCode.nop).then(enhancement)
                .build();

        new DecompilerImpl(configuration).parse(exampleMethod, CodeStreamTestUtils.codeStream(ByteCode.nop));

        final InOrder inOrder = Mockito.inOrder(extension, enhancement);

        inOrder.verify(extension).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.nop));
        inOrder.verify(enhancement).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.nop));
    }

    @Test
    public void configuredDecompilerEnhancementShouldBeCalledAfterInstruction() throws IOException {
        final DecompilerDelegate enhancement = mock(DecompilerDelegate.class, "enhancement");
        final DecompilerConfiguration configuration = DecompilerConfigurationImpl.newBuilder()
                .after(ByteCode.istore_1).then(enhancement)
                .build();

        final DecompilerImpl decompiler = new DecompilerImpl(configuration.merge(CoreDecompilerDelegation.configuration()));

        when(exampleMethod.getLocalVariableTable()).thenReturn(Optional.of(new LocalVariableTableImpl(new LocalVariable[]{
                new LocalVariableImpl(-1, -1, "test", String.class, 1)
        })));

        decompiler.parse(exampleMethod, new InputStreamCodeStream(new ByteArrayInputStream(new byte[]{(byte) ByteCode.iconst_0, ByteCode.istore_1})));

        verify(enhancement).apply(any(DecompilationContext.class), any(CodeStream.class), eq(ByteCode.istore_1));
    }

    @Test
    public void compilerExtensionCanOverrideByteCodeHandling() throws IOException {
        final DecompilerDelegate extension = mock(DecompilerDelegate.class);
        final DecompilerConfiguration configuration = DecompilerConfigurationImpl.newBuilder()
                .on(ByteCode.iconst_0).then(extension)
                .build();
        final DecompilerImpl decompiler = new DecompilerImpl(configuration);

        final Element[] elements = decompiler.parse(exampleMethod, new InputStreamCodeStream(new ByteArrayInputStream(new byte[]{(byte) ByteCode.iconst_0})));

        assertEquals(0, elements.length);

        verify(extension).apply(any(DecompilationContext.class), any(CodeStream.class), anyInt());
    }

    @Test
    public void decompilationProgressCallbackShouldBeNotifiedOfProgress() throws IOException {
        int n = 100;

        final Caller caller = adjacent(-2);
        final DecompilationHistoryCallback callback = new DecompilationHistoryCallback();

        decompileCallerWithCallback(caller, callback);

        Assert.assertArrayEquals(new DecompilerState[]{
                new DecompilerState(Arrays.asList(AST.constant(100)), Collections.emptyList()),
                new DecompilerState(Collections.emptyList(), Arrays.asList(AST.set(1, "n", int.class, AST.constant(100))))
        }, callback.getDecompilerStates());
    }

    @Test
    public void decompilationCanBeAborted() throws IOException {
        int n = 100; int m = 200;

        final Caller caller = adjacent(-2);
        final DecompilationProgressCallback callback = mock(DecompilationProgressCallback.class);
        final DecompilationHistoryCallback history = new DecompilationHistoryCallback();

        doAnswer(i -> {
            final DecompilationContext context = (DecompilationContext) i.getArguments()[0];

            if (context.getStackedExpressions().isEmpty()) {
                context.abort();
            }

            return null;
        }).when(callback).afterInstruction(any(), any());

        decompileCallerWithCallback(caller, new CompositeDecompilationProgressCallback(new DecompilationProgressCallback[]{callback, history}));

        Assert.assertArrayEquals(new DecompilerState[]{
                new DecompilerState(Arrays.asList(AST.constant(100)), Collections.emptyList()),
                new DecompilerState(Collections.emptyList(), Arrays.asList(AST.set(1, "n", int.class, AST.constant(100))))
        }, history.getDecompilerStates());
    }

    @Test
    public void emptyMethodCanBeParsed() {
        assertArrayEquals(new Element[]{$return()}, parseMethodBody("emptyMethod"));
    }

    @Test
    public void methodWithReturnStatementCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithIntegerReturn");

        assertArrayEquals(new Element[]{
                $return(constant(1234))
        }, elements);
    }

    @Test
    public void methodWithReturnFromOtherMethod() {
        final Element[] elements = parseMethodBody("exampleMethodWithReturnFromOtherMethod");

        assertArrayEquals(new Element[]{
                $return(plus(constant(1), call(local("this", ExampleClass.class, 0), "methodWithIntegerReturn", int.class)))
        }, elements);
    }

    @Test
    public void methodWithReturnFromOtherMethodWithParameters() {
        final Element[] elements = parseMethodBody("exampleMethodWithMethodCallWithParameters");

        assertArrayEquals(new Element[]{
                $return(call(local("this", ExampleClass.class, 0), "add", int.class, constant(1), constant(2)))
        }, elements);
    }

    @Test
    public void methodWithReturnOfLocalCanBeParsed() {
        final Element[] elements = parseMethodBody("returnLocal");

        final Element[] expectedElements = {
                set(1, "n", constant(100)),
                $return(local("n", int.class, 1))
        };

        assertArrayEquals(expectedElements, elements);
    }

    private <T> ExpectContinuation<T> expect(T instance) {
        return value -> {
        };
    }

    private interface ExpectContinuation<T> {

        void toBe(T value);
    }

    @Test
    public void expectationsCanBeParsed() {
        expect("foo").toBe("foo");
        final CodePointer codePointer = ClassModelTestUtils.code(adjacent(-1))[0];

        final MethodCall toBe = codePointer.getElement().as(MethodCall.class);
        assertEquals("toBe", toBe.getMethodName());
        assertEquals(Arrays.asList(AST.constant("foo")), toBe.getParameters());

        final MethodCall expect = toBe.getTargetInstance().as(MethodCall.class);
        assertEquals("expect", expect.getMethodName());
        assertEquals(Arrays.asList(AST.constant("foo")), expect.getParameters());
    }

    @Test
    public void methodWithReferencesToConstantsInConstantPoolCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithConstantPoolReferences");

        assertArrayEquals(new Element[]{
                set(1, "n", constant(123456789)),
                set(2, "f", constant(123456789f)),
                set(3, "str", constant("foobar")),
                $return()
        }, elements);
    }

    @Test
    public void methodWithFieldAccessCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithFieldAccess");

        final Element[] expectedElements = {
                call(field(local("this", ExampleClass.class, 0), String.class, "string"), "toString", String.class),
                $return()
        };

        expect(elements).toBe(expectedElements);
    }

    @Test
    public void methodWithLambdaDeclarationAndInvocationCanBeParsed() throws Exception {
        final Element[] elements = parseMethodBody("methodWithLambdaDeclarationAndInvocation");

        expect(elements.length).toBe(3);

        final VariableAssignment assignment = (VariableAssignment) elements[0];

        assertEquals("s", assignment.getVariableName());
        assertEquals(Supplier.class, assignment.getVariableType());
        assertTrue(assignment.getValue() instanceof Lambda);

        final Lambda lambda = (Lambda) assignment.getValue();

        assertEquals(Supplier.class, lambda.getFunctionalInterface());
        assertEquals("get", lambda.getFunctionalMethodName());
        assertEquals(MethodSignature.parse("()Ljava/lang/Object;"), lambda.getInterfaceMethodSignature());
        assertEquals(MethodSignature.parse("()Ljava/lang/String;"), lambda.getBackingMethodSignature());
        assertEquals(ExampleClass.class, lambda.getDeclaringClass());
        assertNotNull(ExampleClass.class.getDeclaredMethod(lambda.getBackingMethodName()));
        assertEquals(Supplier.class, lambda.getType());

        assertEquals(call(local("s", Supplier.class, 1), "get", Object.class), elements[1]);
    }

    @Test
    public void methodWithStaticFieldReferenceCanBeParsed() {
        final Element[] actualElements = parseMethodBody("methodWithStaticFieldReference");
        final Element[] expectedElements = {
                set(1, "b", field(BigDecimal.class, BigDecimal.class, "ZERO")),
                $return()
        };

        assertArrayEquals(expectedElements, actualElements);
    }

    @Test
    public void methodWithLongConstantsCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithLongConstants");

        assertArrayEquals(new Element[]{
                set(1, "l1", constant(0L)),
                set(3, "l2", constant(1L)),
                $return()
        }, elements);
    }

    @Test
    public void methodWithByteConstantsCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithByteConstants");

        assertArrayEquals(new Element[]{
                set(1, "b1", byte.class, constant(0)),
                set(2, "b2", byte.class, constant(1)),
                set(3, "b3", byte.class, constant(2)),
                $return()
        }, elements);
    }

    @Test
    public void methodWithEqComparisonCanBeParsed() {
        final Element[] elements = parseMethodBody("methodWithEqComparison");

        assertArrayEquals(new Element[]{
                set(1, "b1", eq(call(constant("str"), "length", int.class), constant(3))),
                $return()
        }, elements);
    }

    @Test
    public void constantsOfAllTypesCanBeParsed() {
        final Element[] elements = parseMethodBody("constantsOfAllTypes");

        assertArrayEquals(new Element[]{
                set(1, "z", boolean.class, constant(true)),
                set(2, "b", byte.class, constant(100)),
                set(3, "s", short.class, constant(200)),
                set(4, "c", char.class, constant(300)),
                set(5, "n", int.class, constant(400)),
                set(6, "l", long.class, constant(500L)),
                set(8, "f", float.class, constant(600.1234f)),
                set(9, "d", double.class, constant(700.1234d)),
                $return()
        }, elements);
    }

    @Test
    public void lambdaWithMethodCallThatDiscardsResultCanBeParsed() throws IOException {
        final DefaultConstantPool constantPool = new DefaultConstantPool.Builder().create();

        assertThrown(() -> constantPool.getInterfaceMethodRefDescriptor(1), IndexOutOfBoundsException.class);

        final CodePointer[] codePointers = ClassModelTestUtils.code(adjacent(-2));

        assertEquals(1, codePointers.length);

        final MethodCall expect = codePointers[0].getElement().as(MethodCall.class);
        assertEquals("assertThrown", expect.getMethodName());
        assertEquals(2, expect.getParameters().size());

        final Lambda lambda = expect.getParameters().get(0).as(Lambda.class);
        final Method backingMethod = Lambdas.getBackingMethod(codePointers[0].forElement(lambda));

        assertArrayEquals(new Element[]{
                AST.call(AST.local("constantPool", DefaultConstantPool.class, 0), "getInterfaceMethodRefDescriptor", InterfaceMethodRefDescriptor.class, AST.constant(1)),
                AST.$return()
        }, new DecompilerImpl().decompile(backingMethod));
    }

    @Test
    public void newStatementCanBeDecompiled() {
        new String("Hello World!");

        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        assertArrayEquals(new Element[]{newInstance(String.class, constant("Hello World!"))}, elements);
    }

    @Test
    public void newStatementWithAssignmentCanBeDecompiled() {
        final String str = new String("Hello World!");

        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        assertArrayEquals(new Element[]{set(1, "str", newInstance(String.class, constant("Hello World!")))}, elements);
    }

    @Test
    public void newArrayWithAssignmentCanBeDecompiled() {
        final String[] array = {"Hello!"};

        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        assertArrayEquals(new Element[]{
                new VariableAssignmentImpl(
                        new NewArrayImpl(String[].class, String.class, constant(1),
                                Arrays.asList(new ArrayInitializerImpl(0, constant("Hello!")))),
                        1, "array", String[].class)
        }, elements);
    }

    @Test
    public void arrayStoreCanBeDecompiled() {
        final String[] array = new String[1];

        array[0] = "Hello World!";

        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-2)))
                .map(CodePointer::getElement)
                .toArray(Element[]::new);

        assertArrayEquals(new Element[]{
                new ArrayStoreImpl(local("array", String[].class, 1), constant(0), constant("Hello World!"))
        }, elements);
    }

    private String str = "astring";

    @Test
    public void fieldAssignmentCanBeDecompiled() {
        this.str = "newvalue";

        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-2)))
                .map(CodePointer::getElement)
                .toArray(Element[]::new);

        assertArrayEquals(new Element[]{
                new FieldAssignmentImpl(
                        new FieldReferenceImpl(local("this", getClass(), 0), getClass(), String.class, "str"),
                        constant("newvalue"))
        }, elements);
    }

    @Test
    public void staticFieldReferenceCanBeDecompiled() {
        final PrintStream out = System.out;

        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        assertArrayEquals(new Element[]{
                new VariableAssignmentImpl(
                        new FieldReferenceImpl(null, System.class, PrintStream.class, "out"),
                        1, "out", PrintStream.class)
        }, elements);
    }

    @Test
    public void staticFieldAssignmentCanBeDecompiled() {
        ExampleClass.STATIC_STRING = "bar";

        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        assertArrayEquals(new Element[]{
                new FieldAssignmentImpl(
                        new FieldReferenceImpl(null, ExampleClass.class, String.class, "STATIC_STRING"),
                        constant("bar"))
        }, elements);
    }

    @Test
    public void typeCastCanBeDecompiled() {
        final Object object = "foo";
        final String string = (String) object;

        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-2))).map(CodePointer::getElement).toArray(Element[]::new);

        assertArrayEquals(new Element[]{
                AST.set(2, "string", cast(local("object", Object.class, 1)).to(String.class))
        }, elements);
    }

    @Test
    public void lineNumbersShouldBeRetained() {
        final String string = "str";

        final Caller caller = adjacent(-2);
        final Element[] elements = Arrays.stream(ClassModelTestUtils.code(adjacent(-3))).map(CodePointer::getElement).toArray(Element[]::new);

        final VariableAssignment variableAssignments = (VariableAssignment) elements[0];
        assertEquals(caller.getLineNumber(), variableAssignments.getMetaData().getLineNumber());

        final Constant value = (Constant) variableAssignments.getValue();
        assertEquals(caller.getLineNumber(), value.getMetaData().getLineNumber());
    }

    @Test
    public void intArrayStoreCanBeDecompiled() {
        int[] array = new int[2];
        array[0] = 1234;

        final CodePointer codePointer = ClassModelTestUtils.code(adjacent(-2))[0];

        assertEquals(new ArrayStoreImpl(
                new LocalVariableReferenceImpl("array", int[].class, 1),
                AST.constant(0),
                AST.constant(1234)), codePointer.getElement());
    }

    @Test
    public void longArrayLoadCanBeDecompiled() {
        long[] array = new long[]{1};
        long l = array[0];

        Assert.assertEquals(new VariableAssignmentImpl(
                new ArrayLoadImpl(
                        new LocalVariableReferenceImpl("array", long[].class, 1),
                        AST.constant(0),
                        long.class),
                2, "l", long.class), ClassModelTestUtils.code(adjacent(-7))[0].getElement());
    }

    @Test
    public void floatArrayLoadCanBeDecompiled() {
        float[] array = new float[]{1f};
        float f = array[0];

        Assert.assertEquals(new VariableAssignmentImpl(
                new ArrayLoadImpl(
                        new LocalVariableReferenceImpl("array", float[].class, 1),
                        AST.constant(0),
                        float.class),
                2, "f", float.class), ClassModelTestUtils.code(adjacent(-7))[0].getElement());
    }

    @Test
    public void doubleArrayLoadCanBeDecompiled() {
        double[] array = new double[]{1d};
        double d = array[0];

        Assert.assertEquals(new VariableAssignmentImpl(
                new ArrayLoadImpl(
                        new LocalVariableReferenceImpl("array", double[].class, 1),
                        AST.constant(0),
                        double.class),
                2, "d", double.class), ClassModelTestUtils.code(adjacent(-7))[0].getElement());
    }

    @Test
    public void booleanArrayLoadCanBeDecompiled() {
        boolean[] array = new boolean[]{true};
        boolean b = array[0];

        Assert.assertEquals(new VariableAssignmentImpl(
                new ArrayLoadImpl(
                        new LocalVariableReferenceImpl("array", boolean[].class, 1),
                        AST.constant(0),
                        boolean.class),
                2, "b", boolean.class), ClassModelTestUtils.code(adjacent(-7))[0].getElement());
    }

    @Test
    public void charArrayLoadCanBeDecompiled() {
        char[] array = new char[]{'c'};
        char c = array[0];

        Assert.assertEquals(new VariableAssignmentImpl(
                new ArrayLoadImpl(
                        new LocalVariableReferenceImpl("array", char[].class, 1),
                        AST.constant(0),
                        char.class),
                2, "c", char.class), ClassModelTestUtils.code(adjacent(-7))[0].getElement());
    }

    @Test
    public void shortArrayLoadCanBeDecompiled() {
        short[] array = new short[]{(short) 1};
        short s = array[0];

        Assert.assertEquals(new VariableAssignmentImpl(
                new ArrayLoadImpl(
                        new LocalVariableReferenceImpl("array", short[].class, 1),
                        AST.constant(0),
                        short.class),
                2, "s", short.class), ClassModelTestUtils.code(adjacent(-7))[0].getElement());
    }

    private Element[] parseMethodBody(String methodName) {
        return ClassModelTestUtils.methodBodyOf(ExampleClass.class, methodName);
    }

    private void decompileCallerWithCallback(Caller caller, DecompilationProgressCallback callback) throws IOException {
        final Decompiler decompiler = new DecompilerImpl();
        final Method method = ClassModelTestUtils.methodWithName(getClass(), caller.getMethodName());

        try (CodeStream code = new InputStreamCodeStream(Methods.getCodeForLineNumber(method, caller.getLineNumber()))) {
            decompiler.parse(method, code, callback);
        }
    }

    private static void accept(Runnable procedure) {
    }

    private static class ExampleClass {

        public static String STATIC_STRING = "foo";

        private String string = new String("Hello World!");

        private void constantsOfAllTypes() {
            boolean z = true;
            byte b = 100;
            short s = 200;
            char c = 300;
            int n = 400;
            long l = 500;
            float f = 600.1234f;
            double d = 700.1234d;
        }

        private void methodWithLongConstants() {
            long l1 = 0;
            long l2 = 1;
        }

        private void methodWithByteConstants() {
            byte b1 = 0;
            byte b2 = 1;
            byte b3 = 2;
        }

        private void methodWithStaticFieldReference() {
            final BigDecimal b = BigDecimal.ZERO;
        }

        private void methodWithLambdaDeclarationAndInvocation() {
            final Supplier<String> s = () -> "Hello World!";
            s.get();
        }

        private void methodWithFieldAccess() {
            string.toString();
        }

        private void emptyMethod() {
        }

        private int methodWithIntegerReturn() {
            return 1234;
        }

        private int exampleMethodWithReturnFromOtherMethod() {
            return 1 + methodWithIntegerReturn();
        }

        private int add(int a, int b) {
            return a + b;
        }

        private int exampleMethodWithMethodCallWithParameters() {
            return add(1, 2);
        }

        private int returnLocal() {
            int n = 100;

            return n;
        }

        private void methodWithConstantPoolReferences() {
            int n = 123456789;
            float f = 123456789f;
            String str = "foobar";
        }

        private void methodWithEqComparison() {
            boolean b1 = "str".length() == 3;
        }

    }
}
