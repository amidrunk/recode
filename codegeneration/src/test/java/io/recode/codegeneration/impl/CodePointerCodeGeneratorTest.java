package io.recode.codegeneration.impl;

import io.recode.CodeLocation;
import io.recode.classfile.Method;
import io.recode.codegeneration.*;
import io.recode.decompile.CodeLocationDecompiler;
import io.recode.decompile.CodePointer;
import io.recode.decompile.Decompiler;
import io.recode.decompile.impl.CodeLocationDecompilerImpl;
import io.recode.decompile.impl.CodePointerImpl;
import io.recode.model.AST;
import io.recode.model.ArrayInitializer;
import io.recode.model.Element;
import io.recode.model.ElementType;
import io.recode.model.impl.*;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.recode.Caller.adjacent;
import static io.recode.codegeneration.impl.TestUtils.assertThrown;
import static io.recode.model.AST.constant;
import static io.recode.model.AST.local;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class CodePointerCodeGeneratorTest {

    private final CodePointerCodeGenerator codeGenerator = new CodePointerCodeGenerator();

    private final Method method = mock(Method.class);
    private final Decompiler decompiler = mock(Decompiler.class);
    private final CodeGeneratorDelegate exampleDelegate = mock(CodeGeneratorDelegate.class);
    private final CodeGeneratorAdvice exampleAdvice = mock(CodeGeneratorAdvice.class);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new CodePointerCodeGenerator(null, mock(CodeGeneratorConfiguration.class)), AssertionError.class);
        assertThrown(() -> new CodePointerCodeGenerator(mock(Decompiler.class), null), AssertionError.class);
    }

    @Test
    public void aroundAdviceShouldBeCalledForMatchingElement() {
        final CodePointerCodeGenerator codeGenerator = new CodePointerCodeGenerator(decompiler, SimpleCodeGeneratorConfiguration.configurer()
                .on(ElementSelector.forType(ElementType.CONSTANT)).then(exampleDelegate)
                .around(ElementSelector.forType(ElementType.CONSTANT)).then(exampleAdvice)
                .configuration());

        doAnswer(answer -> {
            ((CodeGeneratorPointcut) answer.getArguments()[2])
                    .proceed((CodeGenerationContext) answer.getArguments()[0], (CodePointer) answer.getArguments()[1]);
            return null;
        }).when(exampleAdvice).apply(any(), any(), any());

        final CodePointer codePointer = pointer(constant(1));

        generatedCode(codeGenerator, codePointer);

        final InOrder inOrder = inOrder(exampleDelegate, exampleAdvice);

        inOrder.verify(exampleAdvice).apply(any(), Mockito.eq(codePointer), any());
        inOrder.verify(exampleDelegate).apply(any(), Mockito.eq(codePointer), any());
    }

    private String generatedCode(CodePointerCodeGenerator codeGenerator, CodePointer codePointer) {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(bout);

        codeGenerator.generateCode(codePointer, writer);

        writer.flush();

        return bout.toString();
    }

    // TODO: move these to some integration test catageory

    @Test
    public void returnCanBeDescribed() {
        final String code = codeGenerator.generateCode(pointer(new ReturnImpl()), StandardCharsets.UTF_8);

        assertEquals("return", code);
    }

    @Test
    public void lambdaMethodReferenceCanBeDescribed() {
        expect(this::lambdaTargetTest);

        final String code = toCode(adjacent(-2));

        assertEquals("expect(this::lambdaTargetTest)", code);
    }

    @Test
    public void lambdaWithImplicitThisAndOtherCodeCanBeDescribed() {
        expect(() -> {
            Math.random();
            this.lambdaTargetTest();
        });

        final String code = toCode(adjacent(-5));

        assertEquals("expect(() -> {\n" +
                        "   Math.random();\n" +
                        "   lambdaTargetTest();\n" +
                        ")"
                , code);
    }

    @Test
    public void lambdaWithInstanceMethodReferenceCanBeDescribed() {
        callFunction(String::length, "foo");

        final String code = toCode(adjacent(-2));

        assertEquals("callFunction(String::length, \"foo\")", code);
    }

    @Test
    public void lambdaWithArgumentsAndCodeCanBeDescribed() {
        callFunction(s -> s.length() + 1, "foo");

        final String code = toCode(adjacent(-2));

        assertEquals("callFunction(s -> s.length() + 1, \"foo\")", code);
    }

    @Test
    public void enumReferenceCanBeDescribed() {
        final ElementType fieldReference = ElementType.FIELD_REFERENCE;

        final String code = toCode(adjacent(-2));

        assertEquals("ElementType fieldReference = ElementType.FIELD_REFERENCE", code);
    }

    @Test
    @Ignore("Generics are hard")
    public void lambdaWithGenericsTypeParametersCanBeDescribed() {
        final Supplier<String> supplier = () -> "Hello World!";

        final String code = toCode(adjacent(-2));

        assertEquals("Supplier<String> supplier = () -> \"Hello World!\"", code);
    }

    @Test
    public void newInstanceCanBeDescribed() {
        final String code = codeGenerator.generateCode(new CodePointerImpl(mock(Method.class), AST.newInstance(String.class, constant(1234), constant("foo"))), StandardCharsets.UTF_8);

        assertEquals("new String(1234, \"foo\")", code);
    }

    @Test
    public void memberAccessOfPrivateVariableInInnerClassCanBeDescribed() {
        final ExampleInnerClass exampleInnerClass = new ExampleInnerClass();

        assertEquals(0, exampleInnerClass.exampleVariable);

        final String code = toCode(adjacent(-2));

        assertEquals("Assert.assertEquals(0L, (long)exampleInnerClass.exampleVariable)", code);
    }

    @Test
    public void arrayStoreCanBeDescribed() {
        final String code = toString(new ArrayStoreImpl(local("foo", String[].class, 1), constant(1234), constant("Hello World!")));

        assertEquals("foo[1234] = \"Hello World!\"", code);
    }

    @Test
    public void newArrayWithoutInitializationCanBeDescribed() {
        final String code = toString(new NewArrayImpl(String[].class, String.class, constant(1), Collections.<ArrayInitializer>emptyList()));

        assertEquals("new String[1]", code);
    }

    @Test
    public void newArrayWithInitializationCanBeDescribed() {
        final String code = toString(new NewArrayImpl(String[].class, String.class, constant(2), Arrays.asList(
                new ArrayInitializerImpl(0, constant("foo")),
                new ArrayInitializerImpl(1, constant("bar")))));

        assertEquals("new String[] { \"foo\", \"bar\" }", code);
    }

    @Test
    public void innerClassFieldAssignmentCanBeDescribed() {
        final ExampleInnerClass exampleInnerClass = new ExampleInnerClass();

        exampleInnerClass.exampleVariable = 1234;

        final String code = toCode(adjacent(-2));

        assertEquals("exampleInnerClass.exampleVariable = 1234", code);
    }

    @Test
    public void typeCastCanBeDescribed() {
        final String code = codeGenerator.generateCode(pointer(AST.cast(constant("foo")).to(String.class)), StandardCharsets.UTF_8);

        assertEquals("(String)\"foo\"", code);
    }

    @Test
    public void arrayLoadCanBeDescribed() {
        final String generatedCode = toString(new ArrayLoadImpl(AST.local("foo", String[].class, 1), AST.constant(1234), String.class));

        assertEquals("foo[1234]", generatedCode);
    }

    private String toString(Element element) {
        return codeGenerator.generateCode(new CodePointerImpl<>(method, element), StandardCharsets.UTF_8);
    }

    private<T> ExpectContinuation expect(Runnable value) {
        return expected -> {};
    }

    private interface ExpectContinuation<T> {
        void toBe(T expected);
    }

    private String toCode(CodeLocation codeLocation) {
        final CodeLocationDecompiler decompiler = new CodeLocationDecompilerImpl();
        final CodePointer[] codePointers;

        try {
            codePointers = decompiler.decompileCodeLocation(codeLocation);
        } catch (IOException e) {
            throw new RuntimeException("Failed to decompile code location", e);
        }

        return Arrays.stream(codePointers)
                .map(cp -> codeGenerator.generateCode(cp, StandardCharsets.UTF_8))
                .collect(joining("\n"));
    }

    private CodePointer pointer(Element element) {
        return new CodePointerImpl(method, element);
    }

    private void lambdaTargetTest() {
    }

    private <T, R> R callFunction(Function<T, R> function, T arg) {
        return function.apply(arg);
    }

    private class ExampleInnerClass {

        private int exampleVariable;

    }
}
