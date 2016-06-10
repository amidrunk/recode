package io.recode.classfile;

public class ClassFileNotFoundException extends ClassFileResolutionException {

    public ClassFileNotFoundException(String message) {
        super(message);
    }

    public ClassFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassFileNotFoundException(Throwable cause) {
        super(cause);
    }

}
