package io.recode.codegeneration.impl;

public class TestUtils {

    public interface ThrowingCommand {

        void call() throws Throwable;

    }

    public static void assertThrown(ThrowingCommand command, Class<? extends Throwable> type) {
        boolean failed = false;

        try {
            command.call();
        } catch (Throwable throwable) {
            if (!type.isInstance(throwable)) {
                throw new AssertionError("Expected command to throw " + type.getSimpleName() + ", actually through " + throwable.getClass().getSimpleName());
            }

            failed = true;
        }

        if (!failed) {
            throw new AssertionError("Expected command to throw " + type.getSimpleName());
        }
    }
}
