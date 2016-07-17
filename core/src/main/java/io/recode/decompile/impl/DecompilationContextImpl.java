package io.recode.decompile.impl;

import io.recode.decompile.*;
import io.recode.model.EmptyElementMetaData;
import io.recode.model.Expression;
import io.recode.model.ModelFactory;
import io.recode.model.Statement;
import io.recode.TypeResolver;
import io.recode.model.impl.LocalVariableReferenceImpl;
import io.recode.util.*;
import io.recode.classfile.Method;
import io.recode.util.Stack;

import java.lang.reflect.Type;
import java.util.*;

import io.recode.model.impl.DefaultModelFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class DecompilationContextImpl implements DecompilationContext {

    private final Decompiler decompiler;

    private final Method method;

    private final ProgramCounter programCounter;

    private final LineNumberCounter lineNumberCounter;

    private final Stack<Expression> stack;

    private final Sequence<Statement> statements;

    private final TypeResolver typeResolver;

    private final AtomicInteger contextVersion = new AtomicInteger();

    private final AtomicBoolean aborted = new AtomicBoolean(false);

    private final Stack<Expression> visibleStack;

    private final Sequence<Statement> visibleStatements;

    private final ModelFactory modelFactory;

    private final int startPC;

    @Deprecated
    public DecompilationContextImpl(Decompiler decompiler,
                                    Method method,
                                    ProgramCounter programCounter,
                                    LineNumberCounter lineNumberCounter,
                                    TypeResolver typeResolver,
                                    int startPC) {
        this(decompiler, method, programCounter, lineNumberCounter, typeResolver,
                new DefaultModelFactory(() -> EmptyElementMetaData.EMPTY),
                new SingleThreadedStack<>(), new LinkedSequence<>(), startPC);
    }

    public DecompilationContextImpl(Decompiler decompiler,
                                    Method method,
                                    ProgramCounter programCounter,
                                    LineNumberCounter lineNumberCounter,
                                    TypeResolver typeResolver,
                                    ModelFactory modelFactory,
                                    Stack<Expression> stack,
                                    Sequence<Statement> statements,
                                    int startPC) {
        assert decompiler != null : "Decompiler can't be null";
        assert method != null : "Method can't be null";
        assert programCounter != null : "Program counter can't be null";
        assert lineNumberCounter != null : "Line number counter can't be null";
        assert typeResolver != null : "Type resolver can't be null";

        this.decompiler = decompiler;
        this.method = method;
        this.programCounter = programCounter;
        this.lineNumberCounter = lineNumberCounter;
        this.typeResolver = typeResolver;
        this.stack = stack;
        this.statements = statements;
        this.modelFactory = modelFactory;
        this.startPC = startPC;

        this.visibleStatements = new TransformedSequence<>(statements, statement -> {
            contextVersion.incrementAndGet();
            return statement;
        }, Function.identity());

        this.visibleStack = new TransformedStack<>(stack, expression -> {
            contextVersion.incrementAndGet();
            return expression;
        }, Function.identity());
    }

    @Override
    public Decompiler getDecompiler() {
        return decompiler;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Type resolveType(String internalName) {
        assert internalName != null && !internalName.isEmpty() : "Internal type name can't be null or empty";

        return typeResolver.resolveType(internalName.replace('/', '.'));
    }

    @Override
    public int getStackSize() {
        return stack.size();
    }

    public boolean isStackCompliantWithComputationalCategories(int... computationalCategories) {
        assert computationalCategories != null : "Computational categories can't be null";

        if (computationalCategories.length > stack.size()) {
            return false;
        }

        final Iterable<Expression> subStack = (stack.size() == computationalCategories.length
                ? stack
                : stack.tail(stack.size() - computationalCategories.length));

        int index = 0;

        for (Expression expression : subStack) {
            final int actualComputationalCategory = Types.getComputationalCategory(expression.getType());

            if (computationalCategories[index++] != actualComputationalCategory) {
                return false;
            }
        }

        return true;
    }

    public Stack<Expression> getStack() {
        return visibleStack;
    }

    @Override
    @Deprecated
    public List<Expression> getStackedExpressions() {
        return Arrays.asList(stack.stream().toArray(Expression[]::new));
    }

    @Override
    public boolean reduce() {
        if (stack.isEmpty()) {
            return false;
        }

        checkReducable(stack.peek());

        final Expression expression = stack.pop();
        final Statement newStatement = (Statement) expression;
        final Sequence.SingleElement<Statement> selector = statements
                .first(s -> s.getMetaData().getProgramCounter() > expression.getMetaData().getProgramCounter());

        if (selector.exists()) {
            selector.insertBefore(newStatement);
        } else {
            statements.add(newStatement);
        }

        return true;
    }

    @Override
    public boolean reduceAll() throws IllegalStateException {
        if (stack.isEmpty()) {
            return false;
        }

        /*stack.forEach(this::checkReducable);
        stack.forEach(e -> statements.add(new StatementWithPC((Statement) e.expression(), e.pc(), e.version())));
        stack.clear();*/

        while (!stack.isEmpty()) {
            reduce();
        }

        return true;
    }

    @Override
    public void enlist(Statement statement) {
        assert statement != null : "Statement can't be null";

        contextVersion.incrementAndGet();
        statements.add(statement);
    }

    @Override
    public void push(Expression expression) {
        assert expression != null : "Expression can't be null";

        contextVersion.incrementAndGet();
        stack.push(expression);
    }

    @Override
    public void insert(int offset, Expression expression) {
        contextVersion.incrementAndGet();
        stack.insert(stack.size() + offset, expression);
    }

    @Override
    public Expression pop() {
        checkStackNotEmpty();
        contextVersion.incrementAndGet();
        return stack.pop();
    }

    @Override
    public Expression peek() throws IllegalStateException {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }

        return stack.peek();
    }

    @Override
    public Sequence<Statement> getStatements() {
        return visibleStatements;
    }

    @Override
    @Deprecated
    public boolean hasStackedExpressions() {
        return !stack.isEmpty();
    }

    @Override
    @Deprecated
    public void removeStatement(int index) {
        assert index >= 0 : "Index must be positive";
        contextVersion.incrementAndGet();
        statements.at(index).remove();
    }

    @Override
    public void abort() {
        this.aborted.set(true);
    }

    @Override
    public boolean isAborted() {
        return this.aborted.get();
    }

    @Override
    public ModelFactory getModelFactory() {
        return modelFactory;
    }

    @Override
    public int getStartPC() {
        return startPC;
    }

    @Override
    public ProgramCounter getProgramCounter() {
        return programCounter;
    }

    public LineNumberCounter getLineNumberCounter() {
        return lineNumberCounter;
    }

    private void checkStackNotEmpty() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("No syntax element is available on the stack (decompiling line number " + getLineNumberCounter().get() + ")");
        }
    }

    private void checkReducable(Expression stackedExpression) {
        if (!(stackedExpression instanceof Statement)) {
            throw new IllegalStateException("Stacked expression is not a statement: " + stackedExpression);
        }
    }

    public static final class Builder {

        private Decompiler decompiler;

        private Method method;

        private ProgramCounter programCounter;

        private LineNumberCounter lineNumberCounter;

        private TypeResolver typeResolver;

        private Stack<Expression> stack;

        private Sequence<Statement> statements;

        private ModelFactory modelFactory;

        private InstructionContext instructionContext;

        private int startPC;

        public Builder setDecompiler(Decompiler decompiler) {
            this.decompiler = decompiler;
            return this;
        }

        public Builder setMethod(Method method) {
            this.method = method;
            return this;
        }

        public Builder setProgramCounter(ProgramCounter programCounter) {
            this.programCounter = programCounter;
            return this;
        }

        public Builder setLineNumberCounter(LineNumberCounter lineNumberCounter) {
            this.lineNumberCounter = lineNumberCounter;
            return this;
        }

        public Builder setTypeResolver(TypeResolver typeResolver) {
            this.typeResolver = typeResolver;
            return this;
        }

        public Builder setStack(Stack<Expression> stack) {
            this.stack = stack;
            return this;
        }

        public Builder setStatements(Sequence<Statement> visibleStatements) {
            this.statements = visibleStatements;
            return this;
        }

        public Builder setModelFactory(ModelFactory modelFactory) {
            this.modelFactory = modelFactory;
            return this;
        }

        public Builder setInstructionContext(InstructionContext instructionContext) {
            this.instructionContext = instructionContext;
            return this;
        }

        public Builder setStartPC(int startPC) {
            this.startPC = startPC;
            return this;
        }

        public DecompilationContext build() {
            return new DecompilationContextImpl(
                    decompiler,
                    method,
                    programCounter,
                    lineNumberCounter,
                    typeResolver,
                    modelFactory,
                    stack,
                    statements,
                    startPC);
        }
    }

    private static final class ExpressionWithPC implements Comparable<ExpressionWithPC> {

        private final Expression expression;

        private final int pc;

        private final int contextVersion;

        private ExpressionWithPC(Expression expression, int pc, int contextVersion) {
            this.expression = expression;
            this.pc = pc;
            this.contextVersion = contextVersion;
        }

        public Expression expression() {
            return expression;
        }

        public int pc() {
            return pc;
        }

        public int version() {
            return contextVersion;
        }

        @Override
        public int compareTo(ExpressionWithPC o) {
            if (pc == o.pc) {
                return contextVersion - o.contextVersion;
            }

            return pc - o.pc;
        }

        @Override
        public String toString() {
            return "ExpressionWithPC{" +
                    "expression=" + expression +
                    ", pc=" + pc +
                    ", contextVersion=" + contextVersion +
                    '}';
        }
    }

    private final class StatementWithPC implements Comparable<StatementWithPC> {

        private final Statement statement;

        private final int pc;

        private final int contextVersion;

        private StatementWithPC(Statement statement, int pc, int contextVersion) {
            this.statement = statement;
            this.pc = pc;
            this.contextVersion = contextVersion;
        }

        public Statement statement() {
            return statement;
        }

        public int pc() {
            return pc;
        }

        @Override
        public int compareTo(StatementWithPC o) {
            if (pc == o.pc) {
                return contextVersion - o.contextVersion;
            }

            return pc - o.pc;
        }

        @Override
        public String toString() {
            return "StatementWithPC{" +
                    "statement=" + statement +
                    ", pc=" + pc +
                    ", contextVersion=" + contextVersion +
                    '}';
        }
    }
}
