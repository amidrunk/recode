package io.recode.util;

import io.recode.classfile.ClassFileFormatException;
import io.recode.model.MethodSignature;
import org.junit.Test;

import java.lang.reflect.Type;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class MethodSignatureTest {

    @Test
    public void parseShouldNotAcceptNullOrEmptySpec() {
        assertThrown(() -> MethodSignature.parse(null), AssertionError.class);
        assertThrown(() -> MethodSignature.parse(""), AssertionError.class);
    }

    @Test
    public void signatureMustBeginWithParentheses() {
        assertThrown(() -> MethodSignature.parse("foo)V"), ClassFileFormatException.class);
    }

    @Test
    public void voidMethodWithoutParametersSignatureCanBeParsed() {
        final MethodSignature signature = MethodSignature.parse("()V");

        assertTrue(signature.getParameterTypes().isEmpty());
        assertEquals(void.class, signature.getReturnType());
    }

    @Test
    public void objectMethodWithObjectReturnCanBeParsed() {
        final MethodSignature signature = MethodSignature.parse("(Ljava/lang/Object;)Ljava/lang/String;");

        assertArrayEquals(new Object[]{Object.class}, signature.getParameterTypes().toArray());
        assertEquals(String.class, signature.getReturnType());
    }

    @Test
    public void objectMethodWithMixedParametersCanBeParsed() {
        final MethodSignature signature = MethodSignature.parse("(ILjava/lang/Object;Ljava/lang/String;)V");

        assertArrayEquals(new Object[]{int.class, Object.class, String.class}, signature.getParameterTypes().toArray());
        assertEquals(void.class, signature.getReturnType());
    }

    @Test
    public void methodWithArraysAsParametersAndReturnTypeCanBeParsed() {
        final MethodSignature signature = MethodSignature.parse("([Ljava/lang/Object;[[I)[Ljava/lang/String;");

        assertArrayEquals(new Object[]{Object[].class, int[][].class}, signature.getParameterTypes().toArray());
        assertEquals(String[].class, signature.getReturnType());
    }

    @Test
    public void createShouldNotAcceptInvalidParameters() {
        assertThrown(() -> MethodSignature.create(new Type[]{}, null), AssertionError.class);
        assertThrown(() -> MethodSignature.create(null, String.class), AssertionError.class);
        assertThrown(() -> MethodSignature.create(new Type[]{String.class, null}, String.class), AssertionError.class);
    }

    @Test
    public void createShouldCreateSignatureFromPrimitives() {
        final MethodSignature actualSignature = MethodSignature.create(new Type[]{
                byte.class,
                short.class,
                char.class,
                int.class,
                long.class,
                float.class,
                double.class,
                boolean.class}, void.class);

        final MethodSignature expectedSignature = MethodSignature.parse("(BSCIJFDZ)V");

        assertEquals(expectedSignature, actualSignature);
        assertEquals(expectedSignature.toString(), actualSignature.toString());
    }

    @Test
    public void createShouldCreateSignatureFromObjectsAndPrimitives() {
        final MethodSignature actualSignature = MethodSignature.create(new Type[]{String.class, int.class}, Integer.class);
        final MethodSignature expectedSignature = MethodSignature.parse("(Ljava/lang/String;I)Ljava/lang/Integer;");

        assertEquals(expectedSignature, actualSignature);
        assertEquals(expectedSignature.toString(), actualSignature.toString());
    }

    @Test
    public void createShouldCreateSignatureFromObjectArrays() {
        final MethodSignature actualSignature = MethodSignature.create(new Type[]{Object[].class}, Object[][].class);
        final MethodSignature expectedSignature = MethodSignature.parse("([Ljava/lang/Object;)[[Ljava/lang/Object;");

        assertEquals(expectedSignature, actualSignature);
        assertEquals(expectedSignature.toString(), actualSignature.toString());
    }

    @Test
    public void createShouldCreateSignatureFromPrimitiveArrays() {
        final MethodSignature actualSignature = MethodSignature.create(new Type[]{int[].class, short[].class}, boolean[][].class);
        final MethodSignature expectedSignature = MethodSignature.parse("([I[S)[[Z");

        assertEquals(expectedSignature, actualSignature);
        assertEquals(expectedSignature.toString(), actualSignature.toString());
    }

    @Test(expected = AssertionError.class)
    public void signatureShouldRejectNullMethod() {
        MethodSignature.create(new Type[]{}, void.class).test(null);
    }

    @Test
    public void fromMethodShouldCreateSignature() throws Exception {
        assertEquals("()V", MethodSignature.from(getClass().getMethod("method1")).toString());
        assertEquals("()Ljava/lang/Object;", MethodSignature.from(getClass().getMethod("method2")).toString());
        assertEquals("(ILjava/lang/String;)V", MethodSignature.from(getClass().getMethod("method3", int.class, String.class)).toString());
        assertEquals("(ILjava/lang/String;)Ljava/lang/Object;", MethodSignature.from(getClass().getMethod("method4", int.class, String.class)).toString());
        assertEquals("([I)[Ljava/lang/Object;", MethodSignature.from(getClass().getMethod("method5", int[].class)).toString());
    }

    @Test(expected = AssertionError.class)
    public void fromMethodShouldRejectNullMethod() {
        MethodSignature.from(null);
    }

    @Test
    public void testShouldMatchMethodVoidMethodWithNoParameters() throws Exception  {
        final MethodSignature signature = MethodSignature.create(new Type[]{}, void.class);

        assertTrue(signature.test(getClass().getMethod("method1")));
        assertFalse(signature.test(getClass().getMethod("method2")));
        assertFalse(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method5", int[].class)));
    }

    @Test
    public void testShouldMatchMethodWithNonVoidReturnType() throws Exception {
        final MethodSignature signature = MethodSignature.create(new Type[]{}, Object.class);

        assertFalse(signature.test(getClass().getMethod("method1")));
        assertTrue(signature.test(getClass().getMethod("method2")));
        assertFalse(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method5", int[].class)));
    }

    @Test
    public void testShouldMatchMethodWithVoidReturnTypeAndMultipleParameters() throws Exception {
        final MethodSignature signature = MethodSignature.create(new Type[]{int.class, String.class}, void.class);

        assertFalse(signature.test(getClass().getMethod("method1")));
        assertFalse(signature.test(getClass().getMethod("method2")));
        assertTrue(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method5", int[].class)));
    }

    @Test
    public void testShouldMatchMethodWithObjectReturnTypeAndMultipleParameters() throws Exception {
        final MethodSignature signature = MethodSignature.create(new Type[]{int.class, String.class}, Object.class);

        assertFalse(signature.test(getClass().getMethod("method1")));
        assertFalse(signature.test(getClass().getMethod("method2")));
        assertFalse(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertTrue(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method5", int[].class)));
    }

    @Test
    public void testShouldMatchMethodWithArrayReturnTypeAndArrayParameter() throws Exception {
        final MethodSignature signature = MethodSignature.create(new Type[]{int[].class}, Object[].class);

        assertFalse(signature.test(getClass().getMethod("method1")));
        assertFalse(signature.test(getClass().getMethod("method2")));
        assertFalse(signature.test(getClass().getMethod("method3", int.class, String.class)));
        assertFalse(signature.test(getClass().getMethod("method4", int.class, String.class)));
        assertTrue(signature.test(getClass().getMethod("method5", int[].class)));
    }

    // Support methods
    //

    public void method1() {}

    public Object method2() {
        return null;
    }

    public void method3(int a, String b) {
    }

    public Object method4(int a, String b) {
        return null;
    }

    public Object[] method5(int[] a) {
        return null;
    }
}
