package io.recode.codegeneration.impl;

import io.recode.classfile.ByteCode;
import io.recode.codegeneration.*;
import io.recode.decompile.CodePointer;
import io.recode.decompile.CodeStream;
import io.recode.model.*;
import io.recode.annotations.DSL;
import io.recode.util.Methods;
import io.recode.classfile.ClassFile;
import io.recode.classfile.Method;
import io.recode.classfile.MethodReference;
import io.recode.classfile.impl.MethodReferenceImpl;
import io.recode.decompile.impl.InputStreamCodeStream;
import io.recode.model.MethodSignature;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;

public final class JavaSyntaxCodeGeneration implements CodeGeneratorDelegation {

    public static CodeGeneratorConfiguration configuration() {
        final CodeGeneratorConfigurer configurer = SimpleCodeGeneratorConfiguration.configurer();
        new JavaSyntaxCodeGeneration().configure(configurer);
        return configurer.configuration();
    }

    public void configure(CodeGeneratorConfigurer configurer) {
        assert configurer != null : "Configurer can't be null";

        configurer.on(ElementSelector.forType(ElementType.RETURN)).then(ret());
        configurer.on(ElementSelector.forType(ElementType.CONSTANT)).then(constant());
        configurer.on(ElementSelector.forType(ElementType.RETURN_VALUE)).then(returnValue());
        configurer.on(ElementSelector.forType(ElementType.CONSTANT)).then(constant());
        configurer.on(ElementSelector.forType(ElementType.VARIABLE_REFERENCE)).then(variableReference());
        configurer.on(ElementSelector.forType(ElementType.ARRAY_STORE)).then(arrayStore());
        configurer.on(ElementSelector.forType(ElementType.CAST)).then(typeCast());
        configurer.on(ElementSelector.forType(ElementType.ARRAY_LOAD)).then(arrayLoad());
        configurer.on(ElementSelector.forType(ElementType.ALLOCATE)).then(allocateInstance());
        configurer.on(ElementSelector.forType(ElementType.INCREMENT)).then(increment());
        configurer.on(ElementSelector.forType(ElementType.BINARY_OPERATOR)).then(binaryOperator());

        configurer.on(selectBooleanBoxCall()).then(boxBoolean());
        configurer.on(selectPrimitiveBoxCall()).then(primitiveBoxCall());
        configurer.on(selectDSLMethodCall()).then(dslMethodCall());
        configurer.on(selectInnerClassFieldAccess()).then(innerClassFieldAccess());
        configurer.on(selectInstanceMethodCall()).then(instanceMethodCall());
        configurer.on(selectStaticMethodCall()).then(staticMethodCall());
        configurer.on(selectUninitializedNewArray()).then(newUninitializedArray());
        configurer.on(selectInitializedNewArray()).then(newInitializedArray());
    }

    public static CodeGeneratorDelegate<BinaryOperator> binaryOperator() {
        return new CodeGeneratorDelegate<BinaryOperator>() {
            @Override
            public void apply(CodeGenerationContext context, CodePointer<BinaryOperator> codePointer, PrintWriter out) {
                final String operator;

                switch (codePointer.getElement().getOperatorType()) {
                    case PLUS:
                        operator = "+";
                        break;
                    case NE:
                        operator = "!=";
                        break;
                    case MINUS:
                        operator = "-";
                        break;
                    case MULTIPLY:
                        operator = "*";
                        break;
                    case DIVIDE:
                        operator = "/";
                        break;
                    case MODULO:
                        operator = "%";
                        break;
                    case EQ:
                        operator = "==";
                        break;
                    case GE:
                        operator = ">=";
                        break;
                    case LT:
                        operator = "<";
                        break;
                    case GT:
                        operator = ">";
                        break;
                    case LE:
                        operator = "<=";
                        break;
                    case AND:
                        operator = "&&";
                        break;
                    case OR:
                        operator = "||";
                        break;
                    case LSHIFT:
                        operator = "<<";
                        break;
                    case RSHIFT:
                        operator = ">>";
                        break;
                    case UNSIGNED_RSHIFT:
                        operator = ">>>";
                        break;
                    case BITWISE_AND:
                        operator = "&";
                        break;
                    case BITWISE_OR:
                        operator = "|";
                        break;
                    case XOR:
                        operator = "^";
                        break;
                    default:
                        throw new CodeGenerationException("Operator type is not supported: "
                                + codePointer.getElement().getOperatorType());
                }

                context.delegate(codePointer.forElement(codePointer.getElement().getLeftOperand()));
                out.append(" ").append(operator).append(" ");
                context.delegate(codePointer.forElement(codePointer.getElement().getRightOperand()));
            }
        };
    }

