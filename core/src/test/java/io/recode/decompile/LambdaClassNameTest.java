package io.recode.decompile;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.function.Function;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;

public class LambdaClassNameTest {

    @Test
    public void validLambdaClassNameCanBeParsed() {
        final LambdaClassName className = LambdaClassName.from("io.recode.decompile.LambdaClassNameTest$$Lambda$1/2110121908");

        assertEquals("io.recode.decompile.LambdaClassNameTest", className.getDeclaringClassName());
        assertEquals("Lambda", className.getType());
        assertEquals(1, className.getIndex());
        assertEquals("2110121908", className.getTag());
    }

    @Test
    public void classNameCanBeParsedForValidLambda() {
        final Runnable r = () -> {};
        final LambdaClassName className = LambdaClassName.from(r.getClass().getName());

        assertEquals(LambdaClassNameTest.class.getName(), className.getDeclaringClassName());
        assertEquals("Lambda", className.getType());
    }

    @Test
    public void classNameCanBeParsedForStaticMethodPointer() {
        final Function<String, Integer> function = Integer::parseInt;
        final LambdaClassName className = LambdaClassName.from(function.getClass().getName());

        assertEquals("io.recode.decompile.LambdaClassNameTest", className.getDeclaringClassName());
    }

    @Test
    public void invalidLambdaClassNameShouldBeRejected() {
        assertThrown(() -> LambdaClassName.from("io.recode.decompile.LambdaClassNameTest$$Lambda$1"), IllegalArgumentException.class);
        assertThrown(() -> LambdaClassName.from("io.recode.decompile.LambdaClassNameTest$$Lambda1/2110121908"), IllegalArgumentException.class);
        assertThrown(() -> LambdaClassName.from("io.recode.decompile.LambdaClassNameTest$Lambda$1/2110121908"), IllegalArgumentException.class);
        assertThrown(() -> LambdaClassName.from("io.recode.decompile.LambdaClassNameTest"), IllegalArgumentException.class);
    }
}