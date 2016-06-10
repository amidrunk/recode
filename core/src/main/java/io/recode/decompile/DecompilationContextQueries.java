package io.recode.decompile;

import io.recode.decompile.DecompilationContext;
import io.recode.model.Expression;
import io.recode.model.ModelQuery;
import io.recode.util.Sequence;
import io.recode.model.Statement;

import java.util.List;
import java.util.Optional;

public final class DecompilationContextQueries {

    private static final ModelQuery<DecompilationContext, Statement> LAST_DECOMPILED_STATEMENT = new ModelQuery<DecompilationContext, Statement>() {
        @Override
        public Optional<Statement> from(DecompilationContext context) {
            if (context == null) {
                return Optional.empty();
            }

            return context.getStatements().last().optional();
        }
    };

    private static final ModelQuery<DecompilationContext, Expression> PREVIOUS_VALUE = new ModelQuery<DecompilationContext, Expression>() {
        @Override
        public Optional<Expression> from(DecompilationContext context) {
            if (context == null) {
                return Optional.empty();
            }

            final List<Expression> stack = context.getStackedExpressions();

            if (stack.size() < 2) {
                return Optional.empty();
            }

            return Optional.of(stack.get(stack.size() - 2));
        }
    };

    private static final ModelQuery<DecompilationContext, Expression> CURRENT_VALUE = new ModelQuery<DecompilationContext, Expression>() {
        @Override
        public Optional<Expression> from(DecompilationContext from) {
            return from == null || !from.hasStackedExpressions()
                    ? Optional.<Expression>empty()
                    : Optional.of(from.peek());
        }
    };

    public static final ModelQuery<DecompilationContext, Statement> SECOND_TO_LAST = new ModelQuery<DecompilationContext, Statement>() {
        @Override
        public Optional<Statement> from(DecompilationContext from) {
            if (from == null) {
                return Optional.empty();
            }

            final Sequence.SingleElement<Statement> last = from.getStatements().last();

            if (!last.exists()) {
                return Optional.empty();
            }

            final Sequence.SingleElement<Statement> previous = last.previous();

            if (!previous.exists()) {
                return Optional.empty();
            }

            return Optional.of(previous.get());
        }
    };

    public static ModelQuery<DecompilationContext, Statement> lastStatement() {
        return LAST_DECOMPILED_STATEMENT;
    }

    public static ModelQuery<DecompilationContext, Statement> secondToLastStatement() {
        return SECOND_TO_LAST;
    }

    public static ModelQuery<DecompilationContext, Expression> previousValue() {
        return PREVIOUS_VALUE;
    }

    public static ModelQuery<DecompilationContext, Expression> peek() {
        return CURRENT_VALUE;
    }

}