    public static CodeGeneratorDelegate<Increment> increment() {
        return (context,codePointer,out) -> {
            final Increment increment = codePointer.getElement();
            final Affix affix = increment.getAffix();
            final boolean positive = (((Number) increment.getValue().as(Constant.class).getConstant()).doubleValue() > 0);

            if (affix == Affix.PREFIX) {
                out.append(positive ? "++" : "--");
            }

            context.delegate(codePointer.forElement(codePointer.getElement().getLocalVariable()));

            if (affix == Affix.POSTFIX) {
                out.append(positive ? "++" : "--");
            }
        };
    }

    /**
     * Extension for the {@link ElementType#ALLOCATE} model element. This element
     * is discarded during decompilation, since it doesn't correspond to a Java syntax element. However,
     * generation of the element is required to generate code for intermediate decompilations, e.g. during
     * debug.
     *
     * @return Code generator extension for {@link ElementType#ALLOCATE}, which
     * corresponds to the {@link ByteCode#new_} byte code.
     */
    public static CodeGeneratorDelegate<InstanceAllocation> allocateInstance() {
        return (context,codePointer,out) -> {
            final InstanceAllocation instanceAllocation = codePointer.getElement();

            out.append("new ").append(context.getCodeStyle().getTypeName(instanceAllocation.getType())).append("<uninitialized>");
        };
    }

    /**
     * Handles loading of array elements. This corresponds to the byte code {@link ByteCode#aaload}
     * and the model element {@link ElementType#ARRAY_LOAD}.
     *
     * @return A code generator extension for handling array element access.
     */
    public static CodeGeneratorDelegate<ArrayLoad> arrayLoad() {
        return (context,codePointer,out) -> {
            final ArrayLoad arrayLoad = codePointer.getElement();

            context.delegate(codePointer.forElement(arrayLoad.getArray()));
            out.append("[");
            context.delegate(codePointer.forElement(arrayLoad.getIndex()));
            out.append("]");
        };
    }

    /**
     * Extension for a type cast, i.e. the <code>{@link ByteCode#checkcast}</code> instruction,
     * which is matched by a {@link ElementType#CAST} element. The output of the extension
     * is <code>(typeName)delegate(value)</code> where type name is composed from the active code style.
     *
     * @return An extension that handles the {@link ElementType#CAST} element.
     */
    public static CodeGeneratorDelegate<TypeCast> typeCast() {
        return (context,codePointer,out) -> {
            final TypeCast typeCast = codePointer.getElement();
            final String targetTypeName = context.getCodeStyle().getTypeName(typeCast.getType());

            out.append("(").append(targetTypeName).append(")");
            context.delegate(codePointer.forElement(typeCast.getValue()));
        };
    }

