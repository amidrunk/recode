package io.recode.decompile;

import io.recode.model.Expression;
import io.recode.model.Statement;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static io.recode.util.Sequences.emptySequence;
import static io.recode.util.Sequences.sequenceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DecompilationContextQueriesTest {

    private final DecompilationContext decompilationContext = mock(DecompilationContext.class);

    @Test
    public void lastStatementOfShouldReturnNonPresentInstanceForNull() {
        assertFalse(DecompilationContextQueries.lastStatement().from(null).isPresent());
    }

    @Test
    public void lastStatementOfShouldReturnEmptyForEmptyStatementList() {
        when(decompilationContext.getStatements()).thenReturn(emptySequence());

        final Optional<Statement> result = DecompilationContextQueries.lastStatement().from(decompilationContext);

        assertFalse(result.isPresent());
    }

    @Test
    public void lastStatementOfShouldReturnLastStatementWhenAvailable() {
        final Statement statement1 = mock(Statement.class, "statement1");
        final Statement statement2 = mock(Statement.class, "statement2");

        when(decompilationContext.getStatements()).thenReturn(sequenceOf(statement1, statement2));

        final Optional<Statement> result = DecompilationContextQueries.lastStatement().from(decompilationContext);

        assertEquals(Optional.of(statement2), result);
    }

    @Test
    public void previousValueShouldReturnNonPresentResultIfStackIsEmpty() {
        when(decompilationContext.getStackedExpressions()).thenReturn(Collections.emptyList());

        assertFalse(DecompilationContextQueries.previousValue().from(decompilationContext).isPresent());
    }

    @Test
    public void previousValueShouldReturnNonPresentResultIfContextContainsSingleValue() {
        when(decompilationContext.getStackedExpressions()).thenReturn(Arrays.asList(mock(Expression.class)));

        assertFalse(DecompilationContextQueries.previousValue().from(decompilationContext).isPresent());
    }

    @Test
    public void previousValueShouldReturnSecondToLastStackedValue() {
        final Expression value1 = mock(Expression.class);
        final Expression value2 = mock(Expression.class);
        final Expression value3 = mock(Expression.class);

        when(decompilationContext.getStackedExpressions()).thenReturn(Arrays.asList(value1, value2, value3));

        assertEquals(Optional.of(value2), DecompilationContextQueries.previousValue().from(decompilationContext));
    }

    @Test
    public void previousValueShouldReturnNonPresentResultForNull() {
        assertFalse(DecompilationContextQueries.previousValue().from(null).isPresent());
    }

    @Test
    public void currentValueShouldReturnNonPresentResultForNull() {
        assertFalse(DecompilationContextQueries.peek().from(null).isPresent());
    }

    @Test
    public void currentValueShouldReturnNonPresentResultIfStackIsEmpty() {
        when(decompilationContext.hasStackedExpressions()).thenReturn(false);

        assertFalse(DecompilationContextQueries.peek().from(decompilationContext).isPresent());
    }

    @Test
    public void currentValueShouldReturnStackedElement() {
        final Expression expectedExpression = mock(Expression.class);

        when(decompilationContext.hasStackedExpressions()).thenReturn(true);
        when(decompilationContext.peek()).thenReturn(expectedExpression);

        assertEquals(Optional.of(expectedExpression), DecompilationContextQueries.peek().from(decompilationContext));
    }

    @Test
    public void secondToLastStatementShouldReturnSecondToLastStatement() {
        final Statement s1 = mock(Statement.class);
        final Statement s2 = mock(Statement.class);

        when(decompilationContext.getStatements()).thenReturn(sequenceOf(s1, s2));

        assertEquals(Optional.of(s1), DecompilationContextQueries.secondToLastStatement().from(decompilationContext));
    }

    @Test
    public void secondToLastStatementShouldReturnNonPresentOptionalIfStatementDoesNotExist() {
        when(decompilationContext.getStatements()).thenReturn(sequenceOf(mock(Statement.class)));

        assertFalse(DecompilationContextQueries.secondToLastStatement().from(decompilationContext).isPresent());
    }

    @Test
    public void secondToLastStatementShouldReturnNonPresentOptionalForNullContext() {
        assertFalse(DecompilationContextQueries.secondToLastStatement().from(null).isPresent());
    }

    @Test
    public void secondToLastStatementShouldReturnNonPresentOptionalForEmptyStatements() {
        when(decompilationContext.getStatements()).thenReturn(emptySequence());

        assertFalse(DecompilationContextQueries.secondToLastStatement().from(decompilationContext).isPresent());
    }
}