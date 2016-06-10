package io.recode.codegeneration.impl;

import io.recode.RuntimeTypeResolver;
import io.recode.annotations.DSL;
import io.recode.classfile.ClassPathClassFileResolver;
import io.recode.classfile.Method;
import io.recode.classfile.impl.ClassFileReaderImpl;
import io.recode.codegeneration.*;
import io.recode.decompile.CodePointer;
import io.recode.decompile.impl.CodePointerImpl;
import io.recode.decompile.impl.DecompilerImpl;
import io.recode.model.*;
import io.recode.model.impl.ArrayInitializerImpl;
import io.recode.model.impl.ArrayLoadImpl;
import io.recode.model.impl.InstanceAllocationImpl;
import io.recode.model.impl.NewArrayImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import static io.recode.codegeneration.impl.JavaSyntaxCodeGeneration.selectInnerClassFieldAccess;
import static io.recode.codegeneration.impl.TestUtils.assertThrown;
import static io.recode.model.AST.*;
import static io.recode.model.AST.eq;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class JavaSyntaxCodeGenerationTest {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PrintWriter out = new PrintWriter(baos);
    private final CodeGeneratorConfiguration configuration = coreConfiguration();

    private final CodeGenerationDelegate codeGenerationDelegate = (context, codePointer) -> {
        configuration.getDelegate(context, codePointer).apply(context, codePointer, out);
    };

    private final CodeStyle codeStyle = mock(CodeStyle.class);

    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(
            codeGenerationDelegate,
            new RuntimeTypeResolver(),
            new ClassPathClassFileResolver(new ClassFileReaderImpl()),
            new DecompilerImpl(),
            codeStyle);

    private final Method method = mock(Method.class);

    private final JavaSyntaxCodeGeneration delegation = new JavaSyntaxCodeGeneration();

    @Before
    public void setup() {
        doAnswer(invocationOnMock -> ((Class) invocationOnMock.getArguments()[0]).getSimpleName())
                .when(codeStyle).getTypeName(any(Type.class));
        when(codeStyle.shouldOmitThis()).thenReturn(false);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> delegation.configure(null), AssertionError.class);
    }

    @Test
    public void coreConfigurationShouldSupportGenerationForReturnElement() {
        assertEquals("return", codeFor(AST.$return()));
    }

    @Test
    public void coreConfigurationShouldSupportGenerationForConstantElements() {
        assertEquals("1.234", codeFor(AST.constant(1.234d)));
        assertEquals("1.234f", codeFor(AST.constant(1.234f)));
        assertEquals("1234L", codeFor(AST.constant(1234L)));
        assertEquals("1234", codeFor(AST.constant(1234)));
        assertEquals("\"foo\"", codeFor(AST.constant("foo")));
    }

    @Test
    public void coreConfigurationShouldSupportGenerationOfReturnValue() {
        assertEquals("return 1", codeFor(AST.$return(constant(1))));
    }

    @Test
    public void coreConfigurationShouldSupportGenerationOfVariableReference() {
        assertEquals("foo", codeFor(AST.local("foo", String.class, 1)));
    }

    @Test
    public void coreConfigurationShouldSupportInstanceMethodCall() {
        assertEquals("\"foo\".toString()", codeFor(AST.call(constant("foo"), "toString", String.class)));
        assertEquals("\"foo\".length()", codeFor(AST.call(constant("foo"), "length", int.class)));
        assertEquals("\"foo\".substring(1, 2)", codeFor(AST.call(constant("foo"), "substring", String.class, constant(1), constant(2))));
    }

    @Test
    public void codeConfigurationShouldSupportStaticMethodCall() {
        final MethodCall staticMethodCall = AST.call(String.class, "valueOf", String.class, constant(1));

        assertEquals("String.valueOf(1)", codeFor(staticMethodCall));
    }

    @Test
    public void booleanBoxCallShouldBeSupported() {
        final MethodSignature valueOfSignature = MethodSignature.parse("(Z)Ljava/lang/Boolean;");
        final MethodCall trueCall = AST.call(Boolean.class, "valueOf", valueOfSignature, constant(1));
        final MethodCall falseCall = AST.call(Boolean.class, "valueOf", valueOfSignature, constant(0));

        assertEquals("true", codeFor(trueCall));
        assertEquals("false", codeFor(falseCall));
    }

    @Test
    public void selectBooleanBoxCallShouldSelectBooleanValueOfMethod() {
        final ElementSelector<MethodCall> selector = JavaSyntaxCodeGeneration.selectBooleanBoxCall();
        final CodePointerImpl<MethodCall> codePointer = new CodePointerImpl<>(method,
                AST.call(Boolean.class, "valueOf", MethodSignature.parse("(Z)Ljava/lang/Boolean;"), constant(0)));

        assertEquals(ElementType.METHOD_CALL, selector.getElementType());
        assertTrue(selector.matches(codePointer));
    }

    @Test
    public void isDSLMethodCallShouldReturnTrueIfTargetTypeHasDSLAnnotation() {
        final boolean isDSLCall = JavaSyntaxCodeGeneration.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(SomeClass.class, "doStuff", void.class, local("foo", Object.class, 1))));

        assertTrue(isDSLCall);
    }

    @Test
    public void isDSLMethodCallShouldReturnFalseIfTargetTypeDoesNotHaveDSLAnnotation() {
        final boolean isDSLCall = JavaSyntaxCodeGeneration.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(String.class, "valueOf", String.class, constant("foo"))));

        assertFalse(isDSLCall);
    }

    @Test
    public void isDSLMethodCallShouldReturnFalseIfTargetTypeIsNotAClass() {
        final boolean isDSLMethodCall = JavaSyntaxCodeGeneration.isDSLMethodCall().test(new CodePointerImpl<>(method,
                AST.call(mock(Type.class), "foo", String.class, constant(1))));

        assertFalse(isDSLMethodCall);
    }

    @Test
    public void selectDSLCallShouldSelectMethodCallWhereTypeHasDSLAnnotation() {
        final boolean isDSLMethodCall = JavaSyntaxCodeGeneration.selectDSLMethodCall().matches(new CodePointerImpl<>(
                method, AST.call(SomeClass.class, "doStuff", void.class, local("foo", Object.class, 1))
        ));

        assertTrue(isDSLMethodCall);
    }

    @Test
    public void dslMethodCallsShouldGenerateCallsWithoutTargetTypeSpecified() {
        final String code = codeFor(AST.call(SomeClass.class, "doStuff", void.class, constant("foo")));

        assertEquals("doStuff(\"foo\")", code);
    }

    @Test
    public void thisReferenceCanBeOmittedOnInstanceMethodCall() {
        when(codeStyle.shouldOmitThis()).thenReturn(true);

        final String code = codeFor(new CodePointerImpl(method, AST.call(local("this", Object.class, 0), "foo", String.class)));

        assertEquals("foo()", code);
    }

    @Test
    public void thisReferenceCanBeIncludedOnInstanceMethodCall() {
        when(codeStyle.shouldOmitThis()).thenReturn(false);

        final String code = codeFor(new CodePointerImpl(method, AST.call(local("this", Object.class, 0), "foo", String.class)));

        assertEquals("this.foo()", code);
    }

    @Test
    public void selectUninitializedNewArrayShouldSelectNewArrayWithoutInitializers() {
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Collections.emptyList()));

        assertTrue(JavaSyntaxCodeGeneration.selectUninitializedNewArray().matches(codePointer));
    }

    @Test
    public void selectUninitializedNewArrayShouldNotSElectNewArrayWithInitializers(){
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Arrays.asList(new ArrayInitializerImpl(0, constant("foo")))));

        assertFalse(JavaSyntaxCodeGeneration.selectUninitializedNewArray().matches(codePointer));
    }

    @Test
    public void selectInitializedNewArrayShouldSelectNewArrayWithInitializers() {
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Arrays.asList(new ArrayInitializerImpl(0, constant("foo")))));

        assertTrue(JavaSyntaxCodeGeneration.selectInitializedNewArray().matches(codePointer));
    }

    @Test
    public void selectInitializedNewArrayShouldNotSelectNewArrayWithoutInitializers() {
        final CodePointer<NewArray> codePointer = codePointer(new NewArrayImpl(String[].class, String.class,
                constant(1), Collections.emptyList()));

        assertFalse(JavaSyntaxCodeGeneration.selectInitializedNewArray().matches(codePointer));
    }

    @Test
    public void primitiveBoxCallsShouldBeHandledAndGeneratedAsImplicit() {
        assertEquals("1", codeFor(new CodePointerImpl(method, AST.call(Byte.class, "valueOf",
                MethodSignature.parse("(B)Ljava/lang/Byte;"), constant(1)))));
        assertEquals("1", codeFor(new CodePointerImpl(method, AST.call(Short.class, "valueOf",
                MethodSignature.parse("(S)Ljava/lang/Short;"), constant(1)))));
        assertEquals("1", codeFor(new CodePointerImpl(method, AST.call(Character.class, "valueOf",
                MethodSignature.parse("(C)Ljava/lang/Character;"), constant(1)))));
        assertEquals("1", codeFor(new CodePointerImpl(method, AST.call(Integer.class, "valueOf",
                MethodSignature.parse("(I)Ljava/lang/Integer;"), constant(1)))));
        assertEquals("1L", codeFor(new CodePointerImpl(method, AST.call(Long.class, "valueOf",
                MethodSignature.parse("(J)Ljava/lang/Long;"), constant(1L)))));
        assertEquals("1.0f", codeFor(new CodePointerImpl(method, AST.call(Float.class, "valueOf",
                MethodSignature.parse("(F)Ljava/lang/Float;"), constant(1F)))));
        assertEquals("1.0", codeFor(new CodePointerImpl(method, AST.call(Double.class, "valueOf",
                MethodSignature.parse("(D)Ljava/lang/Double;"), constant(1D)))));
    }

    @Test
    public void isPrimitiveBoxCallShouldReturnTrueForBoxMethods() {
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = JavaSyntaxCodeGeneration.isPrimitiveBoxCall();

        assertTrue(primitiveBoxCall.test(codePointer(AST.call(Byte.class, "valueOf",
                MethodSignature.parse("(B)Ljava/lang/Byte;"), constant(1)))));
        assertTrue(primitiveBoxCall.test(codePointer(AST.call(Short.class, "valueOf",
                MethodSignature.parse("(S)Ljava/lang/Short;"), constant(1)))));
        assertTrue(primitiveBoxCall.test(codePointer(AST.call(Character.class, "valueOf",
                MethodSignature.parse("(C)Ljava/lang/Character;"), constant(1)))));
        assertTrue(primitiveBoxCall.test(codePointer(AST.call(Integer.class, "valueOf",
                MethodSignature.parse("(I)Ljava/lang/Integer;"), constant(1)))));
        assertTrue(primitiveBoxCall.test(codePointer(AST.call(Long.class, "valueOf",
                MethodSignature.parse("(J)Ljava/lang/Long;"), constant(1L)))));
        assertTrue(primitiveBoxCall.test(codePointer(AST.call(Float.class, "valueOf",
                MethodSignature.parse("(F)Ljava/lang/Float;"), constant(1F)))));
        assertTrue(primitiveBoxCall.test(codePointer(AST.call(Double.class, "valueOf",
                MethodSignature.parse("(D)Ljava/lang/Double;"), constant(1D)))));
    }

    @Test
    public void isPrimitiveBoxCallShouldReturnFalseForInstanceMethod() {
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = JavaSyntaxCodeGeneration.isPrimitiveBoxCall();

        assertFalse(primitiveBoxCall.test(codePointer(AST.call(constant(1), "valueOf", Integer.class, constant(1)))));
    }

    @Test
    public void isPrimitiveBoxCallShouldReturnFalseForNonMatchingStaticMethod() {
        final Predicate<CodePointer<MethodCall>> primitiveBoxCall = JavaSyntaxCodeGeneration.isPrimitiveBoxCall();

        assertFalse(primitiveBoxCall.test(codePointer(AST.call(Integer.class, "valueOf", Integer.class, constant(1), constant(10)))));
    }

    @Test
    public void selectInnerClassFieldAccessShouldNotSelectNonMethodCall() {
        assertFalse(((ElementSelector) selectInnerClassFieldAccess()).matches(codePointer(AST.constant(1))));
    }

    @Test
    public void selectInnerClassFieldAccessShouldNotSelectNonStaticMethodCall() {
        final boolean matchesInstanceMethod = selectInnerClassFieldAccess()
                .matches(codePointer(call(AST.constant("foo"), "toString", String.class)));

        assertFalse(matchesInstanceMethod);
    }

    @Test
    public void innerClassFieldSelectorShouldNotSelectStaticMethodWithInvalidArguments() {
        assertFalse(selectInnerClassFieldAccess().matches(codePointer(AST.call(String.class, "access$100", String.class, constant("foo"), constant("bar"), constant("baz")))));
        assertFalse(selectInnerClassFieldAccess().matches(codePointer(AST.call(String.class, "valueOf", String.class, constant("foo")))));
        assertFalse(selectInnerClassFieldAccess().matches(codePointer(AST.call(String.class, "access$100", String.class))));
    }

    @Test
    public void innerClassFieldSelectorShouldNotSelectNonInnerClassMethodCall() {
        assertFalse(selectInnerClassFieldAccess().matches(codePointer(AST.call(String.class, "access$100", String.class, constant(1234)))));
    }

    @Test
    public void innerClassFieldSelectorShouldSelectInnerClassFieldReference() {
        final boolean innerClassFieldReferenceSelected = selectInnerClassFieldAccess()
                .matches(codePointer(AST.call(Inner.class, "access$100", String.class, local("myInner", Inner.class, 1))));

        assertTrue(innerClassFieldReferenceSelected);
    }

    @Test
    public void innerClassFieldAccessShouldGeneratePlainFieldReference() {
        final Inner inner = new Inner();
        final String str = inner.str;

        final MethodCall innerClassFieldAccessor = AST.call(Inner.class, "access$100", String.class, local("myInner", Inner.class, 1));

        assertEquals("myInner.str", codeFor(innerClassFieldAccessor));
    }

    @Test
    public void innerClassFieldAssignmentShouldGeneratePlainFieldAssignment() {
        final Inner inner = new Inner();

        inner.str = "foo";

        final MethodCall innerClassFieldAssignment = AST.call(Inner.class, "access$102", String.class, local("myInner", Inner.class, 1), constant("foo"));

        assertEquals("myInner.str = \"foo\"", codeFor(innerClassFieldAssignment));
    }

    @Test
    public void castExtensionShouldBeSupported() {
        assertEquals("(String)\"foo\"", codeFor(AST.cast(constant("foo")).to(String.class)));
    }

    @Test
    public void classConstantShouldOutputClass() {
        assertEquals("String.class", codeFor(constant(String.class)));
    }

    @Test
    public void arrayLoadShouldOutputArrayElementAccess() {
        final String code = codeFor(new ArrayLoadImpl(AST.local("foo", String[].class, 1), AST.constant(1234), String.class));

        assertEquals("foo[1234]", code);
    }

    @Test
    public void allocateInstanceShouldBeSupported() {
        assertEquals("new String<uninitialized>", codeFor(new InstanceAllocationImpl(String.class)));
    }

    @Test
    public void binaryOperatorsShouldBeSupported() {
        assertEquals("1 + 2", codeFor(add(constant(1), constant(2), int.class)));
        assertEquals("1 - 2", codeFor(sub(constant(1), constant(2), int.class)));
        assertEquals("1 * 2", codeFor(mul(constant(1), constant(2), int.class)));
        assertEquals("1 / 2", codeFor(div(constant(1), constant(2), int.class)));
        assertEquals("1 % 2", codeFor(mod(constant(1), constant(2), int.class)));
        assertEquals("1 << 2", codeFor(lshift(constant(1), constant(2), int.class)));
        assertEquals("1 >> 2", codeFor(rshift(constant(1), constant(2), int.class)));
        assertEquals("1 >>> 2", codeFor(unsignedRightShift(constant(1), constant(2), int.class)));
        assertEquals("1 == 2", codeFor(eq(constant(1), constant(2))));
        assertEquals("1 != 2", codeFor(ne(constant(1), constant(2))));
        assertEquals("1 < 2", codeFor(lt(constant(1), constant(2))));
        assertEquals("1 <= 2", codeFor(le(constant(1), constant(2))));
        assertEquals("1 > 2", codeFor(gt(constant(1), constant(2))));
        assertEquals("1 >= 2", codeFor(ge(constant(1), constant(2))));
        assertEquals("1 && 2", codeFor(and(constant(1), constant(2))));
        assertEquals("1 || 2", codeFor(or(constant(1), constant(2))));
        assertEquals("1 & 2", codeFor(bitwiseAnd(constant(1), constant(2), int.class)));
        assertEquals("1 | 2", codeFor(bitwiseOr(constant(1), constant(2), int.class)));
        assertEquals("1 ^ 2", codeFor(xor(constant(1), constant(2), int.class)));
    }

    @Test
    public void varArgsWithNoOtherArgumentsShouldBeSupportedInStatic() {
        final String code = codeFor(call(Varargs.class, "varArgs1", void.class,
                AST.newArray(String[].class, constant("foo"), constant("bar"), constant("baz"))));

        assertEquals("Varargs.varArgs1(\"foo\", \"bar\", \"baz\")", code);
    }

    @Test
    public void varArgsWithNoOtherArgumentsShouldBeSupportedInStatic2() {
        final String code = codeFor(call(Varargs.class, "varArgs1", void.class,
                AST.newArray(String[].class, constant("foo"), constant("bar"), constant("baz"))));

        assertEquals("Varargs.varArgs1(\"foo\", \"bar\", \"baz\")", code);
    }

    // Support classes
    //

    @DSL
    public static class SomeClass {

        public static void doStuff(Object o) {}

    }

    private static class Inner {

        private String str;

    }

    private static class Varargs {

        public void varArgs1(String ... args) {
        }

        public void varArgs2(int head, int ... tail) {
        }

    }

    // Support methods
    //

    private <T extends Element> CodePointer<T> codePointer(T element) {
        final CodePointer codePointer = mock(CodePointer.class);

        when(codePointer.getElement()).thenReturn(element);
        when(codePointer.getMethod()).thenThrow(new UnsupportedOperationException());

        return codePointer;
    }

    private String codeFor(Element element) {
        return codeFor(new CodePointerImpl(method, element));
    }

    private String codeFor(CodePointerImpl codePointer) {
        final CodeGeneratorDelegate extension = coreConfiguration().getDelegate(context, codePointer);

        assertNotNull(extension);

        baos.reset();
        extension.apply(context, codePointer, out);
        out.flush();

        return baos.toString();
    }

    private CodeGeneratorConfiguration coreConfiguration() {
        final CodeGeneratorConfigurer configurer = SimpleCodeGeneratorConfiguration.configurer();
        new JavaSyntaxCodeGeneration().configure(configurer);
        return configurer.configuration();
    }
}
