package io.recode.classfile;

public class ClassFileResolutionException extends RuntimeException {

    public ClassFileResolutionException(String message) {
        super(message);
    }

    public ClassFileResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassFileResolutionException(Throwable cause) {
        super(cause);
    }
}
