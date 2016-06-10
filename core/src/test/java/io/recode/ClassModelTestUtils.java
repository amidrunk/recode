package io.recode;

import io.recode.classfile.ClassFile;
import io.recode.classfile.ClassFileReader;
import io.recode.classfile.Method;
import io.recode.classfile.impl.ClassFileReaderImpl;
import io.recode.decompile.CodePointer;
import io.recode.decompile.CodeStream;
import io.recode.decompile.impl.CodeLocationDecompilerImpl;
import io.recode.decompile.impl.DecompilerImpl;
import io.recode.decompile.impl.InputStreamCodeStream;
import io.recode.model.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ClassModelTestUtils {

    // LambdasTest, internal
    public static ClassFile classFileOf(Class<?> clazz) {
        final String resourceName = "/" + clazz.getName().replace('.', '/') + ".class";

        try (InputStream in = clazz.getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new AssertionError("No class file resource exists for class '"
                        + clazz.getName() + "' (resource = " + resourceName + ")");
            }

            final ClassFileReader classFileReader = new ClassFileReaderImpl();

            return classFileReader.read(in);
        } catch (IOException e) {
            throw new AssertionError("getResourceAsStream failed with IOException", e);
        }
    }

    // DecompilerImplTest, internal
    public static Method methodWithName(Class<?> clazz, String methodName) {
        return methodWithName(classFileOf(clazz), methodName);
    }

    public static Method methodWithName(ClassFile classFile, String methodName) {
        final Optional<Method> optionalMethod = classFile.getMethods().stream()
                .filter(m -> m.getName().equals(methodName))
                .findFirst();

        return optionalMethod.orElseThrow(() -> new AssertionError("Method not found: " + classFile.getName() + "." + methodName + "(..)"));
    }

    // DecompilerImplTest
    public static Element[] methodBodyOf(Class<?> clazz, String methodName) {
        return methodBodyOf(methodWithName(classFileOf(clazz), methodName));
    }

    // internal
    public static Element[] methodBodyOf(Method method) {
        try (CodeStream code = new InputStreamCodeStream(method.getCode().getCode())) {
            return new DecompilerImpl().parse(method, code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // DecompilerImplTest
    /*public static String toCode(CodePointer codePointer) {
        return new CodePointerCodeGenerator().describe(codePointer).toString();
    }*/

    public static CodePointer[] code(Caller caller) {
        try {
            return new CodeLocationDecompilerImpl().decompileCodeLocation(caller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
