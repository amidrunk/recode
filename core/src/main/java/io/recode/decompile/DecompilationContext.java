package io.recode.decompile;

import io.recode.classfile.Method;
import io.recode.model.Expression;
import io.recode.model.ModelFactory;
import io.recode.util.Sequence;
import io.recode.model.Statement;
import io.recode.util.Stack;

import java.lang.reflect.Type;
import java.util.List;

public interface DecompilationContext {

    int getStartPC();

    ProgramCounter getProgramCounter();

    LineNumberCounter getLineNumberCounter();

    Decompiler getDecompiler();

    Method getMethod();

    Type resolveType(String internalName);

    int getStackSize();

    boolean isStackCompliantWithComputationalCategories(int... computationalCategories);

    Stack<Expression> getStack();

    /**
     * Returns the expressions currently available on the stack. The last pushed element will be the last element
     * in the list.
     *
     * @return A list of the currently stacked expressions.
     */
    List<Expression> getStackedExpressions();

    /**
     * Reduces the stack, i.e. pops the stack and puts the stacked statement on in the
     * statement list. The element on the stack must be a valid statement.
     *
     * @return Whether or not any element was reduced.
     * @throws java.lang.IllegalStateException Thrown if there's no expression on the stack
     *                                         or the stacked expression is not a valid statement.
     */
    boolean reduce() throws IllegalStateException;

    /**
     * Reduces the stack until empty. All elements must be statements.
     *
     * @return Whether or not any element was reduced.
     * @throws IllegalStateException Thrown if any element is not a statement.
     */
    boolean reduceAll() throws IllegalStateException;

    /**
     * Called by the decompiler when a statements has been reduced and needs to be enlisted.
     * This will add the statement to the statement list.
     *
     * @param statement The statement that should be enlisted.
     */
    void enlist(Statement statement);

    /**
     * Pushes an expression onto the stack.
     *
     * @param expression The expression that should be pushed onto the stack.
     */
    void push(Expression expression);

    /**
     * Inserts a value into the stack. Used by e.g. dup_x1 that inserts a value beneath the top of
     * the stack.
     *
     * @param offset The offset from the top (0=top, -1=beneath top etc).
     * @param expression The expression that should be inserted.
     */
    void insert(int offset, Expression expression);

    /**
     * Pops an expression from the stack. If there's no expression available no the stack,
     * an <code>IllegalStateException</code> will be thrown.
     *
     * @return The popped statement.
     * @throws java.lang.IllegalStateException Thrown if there's no expression on the stack.
     */
    Expression pop() throws IllegalStateException;

    Expression peek() throws IllegalStateException;

    /**
     * The statements that have been enlisted thus far in the context.
     *
     * @return The enlisted statements.
     */
    Sequence<Statement> getStatements();

    boolean hasStackedExpressions();

    void removeStatement(int index);

    /**
     * Aborts the decompilation. The decompiler will attempt to complete the decompilation by reducing the
     * stack and returning completed decompilation.
     */
    void abort();

    /**
     * Returns whether or not decompilation has been aborted. If so, the decompiler should not proceed
     * with reading any more byte codes, but rather abort immediately and attempt to reduce the stack.
     *
     * @return Whether or not the decompilation has been aborted.
     */
    boolean isAborted();

    ModelFactory getModelFactory();
}
