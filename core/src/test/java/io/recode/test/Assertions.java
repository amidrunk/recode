package io.recode.test;

import org.hamcrest.Matcher;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class Assertions {

    public static void assertThrown(ThrowingCommand command, Class<? extends Throwable> exceptionType) {
        assertThrown(command, instanceOf(exceptionType));
    }

    public static void assertThrown(ThrowingCommand command, Matcher<? extends Throwable> exceptionMatcher) {
        boolean failed = false;

        try {
            command.call();
        } catch (Throwable throwable) {
            if (!exceptionMatcher.matches(throwable)) {
                throw new AssertionError("Expected throwable '" + throwable.getClass().getName() + "' to match");
            }

            failed = true;
        }

        if (!failed) {
            throw new AssertionError("Expected command to throw");
        }
    }

    @FunctionalInterface
    public interface ThrowingCommand {

        void call() throws Throwable;
    }

}
