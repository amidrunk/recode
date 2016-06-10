package io.recode.classfile;

import java.lang.reflect.Type;

/**
 * A <code>ClassFileResolver</code> is responsible for resolving a class file given a type. Typically,
 * a resolver would locate the binary class file e.g. on the file system or through a provided class
 * loader etc.
 */
public interface ClassFileResolver {

    ClassFile resolveClassFile(Type type) throws ClassFileResolutionException;

}
