package io.recode.codegeneration;

import java.lang.reflect.Type;

public interface CodeStyle {

    /**
     * Returns a type name for the specified type. Can be e.g. qualified class name or simple class name etc
     * depending on code style.
     *
     * @param type The type for which the type name should be retrieved.
     * @return The name of the provided type.
     */
    String getTypeName(Type type);

    /**
     * Returns whether or not "this" should be omitted where possible.
     *
     * @return Whether or not "this" should be omitted.
     */
    boolean shouldOmitThis();

}
