package io.recode.classfile;

import java.lang.reflect.Type;

public interface ExceptionTableEntry {

    int getStartPC();

    int getEndPC();

    int getHandlerPC();

    /**
     * Returns type type of exceptions caught by the handler. This is null is the entry represents the
     * finally clause.
     *
     * @return The catch type, or null for finally.
     */
    Type getCatchType();

}
