package io.recode;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;

public class CallerTest {

    @Test
    public void constructorShouldValidateParameters() {
        final StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        assertThrown(() -> new Caller(null, 0), AssertionError.class);
        assertThrown(() -> new Caller(Arrays.asList(elements), -1), AssertionError.class);
        assertThrown(() -> new Caller(Arrays.asList(elements), elements.length), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainArguments() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final Caller caller = new Caller(Arrays.asList(stackTrace), 1);

        assertArrayEquals(stackTrace, caller.getCallStack().toArray());
        assertEquals(stackTrace[1], caller.getCallerStackTraceElement());
    }

    @Test
    public void meShouldReturnReferenceToCallingLine() {
        final Caller here = Caller.me();

        assertEquals(getClass().getName(), here.getCallerStackTraceElement().getClassName());
        assertEquals("meShouldReturnReferenceToCallingLine", here.getCallerStackTraceElement().getMethodName());
    }

    @Test
    public void adjacentShouldReturnCallerReferencingAdjacentCodeLine() {
        final Caller caller1 = Caller.me();
        final Caller caller2 = Caller.adjacent(-1);

        assertEquals(caller1.getCallerStackTraceElement(), caller2.getCallerStackTraceElement());
    }

    @Test
    public void getCallerShouldReturnCallerOfCaller() {
        final Caller caller = createCaller();
        final CodeLocation it = caller.getCaller().get();

        assertEquals(getClass().getName(), it.getClassName());
        assertEquals("getCallerShouldReturnCallerOfCaller", it.getMethodName());
    }

    @Test
    public void scanShouldNotAcceptNullPredicate() {
        final Caller caller = Caller.me();

        assertThrown(() -> caller.scan(null), AssertionError.class);
    }

    @Test
    public void scanShouldReturnNonPresentOptionalIfMatchIsNotFound() {
        final Caller caller = Caller.me();

        assertFalse(caller.scan(e -> e.getMethodName().equals("invalid")).isPresent());
    }

    @Test
    public void scanShouldReturnFirstMatchingStackTraceElement() {
        final Caller caller = createCaller();
        final Optional<Caller> result = caller.scan(e -> e.getMethodName().equals("scanShouldReturnFirstMatchingStackTraceElement"));

        assertTrue(result.isPresent());
        assertEquals("scanShouldReturnFirstMatchingStackTraceElement", result.get().getCallerStackTraceElement().getMethodName());
    }

    @Test
    public void locationOfCodeShouldBeAvailableInCaller() {
        final Caller caller = Caller.me();

        assertEquals("io.recode.CallerTest", caller.getClassName());
        assertEquals("locationOfCodeShouldBeAvailableInCaller", caller.getMethodName());
        assertEquals(Thread.currentThread().getStackTrace()[1].getLineNumber() - 4, caller.getLineNumber());
    }

    @Test
    public void topCallerShouldHaveNoCaller() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final Caller caller = new Caller(Arrays.asList(stackTrace), stackTrace.length - 1);

        assertFalse(caller.getCaller().isPresent());
    }

    private Caller createCaller() {
        return Caller.me();
    }
}

