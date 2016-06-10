package io.recode.decompile;

import io.recode.annotations.DSL;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilationStateSelector;
import io.recode.model.ElementType;
import io.recode.model.Expression;
import io.recode.util.Sequence;
import io.recode.model.Statement;
import io.recode.util.Stack;

import java.util.Arrays;
import java.util.function.Predicate;

@DSL
public final class DecompilationStateSelectors {

    private static final DecompilationStateSelector AT_LAST_ONE_STATEMENT = new DecompilationStateSelector() {
        @Override
        public boolean select(DecompilationContext context, int byteCode) {
            return !context.getStatements().isEmpty();
        }
    };

    public static DecompilationStateSelector atLeastOneStatement() {
        return AT_LAST_ONE_STATEMENT;
    }

    public static DecompilationStateSelector[] STACK_SIZE_IS_AT_LEAST = {
            stackSizeIsAtLeastUnCached(1),
            stackSizeIsAtLeastUnCached(2),
            stackSizeIsAtLeastUnCached(3)
    };

    public static DecompilationStateSelector stackSizeIsAtLeast(int count) {
        assert count > 0 : "Count must be positive";

        if (count <= STACK_SIZE_IS_AT_LEAST.length) {
            return STACK_SIZE_IS_AT_LEAST[count - 1];
        }

        return stackSizeIsAtLeastUnCached(count);
    }

    public static DecompilationStateSelector elementIsStacked(ElementType elementType) {
        assert elementType != null : "Element type can't be null";
        return (context,byteCode) -> !context.getStack().isEmpty() && context.getStack().peek().getElementType() == elementType;
    }

    @SafeVarargs
    public static DecompilationStateSelector elementsAreStacked(Predicate<Expression>... predicates) {
        assert predicates != null : "Predicates can't be null";

        return new DecompilationStateSelector() {
            @Override
            public boolean select(DecompilationContext context, int byteCode) {
                final Stack<Expression> stack = context.getStack();

                if (stack.size() < predicates.length) {
                    return false;
                }

                int index = 0;

                for (Expression expression : stack.tail(-predicates.length)) {
                    if (!predicates[index++].test(expression)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    public static DecompilationStateSelector elementsAreStacked(Expression... expressions) {
        return new DecompilationStateSelector() {
            @Override
            public boolean select(DecompilationContext context, int byteCode) {
                if (context.getStack().size() < expressions.length) {
                    return false;
                }

                return context.getStack().tail(-expressions.length).equals(Arrays.asList(expressions));
            }
        };
    }

    public static DecompilationStateSelector lastStatementIs(ElementType elementType) {
        return new DecompilationStateSelector() {
            @Override
            public boolean select(DecompilationContext context, int byteCode) {
                final Sequence.SingleElement<Statement> last = context.getStatements().last();

                if (!last.exists()) {
                    return false;
                }

                return last.get().getElementType() == elementType;
            }
        };
    }

    private static DecompilationStateSelector stackSizeIsAtLeastUnCached(int count) {
        return new DecompilationStateSelector() {
            @Override
            public boolean select(DecompilationContext context, int byteCode) {
                return context.getStack().size() >= count;
            }
        };
    }


}
