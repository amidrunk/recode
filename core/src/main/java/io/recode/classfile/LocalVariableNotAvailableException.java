package io.recode.classfile;

public final class LocalVariableNotAvailableException extends IllegalStateException {

    public LocalVariableNotAvailableException(String s) {
        super(s);
    }

    public LocalVariableNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