    /**
     * <p>
     * Returns whether or not a method call is a field access of an inner class. Directly accessed fields
     * of inner classes are not implemented using getfield, but rather a private accessor method is
     * automatically generated by the compiler. For example, assume the following code:
     * </p>
     * <p>
     * <pre><code>
     * public class Outer {
     *
     *     private Inner inner = new Inner();
     *
     *     public String getString() { return inner.str; }
     *
     *     public static class Inner {
     *         private String str;
     *     }
     *
     * }
     * </code></pre>
     * </p>
     * <p>
     * The reference to <code>inner.str</code> in the example above will not be implemented as
     * <ul style="diamond:none;">
     *     <li>aload_0</li>
     *     <li>getfield inner</li>
     *     <li>getfield str</li>
     * </ul>
     * </p>
     * Rather a method will be generated in the inner class, resulting in the following code:
     * <pre><code>
     * public class Outer {
     *     public Inner inner;
     *
     *     public String getString() {
     *         return Inner.access$100(inner);
     *     }
     *
     *     public static class Inner {
     *         private String str;
     *
     *         private static String access$100(Inner inner) {
     *             return inner.str;
     *         }
     *     }
     * }
     * </code></pre>
     *
     * @return An element selector that selects a method call iff it represents an inner class field access.
     */
    public static ElementSelector<MethodCall> selectInnerClassFieldAccess() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isStaticMethodCall().and(codePointer -> {
            final MethodCall methodCall = codePointer.getElement();

            if (!methodCall.getMethodName().startsWith("access$")) {
                return false;
            }

            final List<Expression> parameters = methodCall.getParameters();

            if (parameters.size() != 1 && parameters.size() != 2) {
                return false;
            }

            final Expression parameterValue = parameters.get(0);

            if (!parameterValue.getType().equals(methodCall.getTargetType())) {
                return false;
            }

            return true;
        }));
    }

    public static CodeGeneratorDelegate<MethodCall> innerClassFieldAccess() {
        return (context, codePointer, out) -> {
            final MethodCall methodCall = codePointer.getElement();
            final ClassFile innerClassClassFile = context.getClassFileResolver().resolveClassFile(methodCall.getTargetType());
            final Method method = innerClassClassFile.getMethods().stream()
                    .filter(m -> m.getName().equals(methodCall.getMethodName()))
                    .findFirst()
                    .orElseThrow(() -> new CodeGenerationException("Could not find accessor method "
                            + methodCall.getTargetType().getTypeName() + "." + methodCall.getMethodName() + " in class file"));

            final Element[] methodElements;

            try (CodeStream code = new InputStreamCodeStream(method.getCode().getCode())) {
                methodElements = context.getDecompiler().parse(method, code);
            } catch (IOException e) {
                throw new CodeGenerationException("Failed to decompile method '" + method.getName()
                        + "' in class '" + innerClassClassFile.getName() + "'", e);
            }

            context.delegate(codePointer.forElement(methodCall.getParameters().get(0)));
            out.append(".");

            if (methodCall.getParameters().size() == 1) {
                out.append(((FieldReference) ((ReturnValue) methodElements[0]).getValue()).getFieldName());
            } else {
                out.append(((FieldAssignment) methodElements[0]).getFieldReference().getFieldName()).append(" = ");
                context.delegate(codePointer.forElement(methodCall.getParameters().get(1)));
            }
        };
    }

    /**
     * Selects a {@link NewArray} with no initializers specified. Will create
     * a new array with default values.
     *
     * @return A selector that selects {@link NewArray} elements without
     * and initializers.
     */
    public static ElementSelector<NewArray> selectUninitializedNewArray() {
        return ElementSelector.<NewArray>forType(ElementType.NEW_ARRAY)
                .where(cp -> cp.getElement().getInitializers().isEmpty());
    }

    /**
     * Selects {@link NewArray} elements with initializers specified.
     *
     * @return A selector that selects initialized new arrays.
     */
    public static ElementSelector<NewArray> selectInitializedNewArray() {
        return ElementSelector.<NewArray>forType(ElementType.NEW_ARRAY)
                .where(cp -> !cp.getElement().getInitializers().isEmpty());
    }

    /**
     * Code generator extension for uninitialized new arrays. Will generate code on the form
     * <code>new T:ClassName[n:Expression]</code>.
     *
     * @return A code generator extension that handles {@link NewArray}-elements
     * that has no initializers.
     */
    public static CodeGeneratorDelegate<NewArray> newUninitializedArray() {
        return (context, codePointer, out) -> {
            final NewArray newArray = codePointer.getElement();

            out.append("new ").append(context.getCodeStyle().getTypeName(newArray.getComponentType())).append("[");
            context.delegate(codePointer.forElement(newArray.getLength()));
            out.append("]");
        };
    }

    public static CodeGeneratorDelegate<NewArray> newInitializedArray() {
        return (context, codePointer, out) -> {
            final NewArray newArray = codePointer.getElement();

            out.append("new ").append(context.getCodeStyle().getTypeName(newArray.getComponentType())).append("[] { ");

            for (Iterator<ArrayInitializer> i = newArray.getInitializers().iterator(); i.hasNext(); ) {
                context.delegate(codePointer.forElement(i.next().getValue()));

                if (i.hasNext()) {
                    out.append(", ");
                }
            }

            out.append(" }");
        };
    }

    /**
     * Extension for assignment to array element.
     *
     * @return A code generator extension that handles {@link ArrayStore}-elements.
     */
    public static CodeGeneratorDelegate<ArrayStore> arrayStore() {
        return (context, codePointer, out) -> {
            final ArrayStore arrayStore = codePointer.getElement();

            context.delegate(codePointer.forElement(arrayStore.getArray()));
            out.append("[");
            context.delegate(codePointer.forElement(arrayStore.getIndex()));
            out.append("] = ");
            context.delegate(codePointer.forElement(arrayStore.getValue()));
        };
    }

    public static CodeGeneratorDelegate<Constant> constant() {
        return (context, codePointer, out) -> {
            final Type type = codePointer.getElement().getType();
            final Object constant = codePointer.getElement().getConstant();

            if (type.equals(String.class)) {
                out.append('"').append(String.valueOf(constant)).append('"');
            } else if (type.equals(long.class)) {
                out.append(String.valueOf(constant)).append('L');
            } else if (type.equals(float.class)) {
                out.append(String.valueOf(constant)).append('f');
            } else if (type.equals(Class.class)) {
                out.append(context.getCodeStyle().getTypeName((Type) constant)).append(".class");
            } else {
                out.append(String.valueOf(constant));
            }
        };
    }

    public static CodeGeneratorDelegate<Return> ret() {
        return (context, codePointer, out) -> {
            out.append("return");
        };
    }

    public static CodeGeneratorDelegate<LocalVariableReference> variableReference() {
        return (context, codePointer, out) -> {
            out.append(codePointer.getElement().getName());
        };
    }

    public static CodeGeneratorDelegate<ReturnValue> returnValue() {
        return (context, codePointer, out) -> {
            out.append("return ");
            context.delegate(codePointer.forElement(codePointer.getElement().getValue()));
        };
    }

    public static Predicate<CodePointer<MethodCall>> isStaticMethodCall() {
        return cp -> cp.getElement().getTargetInstance() == null;
    }

    public static Predicate<CodePointer<MethodCall>> isInstanceMethodCall() {
        return cp -> cp.getElement().getTargetInstance() != null;
    }

    public static ElementSelector<MethodCall> selectStaticMethodCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isStaticMethodCall());
    }

    public static ElementSelector<MethodCall> selectInstanceMethodCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isInstanceMethodCall());
    }

    /**
     * Creates a selector for the boolean box call that occurs when a boolean is assigned to / used
     * in place of a java.lang.Boolean instance. The selector will match a static method call against
     * java.lang.Boolean.valueOf with signature (Z)Ljava/lang/Boolean;
     *
     * @return An element selector for the boolean box call.
     */
    public static ElementSelector<MethodCall> selectBooleanBoxCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isStaticMethodCall().and(cp -> {
            final MethodCall methodCall = cp.getElement();

            if (!methodCall.getTargetType().equals(Boolean.class)) {
                return false;
            }

            if (!methodCall.getSignature().toString().equals("(Z)Ljava/lang/Boolean;")) {
                return false;
            }

            final Expression parameter = methodCall.getParameters().get(0);

            if (!parameter.getType().equals(int.class) && !parameter.getType().equals(boolean.class)) {
                return false;
            }

            return true;
        }));
    }

    /**
     * Extension for boxing of a boolean. Boxing is implicit in the language, but will result in
     * a call to <code>java.lang.Boolean.valueOf(boolean)</code> in the byte code. This extension
     * will generate code that uses implicit boxing.
     *
     * @return A code generator extension that translates Boolean.valueOf(boolean) to an
     * implicit boolean boxing.
     */
    public static CodeGeneratorDelegate<MethodCall> boxBoolean() {
        return (context, codePointer, out) -> {
            final Expression parameter = codePointer.getElement().getParameters().get(0);

            if (parameter.getElementType() == ElementType.CONSTANT && parameter.getType().equals(int.class)) {
                if (parameter.as(Constant.class).getConstant().equals(0)) {
                    out.print("false");
                } else {
                    out.print("true");
                }
            } else {
                context.delegate(codePointer.forElement(parameter));
            }
        };
    }

    /**
     * Extension for handling of static methods, i.e. method that are invoked on a class rather
     * than on a target instance. Parameters will be delegated to the context, i.e. the generation
     * will be className(delegate(param_1), ..., delegate(param_n)) where the class name is
     * retrieved from the code style (allows for qualified or unqualified references).
     *
     * @return A code generator extension for static calls.
     */
    public static CodeGeneratorDelegate<MethodCall> staticMethodCall() {
        return (context, codePointer, out) -> {
            final MethodCall methodCall = codePointer.getElement();

            out.append(context.getCodeStyle().getTypeName(methodCall.getTargetType()))
                    .append('.');

            appendMethodCall(context, codePointer, out);
        };
    }

    /**
     * Extensions for plain instance method calls. This extension will delegate code generation for
     * the instance that is target of the method call and the parameters of the method call:
     * delegate(targetInstance).methodName(delegate(param_1), ..., delegate(param_n))
     *
     * @return A code generator extension for method invocations on an instance.
     */
    public static CodeGeneratorDelegate<MethodCall> instanceMethodCall() {
        return (context, codePointer, out) -> {
            final MethodCall methodCall = codePointer.getElement();
            final Expression targetInstance = methodCall.getTargetInstance();

            if (!context.getCodeStyle().shouldOmitThis()
                    || targetInstance.getElementType() != ElementType.VARIABLE_REFERENCE
                    || !((LocalVariableReference) targetInstance).getName().equals("this")) {
                context.delegate(codePointer.forElement(targetInstance));
                out.append('.');
            }

            appendMethodCall(context, codePointer, out);
        };
    }

    /**
     * Returns a predicate that determines whether or not a method call is a DSL method call. This
     * is true iff the target type is (1) static and (2) the target type has the @DSL annotation.
     *
     * @return A predicate that can test whether or not a method call element represents a DSL call.
     */
    public static Predicate<CodePointer<MethodCall>> isDSLMethodCall() {
        return codePointer -> {
            final Type targetType = codePointer.getElement().getTargetType();

            if (!(targetType instanceof Class)) {
                return false;
            }

            return ((Class) targetType).getAnnotation(DSL.class) != null;
        };
    }

    public static Predicate<CodePointer<MethodCall>> isMethodCall() {
        return new Predicate<CodePointer<MethodCall>>() {
            @Override
            public boolean test(CodePointer<MethodCall> codePointer) {
                return codePointer.getElement().getElementType() == ElementType.METHOD_CALL;
            }
        };
    }

    /**
     * Creates an element selector that matches method calls that are (1) static and (2) called on
     * a type that has the @DSL annotation. See {@link JavaSyntaxCodeGeneration#isDSLMethodCall()}.
     *
     * @return A selector that selects DSL method calls.
     */
    public static ElementSelector<MethodCall> selectDSLMethodCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isDSLMethodCall());
    }

    /**
     * Creates a code generator extension that handles DSL method calls. DSL method calls will omit
     * the target type.
     *
     * TODO: Not a core delegate
     *
     * @return A code generator extension that handles DSL method calls.
     */
    public static CodeGeneratorDelegate<MethodCall> dslMethodCall() {
        return JavaSyntaxCodeGeneration::appendMethodCall;
    }

    private static final Set<MethodReference> PRIMITIVE_BOX_METHODS = new HashSet<>(Arrays.<MethodReference>asList(
            new MethodReferenceImpl(Byte.class, "valueOf", MethodSignature.parse("(B)Ljava/lang/Byte;")),
            new MethodReferenceImpl(Short.class, "valueOf", MethodSignature.parse("(S)Ljava/lang/Short;")),
            new MethodReferenceImpl(Character.class, "valueOf", MethodSignature.parse("(C)Ljava/lang/Character;")),
            new MethodReferenceImpl(Integer.class, "valueOf", MethodSignature.parse("(I)Ljava/lang/Integer;")),
            new MethodReferenceImpl(Long.class, "valueOf", MethodSignature.parse("(J)Ljava/lang/Long;")),
            new MethodReferenceImpl(Float.class, "valueOf", MethodSignature.parse("(F)Ljava/lang/Float;")),
            new MethodReferenceImpl(Double.class, "valueOf", MethodSignature.parse("(D)Ljava/lang/Double;"))
    ));

    public static Predicate<CodePointer<MethodCall>> isPrimitiveBoxCall() {
        return isStaticMethodCall().and(cp -> {
            final MethodCall methodCall = cp.getElement();

            return PRIMITIVE_BOX_METHODS.contains(new MethodReferenceImpl(
                    methodCall.getTargetType(),
                    methodCall.getMethodName(),
                    methodCall.getSignature()));
        });
    }

    public static ElementSelector<MethodCall> selectPrimitiveBoxCall() {
        return ElementSelector.<MethodCall>forType(ElementType.METHOD_CALL).where(isPrimitiveBoxCall());
    }

    public static CodeGeneratorDelegate<MethodCall> primitiveBoxCall() {
        return (context, codePointer, out) -> {
            context.delegate(codePointer.forElement(codePointer.getElement().getParameters().get(0)));
        };
    }

    /**
     * Appends a method call to the provided print writer. The target of the method call is assumed to have been
     * appended; this method will append the method name and the parameter list.
     *
     * @param context     The context in which the method call code is generated.
     * @param codePointer The code pointer referencing the method call.
     * @param out         The print writer to which the generated code is written.
     */
    private static void appendMethodCall(CodeGenerationContext context, CodePointer<MethodCall> codePointer, PrintWriter out) {
        final MethodCall methodCall = codePointer.getElement();

        out.append(methodCall.getMethodName()).append('(');

        for (Iterator<Expression> i = methodCall.getParameters().iterator(); i.hasNext(); ) {
            final Expression parameter = i.next();

            if (!i.hasNext()) {
                if (parameter.getElementType() == ElementType.NEW_ARRAY) {
                    if (isVarargsMethodCall(methodCall)) {
                        final NewArray newArray = parameter.as(NewArray.class);

                        for (final Iterator<ArrayInitializer> i2 = newArray.getInitializers().iterator(); i2.hasNext(); ) {
                            context.delegate(codePointer.forElement(i2.next().getValue()));

                            if (i2.hasNext()) {
                                out.print(", ");
                            }
                        }

                        break;
                    }
                }
            }

            context.delegate(codePointer.forElement(parameter));

            if (i.hasNext()) {
                out.append(", ");
            }
        }

        out.append(')');
    }

    /**
     * Checks if the provided method call is a varargs call. This is true only if (1) the last parameter of
     * the method call is an array and (2) the method is a varargs method.
     *
     * @param methodCall The method call to check.
     * @return Whether or not the method call is a varargs method call.
     */
    private static boolean isVarargsMethodCall(MethodCall methodCall) {
        final Optional<java.lang.reflect.Method> methodOptional = Methods.findMethodForNameAndSignature(
                (Class) methodCall.getTargetType(),
                methodCall.getMethodName(),
                methodCall.getSignature());

        if (methodOptional.isPresent()) {
            if (methodOptional.get().isVarArgs()) {
                return true;
            }
        }

        return false;
    }
}
