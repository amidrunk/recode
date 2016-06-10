package io.recode.util;

import io.recode.model.Statement;
import org.junit.Test;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class LinkedSequenceTest {

    private final Sequence<Statement> sequence = new LinkedSequence<>();
    private final Statement statement1 = mock(Statement.class, "statement1");
    private final Statement statement2 = mock(Statement.class, "statement2");
    private final Statement statement3 = mock(Statement.class, "statement3");

    @Test
    public void enlistShouldNotAcceptNullArgument() {
        assertThrown(() -> sequence.add(null), AssertionError.class);
    }

    @Test
    public void allShouldReturnAllEnlistedStatements() {
        sequence.add(statement1);
        sequence.add(statement2);

        assertEquals(Arrays.asList(statement1, statement2), sequence.all().get());
    }

    @Test
    public void allCanClearEntireContents() {
        sequence.add(statement1);
        sequence.add(statement2);

        sequence.all().remove();

        assertTrue(sequence.all().get().isEmpty());
    }

    @Test
    public void swapOnFirstElementShouldFailIfStatementsAreEmpty() {
        assertThrown(() -> sequence.first().swap(statement1), NoSuchElementException.class);
    }

    @Test
    public void firstElementCanBeSwapped() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.first().swap(statement3);

        assertArrayEquals(new Statement[]{statement3, statement2}, sequence.all().get().toArray());
    }

    @Test
    public void statementsCanBeIterated() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);

        assertArrayEquals(new Statement[]{statement1, statement2, statement3}, sequence.all().get().toArray());
    }

    @Test
    public void delistFirstShouldFailIfStatementsAreEmpty() {
        assertThrown(() -> sequence.first().remove(), NoSuchElementException.class);
    }

    @Test
    public void firstElementCanBeDelisted() {
        sequence.add(statement1);
        sequence.add(statement2);

        assertEquals(2, sequence.size());
        assertArrayEquals(new Statement[]{statement1, statement2}, sequence.all().get().toArray());

        sequence.first().remove();

        assertEquals(1, sequence.size());
        assertArrayEquals(new Statement[]{statement2}, sequence.all().get().toArray());
    }

    @Test
    public void getLastElementShouldFailOnEmptyStatements() {
        assertThrown(() -> sequence.last().get(), NoSuchElementException.class);
    }

    @Test
    public void getLastShouldReturnLastElement() {
        sequence.add(statement1);
        sequence.add(statement2);

        assertEquals(statement2, sequence.last().get());
    }

    @Test
    public void swapLastShouldFailIfStatementsAreEmpty() {
        assertThrown(() -> sequence.last().swap(statement2), NoSuchElementException.class);
    }

    @Test
    public void swapLastShouldSwapLastElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.last().swap(statement3);

        assertArrayEquals(new Statement[]{statement1, statement3}, sequence.all().get().toArray());
    }

    @Test
    public void delistLastShouldFailIfStatementsAreEmpty() {
        assertThrown(() -> sequence.last().remove(), NoSuchElementException.class);
    }

    @Test
    public void delistLastShouldRemoveLastElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.last().remove();

        assertEquals(1, sequence.size());
        assertArrayEquals(new Statement[]{statement1}, sequence.all().get().toArray());
    }

    @Test
    public void getAtIndexShouldFailIfElementDoesNotExist() {
        assertThrown(() -> sequence.at(0).get(), NoSuchElementException.class);
    }

    @Test
    public void getAtIndexShouldReturnElementAtIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);

        assertEquals(statement1, sequence.at(0).get());
        assertEquals(statement2, sequence.at(1).get());
        assertEquals(statement3, sequence.at(2).get());
    }

    @Test
    public void statementAtIndexShouldFailImmediatelyForNegativeIndex() {
        assertThrown(() -> sequence.at(-1), AssertionError.class);
    }

    @Test
    public void firstCanBeDelistedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.at(0).remove();

        assertEquals(1, sequence.size());
        assertArrayEquals(new Statement[] {statement2}, sequence.all().get().toArray());
    }

    @Test
    public void lastCanBeReplacedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.at(1).remove();

        assertEquals(1, sequence.size());
        assertArrayEquals(new Statement[] {statement1}, sequence.all().get().toArray());
    }

    @Test
    public void intermediateCanBeReplacedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);
        sequence.at(1).remove();

        assertEquals(2, sequence.size());
        assertArrayEquals(new Statement[] {statement1, statement3}, sequence.all().get().toArray());
    }

    @Test
    public void firstElementCanBeSwappedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.at(0).swap(statement3);

        assertArrayEquals(new Statement[] {statement3, statement2}, sequence.all().get().toArray());
    }

    @Test
    public void lastElementCanBeSwappedThroughIndex() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.at(1).swap(statement3);

        assertArrayEquals(new Statement[]{statement1,statement3}, sequence.all().get().toArray());
    }

    @Test
    public void swapByIndexShouldNotAcceptNullNewStatement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);

        assertThrown(() -> sequence.at(0).swap(null), AssertionError.class);
        assertThrown(() -> sequence.at(1).swap(null), AssertionError.class);
        assertThrown(() -> sequence.at(2).swap(null), AssertionError.class);
    }

    @Test
    public void firstShouldNotExistForEmptyStatements() {
        assertFalse(sequence.first().exists());
    }

    @Test
    public void firstShouldExistForNonEmptyStatements() {
        sequence.add(statement1);
        assertTrue(sequence.first().exists());
    }

    @Test
    public void lastShouldNotExistForEmptyStatements() {
        assertFalse(sequence.last().exists());
    }

    @Test
    public void lastShouldExistForNonEmptyStatements() {
        sequence.add(statement1);
        assertTrue(sequence.last().exists());
    }

    @Test
    public void getByIndexShouldNotExistsIfNoElementExistsForIndex() {
        sequence.add(statement1);

        assertFalse(sequence.at(1).exists());
        assertFalse(sequence.at(2).exists());
    }

    @Test
    public void getByIndexShouldExistIfElementExistsAtIndex() {
        sequence.add(statement1);
        sequence.add(statement2);

        assertTrue(sequence.at(0).exists());
        assertTrue(sequence.at(1).exists());
    }

    @Test
    public void clearShouldRemoveElementsAndResetSize() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.clear();

        assertEquals(0, sequence.size());
        assertTrue(sequence.isEmpty());
    }

    @Test
    public void isEmptyShouldBeTrueIfNoElementsExists() {
        assertTrue(sequence.isEmpty());
    }

    @Test
    public void isEmptyShouldBeFalseIfAtLeastOneElementExists() {
        sequence.add(statement1);
        assertFalse(sequence.isEmpty());
    }

    @Test
    public void firstShouldNotAcceptNullPredicate() {
        assertThrown(() -> sequence.first(null), AssertionError.class);
    }

    @Test
    public void firstShouldNotExistIfNoMatchingElementsExists() {
        sequence.add(statement1);
        assertFalse(sequence.first(s -> false).exists());
    }

    @Test
    public void firstShouldReturnFirstMatchingElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        assertEquals(statement1, sequence.first(s -> true).get());
    }

    @Test
    public void firstWithPredicateShouldFailIfListIsConcurrentlyModified() {
        sequence.add(statement1);

        final Sequence.SingleElement<Statement> selector = sequence.first(s -> true);
        assertTrue(selector.exists());

        sequence.add(statement2);

        assertThrown(() -> selector.get(), ConcurrentModificationException.class);
    }

    @Test
    public void removeAtIndexShouldReduceSizeForOneElement() {
        sequence.add(statement1);
        sequence.at(0).remove();

        assertEquals(0, sequence.size());
    }

    @Test
    public void removeAtLastIndexShouldReduceSizeForMultipleElements() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.add(statement3);
        sequence.at(2).remove();

        assertEquals(2, sequence.size());
    }

    @Test
    public void iteratorShouldFailIfListIsModified() {
        sequence.add(statement1);
        sequence.add(statement2);

        final Iterator<Statement> iterator = sequence.iterator();

        assertEquals(statement1, iterator.next());
        sequence.add(statement3);

        assertThrown(() -> iterator.next(), ConcurrentModificationException.class);
    }

    @Test
    public void firstWithPredicateCannotBeSwappedIfNoElementMatches() {
        sequence.add(statement1);
        assertThrown(() -> sequence.first(s -> false).swap(statement2), NoSuchElementException.class);
    }

    @Test
    public void firstWithPredicateShouldSwapFirstMatchingElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.first(s -> true).swap(statement3);

        assertArrayEquals(new Statement[] {statement3, statement2}, sequence.all().get().toArray());
    }

    @Test
    public void swapForFirstWithPredicateShouldNotAcceptNullElement() {
        sequence.add(statement1);
        assertThrown(() -> sequence.first(s -> true).swap(null), AssertionError.class);
    }

    @Test
    public void removeForFirstWithPredicateShouldFailIfNoElementExists() {
        sequence.add(statement1);
        assertThrown(() -> sequence.first(s -> false).remove(), NoSuchElementException.class);
    }

    @Test
    public void removeForFirstWithPredicateShouldRemoveFirstMatchingElement() {
        sequence.add(statement1);
        sequence.add(statement2);
        sequence.first(s -> true).remove();

        assertEquals(1, sequence.size());
        assertArrayEquals(new Statement[] {statement2}, sequence.all().get().toArray());
    }

    @Test
    public void lastByPredicateShouldNotAcceptNullPredicate() {
        assertThrown(() -> sequence.last(null), AssertionError.class);
    }

    @Test
    public void lastByPredicateShouldReturnNonExistingSelectorIfNoElementMatches() {
        sequence.addAll(Arrays.asList(statement1, statement2));

        final Sequence.SingleElement<Statement> selector = sequence.last(s -> false);

        assertFalse(selector.exists());
        assertThrown(() -> selector.get(), NoSuchElementException.class);
        assertThrown(() -> selector.swap(statement3), NoSuchElementException.class);
        assertThrown(() -> selector.remove(), NoSuchElementException.class);
    }

    @Test
    public void lastByPredicateShouldReturnSelectorForLastMatchingElement() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        final Sequence.SingleElement<Statement> selector = sequence.last(s -> s == statement2);

        assertTrue(selector.exists());
        assertEquals(statement2, selector.get());

        selector.swap(statement3);

        assertEquals(statement3, selector.get());
        assertArrayEquals(new Statement[] {statement1, statement3, statement3}, sequence.all().get().toArray());

        selector.remove();
        assertEquals(2, sequence.size());
        assertArrayEquals(new Statement[] {statement1, statement3}, sequence.all().get().toArray());
    }

    @Test
    public void insertBeforeFirstShouldInsertElementAtFirstIndex() {
        sequence.addAll(Arrays.asList(statement1, statement2));

        sequence.first().insertBefore(statement3);

        assertArrayEquals(new Statement[] {statement3, statement1, statement2}, sequence.all().get().toArray());
        assertEquals(3, sequence.size());
        assertEquals(statement3, sequence.first().get());
        assertEquals(statement3, sequence.first().get());
    }

    @Test
    public void insertBeforeFirstShouldFailForEmptySequence() {
        assertThrown(() -> sequence.first().insertBefore(statement1), NoSuchElementException.class);
    }

    @Test
    public void insertBeforeFirstShouldNotAcceptNullElement() {
        sequence.add(statement1);
        assertThrown(() -> sequence.first().insertBefore(null), AssertionError.class);
    }

    @Test
    public void insertBeforeFirstByPredicateShouldNotAcceptNullElement() {
        sequence.add(statement1);
        assertThrown(() -> sequence.first(s -> true).insertBefore(null), AssertionError.class);
    }

    @Test
    public void insertBeforeFirstByPredicateShouldFailForEmptySequence() {
        assertThrown(() -> sequence.first(s -> true).insertBefore(statement1), NoSuchElementException.class);
    }

    @Test
    public void insertBeforeFirstByPredicateShouldElementBeforeFirstMatchingElement() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        sequence.first(s -> s == statement2).insertBefore(statement3);

        assertArrayEquals(new Statement[] {statement1, statement3, statement2, statement3}, sequence.all().get().toArray());
    }

    @Test
    public void insertBeforeLastShouldNotAcceptNullElement() {
        sequence.add(statement1);
        assertThrown(() -> sequence.last().insertBefore(null), AssertionError.class);
    }

    @Test
    public void insertBeforeLastShouldFailForEmptySequence() {
        assertThrown(() -> sequence.last().insertBefore(statement1), NoSuchElementException.class);
    }

    @Test
    public void insertBeforeLastShouldInsertElementBeforeLastElement() {
        sequence.add(statement1);

        final Sequence.SingleElement<Statement> selector = sequence.last();

        selector.insertBefore(statement2);

        assertArrayEquals(new Statement[]{statement2, statement1}, sequence.all().get().toArray());

        selector.insertBefore(statement3);

        assertArrayEquals(new Statement[] {statement2, statement3, statement1}, sequence.all().get().toArray());
    }

    @Test
    public void insertAtLastByPredicateShouldNotAcceptNullElement() {
        sequence.add(statement1);
        assertThrown(() -> sequence.last(s -> s == statement1).insertBefore(null), AssertionError.class);
    }

    @Test
    public void insertAtLastByPredicateShouldFailIfNoElementMatches() {
        sequence.add(statement1);
        assertThrown(() -> sequence.last(s -> false).insertBefore(statement3), NoSuchElementException.class);
    }

    @Test
    public void insertAtLastByPredicateShouldInsertElementBeforeFirstMatchingElementFromEnd() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        final Sequence.SingleElement<Statement> selector = sequence.last(s -> s == statement2);

        selector.insertBefore(statement3);

        assertArrayEquals(new Statement[] {statement1, statement3, statement2, statement3}, sequence.all().get().toArray());
    }

    @Test
    public void insertBeforeSpecifiedIndexShouldFailForInvalidIndex() {
        assertThrown(() -> sequence.at(0).insertBefore(statement1), NoSuchElementException.class);
    }

    @Test
    public void insertBeforeSpecifiedIndexShouldNotAcceptNullElement() {
        sequence.add(statement1);
        assertThrown(() -> sequence.at(0).insertBefore(null), AssertionError.class);
    }

    @Test
    public void insertBeforeShouldInsertElementAtIndex() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        final Sequence.SingleElement<Statement> selector = sequence.at(1);

        selector.insertBefore(statement3);

        assertArrayEquals(new Statement[]{statement1, statement3, statement2, statement3}, sequence.all().get().toArray());
    }

    @Test
    public void selectedElementCanBeNavigatedToPreviousElement() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        assertEquals(statement2, sequence.last().previous().get());
    }

    @Test
    public void previousOnSelectorShouldFailIfElementDoesNotExists() {
        assertThrown(() -> sequence.last().previous(), NoSuchElementException.class);
    }

    @Test
    public void previousOnSelectorShouldReturnNonExistingSelectorIfPreviousElementDoesNotExist() {
        sequence.addAll(Arrays.asList(statement1));

        assertFalse(sequence.last().previous().exists());
    }

    @Test
    public void tailShouldReturnNonExistingSelectorForInvalidIndex() {
        assertFalse(sequence.tail(1).exists());
    }

    @Test
    public void tailShouldReturnRemainingElementsForPositiveIndex() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        assertEquals(Arrays.asList(statement1, statement2, statement3), sequence.tail(0).get());
        assertEquals(Arrays.asList(statement2, statement3), sequence.tail(1).get());
        assertEquals(Arrays.asList(statement3), sequence.tail(2).get());
        assertTrue(sequence.tail(3).get().isEmpty());
        assertFalse(sequence.tail(4).exists());
    }

    @Test
    public void tailWithRelativeOffsetFromStartCanBeRemoved() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));
        sequence.tail(1).remove();
        assertArrayEquals(new Statement[] {statement1}, sequence.all().get().toArray());
    }

    @Test
    public void tailShouldReturnTailFromEndForNegativeIndex() {
        sequence.addAll(Arrays.asList(statement1, statement2, statement3));

        assertEquals(Arrays.asList(statement3), sequence.tail(-1).get());
        assertEquals(Arrays.asList(statement2, statement3), sequence.tail(-2).get());
        assertEquals(Arrays.asList(statement1, statement2, statement3), sequence.tail(-3).get());
        assertFalse(sequence.tail(-4).exists());
    }
}