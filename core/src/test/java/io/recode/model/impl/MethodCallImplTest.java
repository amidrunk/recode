package io.recode.model.impl;

import io.recode.model.*;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

import static io.recode.model.AST.call;
import static io.recode.model.AST.constant;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class MethodCallImplTest {

    private final Signature exampleSignature = mock(Signature.class);
    private final Type exampleType = mock(Type.class);
    private final Expression exampleInstance = mock(Expression.class);

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new MethodCallImpl(null, "foo", exampleSignature, exampleInstance, new Expression[0]), AssertionError.class);
        assertThrown(() -> new MethodCallImpl(exampleType, null, exampleSignature, exampleInstance, new Expression[0]), AssertionError.class);
        assertThrown(() -> new MethodCallImpl(exampleType, "", exampleSignature, exampleInstance, new Expression[0]), AssertionError.class);
        assertThrown(() -> new MethodCallImpl(exampleType, "foo", null, exampleInstance, new Expression[0]), AssertionError.class);
        assertThrown(() -> new MethodCallImpl(exampleType, "foo", exampleSignature, exampleInstance, null), AssertionError.class);
        assertThrown(() -> new MethodCallImpl(exampleType, "foo", exampleSignature, exampleInstance, new Expression[0], null), AssertionError.class);
    }

    @Test
    public void constructorShouldInitializeInstance() {
        final MethodSignature signature = MethodSignature.parse("()Ljava/lang/String;");
        final Expression parameter = mock(Expression.class);
        final MethodCallImpl methodCall = new MethodCallImpl(exampleType, "foo", signature, exampleInstance, new Expression[]{parameter});

        assertEquals(ElementType.METHOD_CALL, methodCall.getElementType());
        assertEquals("foo", methodCall.getMethodName());
        assertEquals(signature, methodCall.getSignature());
        assertEquals(signature.getReturnType(), methodCall.getType());
        assertEquals(exampleType, methodCall.getTargetType());
        assertEquals(exampleInstance, methodCall.getTargetInstance());
        assertArrayEquals(new Object[]{parameter}, methodCall.getParameters().toArray());
    }

    @Test
    public void typeOfExpressionCanBeSpecifiedExplicitly() {
        final MethodCallImpl methodCall = new MethodCallImpl(String.class, "toString",
                MethodSignature.parse("()V"), new ConstantImpl("foo", String.class), new Expression[0], int.class);

        assertEquals(int.class, methodCall.getType());
    }

    @Test
    public void isStaticShouldBeTrueForStaticMethodCall() {
        final MethodCall methodCall = call(String.class, "valueOf", String.class);

        assertTrue(methodCall.isStatic());
    }

    @Test
    public void isStaticCallShouldBeFalseForInstanceMethodCall() {
        final MethodCall methodCall = call(AST.constant("foo"), "toString", String.class);

        assertFalse(methodCall.isStatic());
    }

    @Test
    public void withParameterTypesShouldNotAcceptNullArguments() {
        final MethodCall exampleMethodCall = call(constant("foo"), "substring", String.class, constant(1));

        assertThrown(() -> exampleMethodCall.withParameters(null), AssertionError.class);
    }

    @Test
    public void withParameterTypesShouldNotAcceptParametersWithIncorrectParameters() {
        final MethodCall exampleMethodCall = call(constant("foo"), "substring", String.class, constant(1));

        assertThrown(() -> exampleMethodCall.withParameters(Arrays.asList(constant("foo"))), IncompatibleTypeException.class);
        assertThrown(() -> exampleMethodCall.withParameters(Collections.emptyList()), IncompatibleTypeException.class);
        assertThrown(() -> exampleMethodCall.withParameters(Arrays.asList(constant(1), constant(2))), IncompatibleTypeException.class);
    }

    @Test
    public void withParameterTypesShouldReturnMethodWithNewParameterList() {
        final MethodCall exampleMethodCall = call(constant("foo"), "substring", String.class, constant(1));
        final MethodCall newMethodCall = exampleMethodCall.withParameters(Arrays.asList(constant(2)));

        assertEquals(Arrays.asList(constant(1)), exampleMethodCall.getParameters());
        assertEquals(Arrays.asList(constant(2)), newMethodCall.getParameters());
        assertEquals(MethodSignature.parse("(I)Ljava/lang/String;"), newMethodCall.getSignature());
    }

    @Test
    public void methodCallWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new MethodCallImpl(int.class, "foo", mock(Signature.class), mock(Expression.class), new Expression[0], String.class).getMetaData());
        assertEquals(metaData, new MethodCallImpl(int.class, "foo", mock(Signature.class), mock(Expression.class), new Expression[0], String.class, metaData).getMetaData());
    }
}
