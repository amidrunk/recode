package io.recode.model;

import io.recode.model.*;
import io.recode.annotations.DSL;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@DSL
public final class ModelQueries {

    public static Predicate<Expression> ofType(ElementType elementType) {
        assert elementType != null : "Element type can't be null";

        return expression -> expression != null && expression.getElementType() == elementType;
    }

    public static <E> Predicate<E> ofType(Class<E> type) {
        assert type != null : "Type can't be null";

        return type::isInstance;
    }

    public static Predicate<VariableAssignment> isAssignmentTo(LocalVariableReference localVariable) {
        assert localVariable != null : "Local variable can't be null";

        return instance -> instance != null
                && instance.getVariableName().equals(localVariable.getName())
                && instance.getVariableType().equals(localVariable.getType())
                && instance.getVariableIndex() == localVariable.getIndex();
    }

    public static Predicate<TypeCast> isCastTo(Class<?> targetType) {
        assert targetType != null : "Target type can't be null";
        return instance -> instance.getType().equals(targetType);
    }

    public static ModelQuery<TypeCast, Expression> castValue() {
        return cast -> cast == null ? Optional.empty() : Optional.of(cast.getValue());
    }

    public static ModelQuery<VariableAssignment, Expression> assignedValue() {
        return from -> from == null ? Optional.<Expression>empty() : Optional.of(from.getValue());
    }

    public static Predicate<VariableAssignment> assignedVariableTypeIs(Class<?> type) {
        assert type != null : "Type can't be null";

        return new Predicate<VariableAssignment>() {
            @Override
            public boolean test(VariableAssignment variableAssignment) {
                return variableAssignment != null && variableAssignment.getVariableType().equals(type);
            }
        };
    }

    public static ModelQuery<BinaryOperator, Expression> leftOperand() {
        return o -> o == null ? Optional.<Expression>empty() : Optional.of(o.getLeftOperand());
    }

    public static ModelQuery<BinaryOperator, Expression> rightOperand() {
        return o -> o == null ? Optional.<Expression>empty() : Optional.of(o.getRightOperand());
    }

    public static ModelQuery<Branch, Expression> leftComparativeOperand() {
        return b -> b == null ? Optional.<Expression>empty() : Optional.of(b.getLeftOperand());
    }

    public static ModelQuery<Branch, Expression> rightComparativeOperand() {
        return b -> b == null ? Optional.<Expression>empty() : Optional.of(b.getRightOperand());
    }

    public static ModelQuery<BinaryOperator, OperatorType> operatorType() {
        return o -> o == null ? Optional.<OperatorType>empty() : Optional.of(o.getOperatorType());
    }

    public static Predicate<Branch> operatorTypeIs(OperatorType operatorType) {
        return new Predicate<Branch>() {
            @Override
            public boolean test(Branch branch) {
                return branch.getOperatorType() == operatorType;
            }
        };
    }

    public static <S> Predicate<S> equalTo(S expectedValue) {
        return s -> Objects.equals(s, expectedValue);
    }

    public static <E extends Expression> ModelQuery<E, Type> runtimeType() {
        return e -> e == null ? Optional.<Type>empty() : Optional.of(e.getType());
    }

    public static <E extends Expression> Predicate<E> ofRuntimeType(Class<?> type) {
        assert type != null : "Type can't be null";
        return e -> e != null && e.getType().equals(type);
    }

    public static Predicate<Increment> affixIsUndefined() {
        return source -> source != null && source.getAffix() == Affix.UNDEFINED;
    }

    public static <E extends Element> ModelQuery<E, E> value() {
        return Optional::ofNullable;
    }


    public static<T extends Element> Predicate<T> any() {
        return expression -> true;
    }

    public static Predicate<FieldReference> field(Predicate<Expression> instance, String name, Type type) {
        return e -> e.getTargetInstance().map(instance::test).orElse(false)
                && Objects.equals(e.getFieldName(), name)
                && Objects.equals(e.getFieldType(), type);
    }
}
