package io.recode.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static io.recode.model.AST.*;
import static io.recode.model.ModelQueries.assignedValue;
import static io.recode.model.ModelQueries.equalTo;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ModelQueryTest {

    private final ModelQuery<List<Statement>, Statement> firstInList = from -> from.isEmpty() ? Optional.<Statement>empty() : Optional.of(from.get(0));

    @Test
    public void asShouldNotAcceptNullType() {
        assertThrown(() -> firstInList.as(null), AssertionError.class);
    }

    @Test
    public void asShouldCastQueriedInstance() {
        final VariableAssignment expectedElement = mock(VariableAssignment.class);
        final Optional<VariableAssignment> result = firstInList.as(VariableAssignment.class).from(Arrays.asList(expectedElement));

        assertEquals(Optional.of(expectedElement), result);
    }

    @Test
    public void asShouldReturnNonPresentOptionalIfTargetIsNonPresent() {
        final Optional<VariableAssignment> result = firstInList.as(VariableAssignment.class).from(Collections.emptyList());

        assertFalse(result.isPresent());
    }

    @Test
    public void asShouldReturnNonPresentOptionalIfTargetIsOfIncorrectType() {
        final Optional<VariableAssignment> result = firstInList.as(VariableAssignment.class).from(Arrays.asList(mock(Statement.class)));

        assertFalse(result.isPresent());
    }

    @Test
    public void whereShouldNotAcceptNullPredicate() {
        assertThrown(() -> firstInList.where(null), AssertionError.class);
    }

    @Test
    public void queryShouldReturnEmptyResultIfPredicateDoesNotMatch() {
        final Optional<Statement> result = firstInList.where(statement -> false).from(Arrays.asList(mock(Statement.class)));

        assertFalse(result.isPresent());
    }

    @Test
    public void queryShouldReturnResultIfPredicateMatches() {
        final Statement expectedStatement = mock(Statement.class);
        final Optional<Statement> result = firstInList.where(statement -> true).from(Arrays.asList(expectedStatement));

        assertEquals(Optional.of(expectedStatement), result);
    }

    @Test
    public void getShouldNotAcceptNullModelQuery() {
        assertThrown(() -> firstInList.get(null), AssertionError.class);
    }

    @Test
    public void getShouldReturnQueryThatRetrievesFurtherValues() {
        final Statement statement = mock(Statement.class);

        when(statement.getElementType()).thenReturn(ElementType.RETURN_VALUE);

        final Optional<ElementType> result = firstInList
                .get(e -> Optional.of(e.getElementType()))
                .from(Arrays.asList(statement));

        assertEquals(Optional.of(ElementType.RETURN_VALUE), result);
    }

    @Test
    public void getShouldReturnNonPresentOptionalIfOriginalSourceIsNotPresent() {
        final Optional<ElementType> result = firstInList
                .get(e -> Optional.of(e.getElementType()))
                .from(Collections.emptyList());

        assertFalse(result.isPresent());
    }

    @Test
    public void isShouldNotAcceptNullPredicate() {
        assertThrown(() -> firstInList.is(null), AssertionError.class);
    }

    @Test
    public void isShouldReturnPredicateOnResult() {
        final Predicate<List<Statement>> predicate = firstInList.is(s -> s.getElementType() == ElementType.RETURN);

        assertTrue(predicate.test(Arrays.<Statement>asList(AST.$return(), AST.$return(AST.constant(1)))));
        assertFalse(predicate.test(Arrays.<Statement>asList(AST.$return(AST.constant(1)), AST.$return())));
    }

    @Test
    public void isShouldNotMatchNonPresentResult() {
        final Predicate<List<Statement>> predicate = firstInList.is(s -> s != null);

        assertFalse(predicate.test(Collections.emptyList()));
    }

    @Test
    public void multipleWhereQueriesCanBeChangedWithAnd() {
        final ModelQuery<List<Statement>, Statement> modelQuery = firstInList
                .where(s -> s.getElementType() == ElementType.RETURN)
                .and(s -> s.getMetaData() != null);

        final Statement statement = mock(Statement.class);
        assertFalse(modelQuery.from(Arrays.asList(statement)).isPresent());

        when(statement.getElementType()).thenReturn(ElementType.RETURN);
        assertFalse(modelQuery.from(Arrays.asList(statement)).isPresent());

        when(statement.getMetaData()).thenReturn(mock(ElementMetaData.class));
        assertTrue(modelQuery.from(Arrays.asList(statement)).isPresent());
    }

    @Test
    public void joinShouldNotAcceptNullSubQuery() {
        assertThrown(() -> firstInList.join(null), AssertionError.class);
    }

    @Test
    public void joinShouldReturnIfSubQueryMatches() {
        final VariableAssignment variableAssignment = set(local("foo", String.class, 1)).to(constant("bar"));
        final Optional<VariableAssignment> result = firstInList.as(VariableAssignment.class)
                .join(assignedValue().where(equalTo(constant("bar"))))
                .from(Arrays.asList(variableAssignment));

        assertEquals(Optional.of(variableAssignment), result);
    }

    @Test
    public void joinShouldReturnNonPresentInstanceIfSubQueryDoesNotMatch() {
        final VariableAssignment variableAssignment = set(local("foo", String.class, 1)).to(constant("bar"));
        final Optional<VariableAssignment> result = firstInList.as(VariableAssignment.class)
                .join(assignedValue().where(equalTo(constant("baz"))))
                .from(Arrays.asList(variableAssignment));

        assertFalse(result.isPresent());
    }

    @Test
    public void fieldQueryShouldMatchFieldWithMatchingProperties() {
        // ModelQueries.field(ModelQueries.an"aField", String.class)
    }

    @Test
    public void modelCanBeSearchedForMatchingQuery() {
        final MethodCall model = AST.call(AST.field(AST.local("this", String.class, 0), String.class, "aString"), "aMethod", void.class);

        // ModelQueries.field("")
    }
}