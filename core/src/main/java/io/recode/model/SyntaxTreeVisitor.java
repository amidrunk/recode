package io.recode.model;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public final class SyntaxTreeVisitor {

    public interface Walker {

        void abort();

    }

    public interface Callback {

        void visit(Walker walker, Element element);

    }

    public static Optional<Element> search(Element[] elements, Predicate<Element> predicate) {
        assert elements != null : "Elements can't be null";
        assert predicate != null : "Predicate can't be null";

        for (Element element : elements) {
            final Optional<Element> result = search(element, predicate);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    public static Optional<Element> search(Element element, Predicate<Element> predicate) {
        assert element != null : "Element can't be null";
        assert predicate != null : "Predicate can't be null";

        final AtomicReference<Optional<Element>> result = new AtomicReference<>(Optional.<Element>empty());

        visit(element, (w, e) -> {
            if (predicate.test(e)) {
                result.set(Optional.of(e));
                w.abort();
            }
        });

        return result.get();
    }

    public static void visit(Element element, Callback callback) {
        try {
            visitNonAbortable(element, callback, () -> { throw new Abort(); });
        } catch (Abort abort) {
        }
    }

    private static void visitNonAbortable(Element element, Callback callback, Walker walker) {
        assert element != null : "Element can't be null";
        assert callback != null : "Callback can't be null";


        switch (element.getElementType()) {
            case RETURN_VALUE: {
                visitNonAbortable(element.as(ReturnValue.class).getValue(), callback, walker);
                break;
            }
            case BINARY_OPERATOR: {
                final BinaryOperator binaryOperator = element.as(BinaryOperator.class);
                visitNonAbortable(binaryOperator.getLeftOperand(), callback, walker);
                visitNonAbortable(binaryOperator.getRightOperand(), callback, walker);
                break;
            }
            case METHOD_CALL: {
                final MethodCall methodCall = element.as(MethodCall.class);

                if (methodCall.getTargetInstance() != null) {
                    visitNonAbortable(methodCall.getTargetInstance(), callback, walker);
                }

                for (Expression parameter : methodCall.getParameters()) {
                    visitNonAbortable(parameter, callback, walker);
                }

                break;
            }
            case FIELD_REFERENCE: {
                final FieldReference fieldReference = element.as(FieldReference.class);

                if (fieldReference.getTargetInstance().isPresent()) {
                    visitNonAbortable(fieldReference.getTargetInstance().get(), callback, walker);
                }

                break;
            }
            case VARIABLE_ASSIGNMENT: {
                final VariableAssignment variableAssignment = element.as(VariableAssignment.class);

                visitNonAbortable(variableAssignment.getValue(), callback, walker);

                break;
            }
            case LAMBDA: {
                final Lambda lambda = element.as(Lambda.class);

                if (lambda.getSelf().isPresent()) {
                    visitNonAbortable(lambda.getSelf().get(), callback, walker);
                }

                break;
            }
            case BRANCH: {
                final Branch branch = element.as(Branch.class);

                visitNonAbortable(branch.getLeftOperand(), callback, walker);
                visitNonAbortable(branch.getRightOperand(), callback, walker);
                break;
            }
            case NEW: {
                element.as(NewInstance.class).getParameters().forEach(e -> visitNonAbortable(e, callback, walker));
                break;
            }
            case NEW_ARRAY: {
                final NewArray newArray = element.as(NewArray.class);

                visitNonAbortable(newArray.getLength(), callback, walker);
                newArray.getInitializers().forEach(i -> visitNonAbortable(i.getValue(), callback, walker));
                break;
            }
            case ARRAY_STORE: {
                final ArrayStore arrayStore = element.as(ArrayStore.class);

                visitNonAbortable(arrayStore.getArray(), callback, walker);
                visitNonAbortable(arrayStore.getIndex(), callback, walker);
                visitNonAbortable(arrayStore.getValue(), callback, walker);

                break;
            }
            case FIELD_ASSIGNMENT: {
                final FieldAssignment assignment = element.as(FieldAssignment.class);

                visitNonAbortable(assignment.getFieldReference(), callback, walker);
                visitNonAbortable(assignment.getValue(), callback, walker);

                break;
            }
            case CAST: {
                final TypeCast typeCast = element.as(TypeCast.class);

                visitNonAbortable(typeCast.getValue(), callback, walker);

                break;
            }
            case ARRAY_LOAD: {
                final ArrayLoad arrayLoad = element.as(ArrayLoad.class);

                visitNonAbortable(arrayLoad.getArray(), callback, walker);
                visitNonAbortable(arrayLoad.getIndex(), callback, walker);

                break;
            }
        }

        callback.visit(walker, element);
    }

    private static final class Abort extends RuntimeException {}

}
