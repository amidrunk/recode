package io.recode.model;

import io.recode.classfile.ClassFileFormatException;
import io.recode.util.StringReader;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class MethodSignature implements Signature {

    private final String specification;

    private final Type[] parameterTypes;

    private final Type returnType;

    private MethodSignature(String specification, Type[] parameterTypes, Type returnType) {
        this.specification = specification;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public List<Type> getParameterTypes() {
        return Arrays.asList(parameterTypes);
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public boolean test(Method method) {
        assert method != null : "method can't be null";

        return MethodSignature.from(method).equals(this);
    }

    public static MethodSignature create(Type[] parameters, Type returnType) {
        assert parameters != null : "Parameters can't be null";
        assert returnType != null : "Return type can't be null";

        final StringBuilder specification = new StringBuilder();

        specification.append("(");

        for (int i = 0; i < parameters.length; i++) {
            final Type parameter = parameters[i];

            assert parameter != null : "Parameter[" + i + "] is null";

            specification.append(shortSignature(parameter));
        }

        specification.append(")").append(shortSignature(returnType));

        return new MethodSignature(specification.toString(), parameters, returnType);
    }

    public static MethodSignature parse(String spec) {
        assert spec != null && !spec.isEmpty() : "Signature specification can't be null or empty";

        final StringReader reader = new StringReader(spec);
        final List<Type> parameterTypes = new LinkedList<>();

        if (!reader.read("(")) {
            throw new ClassFileFormatException("Signature must start with '(': '" + spec + "'");
        }

        while (true) {
            final int n = reader.peek();

            if (n == -1) {
                throw new ClassFileFormatException("Invalid signature around; expected ')' before EOF");
            }

            if (n == ')') {
                reader.skip(1);
                break;
            }

            parameterTypes.add(readType(reader));
        }

        final Type returnType = readType(reader);

        return new MethodSignature(spec, parameterTypes.toArray(new Type[parameterTypes.size()]), returnType);
    }

    public static MethodSignature from(Method method) {
        assert method != null : "method can't be null";

        return create(method.getParameterTypes(), method.getReturnType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodSignature signature = (MethodSignature) o;

        if (!Arrays.equals(parameterTypes, signature.parameterTypes)) return false;
        if (!returnType.equals(signature.returnType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(parameterTypes);
        result = 31 * result + returnType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return specification;
    }

    public static Type parseType(String string) {
        return readType(new StringReader(string));
    }

    private static String shortSignature(Type type) {
        if (type.equals(int.class)) {
            return "I";
        }

        if (type.equals(void.class)) {
            return "V";
        }

        if (type.equals(boolean.class)) {
            return "Z";
        }

        if (type.equals(long.class)) {
            return "J";
        }

        if (type.equals(double.class)) {
            return "D";
        }

        if (type.equals(byte.class)) {
            return "B";
        }

        if (type.equals(char.class)) {
            return "C";
        }

        if (type.equals(short.class)) {
            return "S";
        }

        if (type.equals(float.class)) {
            return "F";
        }

        if (type instanceof Class) {
            final Class clazz = (Class) type;

            if (clazz.isArray()) {
                return "[" + shortSignature(clazz.getComponentType());
            }
        }

        return "L" + type.getTypeName().replace('.', '/') + ";";
    }

    private static Type readType(StringReader reader) {
        final int shortType = reader.read();

        if (shortType == -1) {
            throw new ClassFileFormatException("Could not read type due to premature EOF");
        }

        switch (shortType) {
            case 'V':
                return void.class;
            case 'B':
                return byte.class;
            case 'C':
                return char.class;
            case 'D':
                return double.class;
            case 'F':
                return float.class;
            case 'I':
                return int.class;
            case 'J':
                return long.class;
            case 'S':
                return short.class;
            case 'Z':
                return boolean.class;
            case 'L': {
                final Optional<String> typeName = reader.readUntil(Pattern.compile(";"));

                if (!typeName.isPresent()) {
                    throw new ClassFileFormatException("Malformed signature around '..." + reader.remainder() + "'; expected ';' after object");
                } else {
                    reader.skip(1);

                    final String actualTypeName = typeName.get().replace('/', '.');

                    try {
                        return Class.forName(actualTypeName);
                    } catch (ClassNotFoundException e) {
                        return new Type() {
                            @Override
                            public String getTypeName() {
                                return actualTypeName;
                            }
                        };
                    }
                }
            }
            case '[': {
                final int shortComponentType = reader.peek();
                final Class componentType = (Class) readType(reader);
                final String arrayClassName;

                if (componentType.isPrimitive()) {
                    arrayClassName = "[" + (char) shortComponentType;
                } else if (componentType.isArray()) {
                    arrayClassName = "[" + componentType.getName();
                } else {
                    arrayClassName = "[L" + componentType.getName() + ";";
                }

                try {
                    return Class.forName(arrayClassName);
                } catch (ClassNotFoundException e) {
                    throw new ClassFileFormatException("Invalid array format: '" + arrayClassName + "'");
                }
            }
            default:
                throw new ClassFileFormatException("Invalid type in signature '" + (char) shortType + "'");
        }
    }


}
