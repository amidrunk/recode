package io.recode.decompile.impl;

import io.recode.decompile.*;
import io.recode.model.*;
import io.recode.classfile.ByteCode;
import io.recode.classfile.LocalVariable;
import io.recode.model.impl.ConstantImpl;
import io.recode.model.impl.IncrementImpl;
import io.recode.model.impl.LocalVariableReferenceImpl;

import java.io.IOException;
import java.util.Optional;

import static io.recode.decompile.DecompilationContextQueries.*;
import static io.recode.decompile.DecompilationStateSelectors.atLeastOneStatement;
import static io.recode.decompile.DecompilationStateSelectors.elementIsStacked;
import static io.recode.model.AST.constant;
import static io.recode.model.ModelQueries.*;

public final class UnaryOperations implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfigurationBuilder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompilation configuration builder can't be null";

        decompilerConfigurationBuilder.after(ByteCode.longStoreInstructions())
                .when(atLeastOneStatement().and(elementIsStacked(ElementType.BINARY_OPERATOR)))
                .then(correctPrefixFloatingPointIncrement(constant(-1L), constant(1L)));

        decompilerConfigurationBuilder.after(ByteCode.longStoreInstructions())
                .when(atLeastOneStatement().and(elementIsStacked(ElementType.VARIABLE_REFERENCE)))
                .then(correctPostfixFloatingPointIncrement(constant(-1L), constant(1L)));

        decompilerConfigurationBuilder.after(ByteCode.doubleStoreInstructions())
                .when(atLeastOneStatement().and(elementIsStacked(ElementType.VARIABLE_REFERENCE)))
                .then(correctDoublePostfixIncrement());

        decompilerConfigurationBuilder.after(ByteCode.doubleStoreInstructions())
                .when(atLeastOneStatement().and(elementIsStacked(ElementType.BINARY_OPERATOR)))
                .then(correctDoublePrefixIncrement());

        decompilerConfigurationBuilder.after(ByteCode.floatStoreInstructions())
                .when(atLeastOneStatement().and(elementIsStacked(ElementType.VARIABLE_REFERENCE)))
                .then(correctFloatPostfixIncrement());

        decompilerConfigurationBuilder.after(ByteCode.floatStoreInstructions())
                .when(atLeastOneStatement().and(elementIsStacked(ElementType.BINARY_OPERATOR)))
                .then(correctFloatPrefixIncrement());

        decompilerConfigurationBuilder.after(ByteCode.integerLoadInstructions())
                .when(stackContainsPrefixIncrementOfVariable())
                .then(correctPrefixIncrement());

        decompilerConfigurationBuilder.after(ByteCode.iinc)
                .when(stackContainsPostfixIncrementOfVariable())
                .then(correctPostfixIncrement());

        decompilerConfigurationBuilder.after(ByteCode.integerLoadInstructions())
                .when(atLeastOneStatement())
                .then(correctPrefixByteIncrement());

        decompilerConfigurationBuilder.after(ByteCode.integerStoreInstructions())
                .when(atLeastOneStatement().and(elementIsStacked(ElementType.VARIABLE_REFERENCE)))
                .then(correctPostfixByteCodeIncrement());

        decompilerConfigurationBuilder.on(ByteCode.iinc).then(iinc());
    }

    /**
     * Returns a decompiler delegate that handles the iinc operation. Note that iinc increments a variable without
     * leaving anything on the stack. Further, it does not state whether the increment is prefix or postfix. To fully
     * implement the increment, the stack will need to be corrected afterwards.
     *
     * @return A decompiler delegate that handles the <code>iinc=132</code> instruction.
     * @see UnaryOperations#correctPostfixIncrement()
     * @see UnaryOperations#correctPrefixIncrement()
     * @see UnaryOperations#correctPrefixByteIncrement()
     */
    public static DecompilerDelegate iinc() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final int variableIndex = codeStream.nextUnsignedByte();
                final int value = codeStream.nextByte();
                final LocalVariable localVariable = context.getMethod().getLocalVariableForIndex(variableIndex);

                context.push(new IncrementImpl(
                        new LocalVariableReferenceImpl(
                                localVariable.getName(),
                                localVariable.getType(),
                                variableIndex),
                        new ConstantImpl(value, int.class),
                        int.class,
                        Affix.UNDEFINED
                ));
            }
        };
    }

    /**
     * Returns a decompiler delegate that corrects a post fix increment s.t. that stack is transformed according to
     * <code>[X, increment(var=X, affix=undefined)] => [increment(var=X, affix=POSTFIX)]</code>
     *
     * @return A decompiler delegate that corrects a post fix increment.
     */
    private DecompilerDelegate correctPostfixIncrement() {
        return (context, codeStream, byteCode) -> {
            final Increment increment = context.getStack().pop().as(Increment.class);

            context.getStack().swap(new IncrementImpl(
                    increment.getLocalVariable(),
                    increment.getValue(),
                    int.class, Affix.POSTFIX));
        };
    }

    /**
     * Returns a decompiler delegate that corrects a prefix increment s.t. the stack is transformed according to
     * <code>[increment(var=X, affix=undefined), X] => [increment(var=X, affix=PREFIX)]</code>.
     *
     * @return A decompiler delegate that corrects a prefix increment.
     */
    private DecompilerDelegate correctPrefixIncrement() {
        return (context, codeStream, byteCode) -> {
            final LocalVariableReference localVariableReference = context.pop().as(LocalVariableReference.class);
            final Increment increment = context.peek().as(Increment.class);

            context.getStack().swap(new IncrementImpl(localVariableReference, increment.getValue(), int.class, Affix.PREFIX));
        };
    }

    /**
     * Returns a selector that matches an uncorrected postfix increment.
     *
     * @return A selector that matches <code>[X, increment(variable=X, affix=undefined)]</code>.
     */
    private DecompilationStateSelector stackContainsPostfixIncrementOfVariable() {
        final ModelQuery<DecompilationContext, Increment> query = peek()
                .as(Increment.class)
                .where(affixIsUndefined());

        return (context, byteCode) -> {
            final Optional<Increment> increment = query.from(context);
            final Optional<Expression> localVariable = previousValue().from(context);

            return increment.isPresent()
                    && localVariable.isPresent()
                    && increment.get().getLocalVariable().equals(localVariable.get());
        };
    }

    /**
     * Returns a selector that matches an uncorrected prefix increment.
     *
     * @return A selector that matches <code>[increment(variable=X, affix=undefined), X]</code>.
     */
    private DecompilationStateSelector stackContainsPrefixIncrementOfVariable() {
        final ModelQuery<DecompilationContext, Increment> query = previousValue()
                .as(Increment.class)
                .where(affixIsUndefined());

        return (context, byteCode) -> {
            final Optional<Increment> optionalIncrement = query.from(context);

            return optionalIncrement.isPresent() && optionalIncrement.get().getLocalVariable().equals(context.peek());

        };
    }

    /**
     * Corrects the compiled prefix increment on a byte. The compiled code will not use the increment operation
     * but will rather add/subtract 1 from the variable and then reload it. Example:
     * <pre>{@code
     * iload_1      [myByteVariable]
     * iconst_1     [myByteVariable, 1]
     * isub         [myByteVariable - 1]
     * l2b          [(byte)(myByteVariable - 1)]
     * istore_1     []
     * iload_1      [myByteVariable]
     * }
     * </pre>
     *
     * @return A decompiler delegate that corrects the syntax tree for byte prefix increment/decrement.
     */
    private static DecompilerDelegate correctPrefixByteIncrement() {
        return (context, codeStream, byteCode) -> {
            final LocalVariableReference loadedVariable = context.peek().as(LocalVariableReference.class);

            final Optional<BinaryOperator> increment = lastStatement().as(VariableAssignment.class)
                    .where(isAssignmentTo(loadedVariable))
                    .get(assignedValue()).as(TypeCast.class)
                    .get(castValue()).as(BinaryOperator.class)
                    .where(leftOperand().is(equalTo(loadedVariable)))
                    .and(rightOperand().as(Constant.class).is(equalTo(constant(1))))
                    .from(context);

            if (!increment.isPresent()) {
                return;
            }

            context.removeStatement(context.getStatements().size() - 1);
            context.pop();

            context.push(new IncrementImpl(
                    loadedVariable,
                    constant(increment.get().getOperatorType() == OperatorType.MINUS ? -1 : 1),
                    loadedVariable.getType(), Affix.PREFIX));
        };
    }

    /**
     * Corrects a postfix byte increment. A byte increment does not use the iinc operator since that results in
     * an integer (byte newByte = otherByte++ was not syntactically correct in previous java versions). This delegate
     * will change the following sequence to a postfix increment/decrement:
     * <pre>{@code
     * iload_1
     * iload_1      [var, var]
     * iconst_1     [var, var, 1]
     * isub         [var, var - 1]
     * i2b          [var, (byte)(var - 1)]
     * istore_1     [var]
     * }</pre>
     *
     * @return A decompiler delegate that corrects postfix byte increment.
     */
    private DecompilerDelegate correctPostfixByteCodeIncrement() {
        return (context, codeStream, byteCode) -> {
            final Optional<BinaryOperator> result = lastStatement().as(VariableAssignment.class)
                    .get(assignedValue()).as(TypeCast.class)
                    .get(castValue()).as(BinaryOperator.class)
                    .where(leftOperand().is(equalTo(context.getStack().peek())))
                    .and(rightOperand().is(equalTo(constant(1))))
                    .from(context);

            if (!result.isPresent()) {
                return;
            }

            final LocalVariableReference local = context.getStack().pop().as(LocalVariableReference.class);

            context.removeStatement(context.getStatements().size() - 1);
            context.getStack().push(new IncrementImpl(
                    local,
                    constant(result.get().getOperatorType() == OperatorType.MINUS ? -1 : 1),
                    local.getType(),
                    Affix.POSTFIX
            ));
        };
    }

    /**
     * Corrects prefix float increment. E.g. a prefix decrement is implemented by the compiler as:
     * <pre>{@code
     * fload_1      [var]
     * fconst_1     [var, 1]
     * fsub         [var - 1]
     * dup          [var - 1, var - 1]
     * fstore_1     [var - 1]
     * }</pre>
     *
     * This is transformed into a single <code>--var</code>.
     *
     * @return A decompiler delegate that corrects float prefix increment.
     */
    private static DecompilerDelegate correctFloatPrefixIncrement() {
        return correctPrefixFloatingPointIncrement(constant(-1f), constant(1f));
    }

    /**
     * Corrects double prefix increment. See also {@link UnaryOperations#correctFloatPrefixIncrement()}.
     *
     * @return A decompiler delegate that corrects prefix double increment.
     */
    private static DecompilerDelegate correctDoublePrefixIncrement() {
        return correctPrefixFloatingPointIncrement(constant(-1d), constant(1d));
    }

    private static DecompilerDelegate correctPrefixFloatingPointIncrement(Constant decrementConstant, Constant incrementConstant) {
        final ModelQuery<DecompilationContext, VariableAssignment> query = lastStatement().as(VariableAssignment.class)
                .join(assignedValue().as(BinaryOperator.class)
                        .where(leftOperand().is(ofType(LocalVariableReference.class)))
                        .and(rightOperand().is(equalTo(incrementConstant))));

        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Optional<VariableAssignment> variableAssignmentOptional = query.from(context);

                if (!variableAssignmentOptional.isPresent()) {
                    return;
                }

                final VariableAssignment variableAssignment = variableAssignmentOptional.get();

                if (!context.getStack().peek().equals(variableAssignment.getValue())) {
                    return;
                }

                final OperatorType operatorType = variableAssignment.getValue().as(BinaryOperator.class).getOperatorType();

                context.removeStatement(context.getStatements().size() - 1);
                context.getStack().swap(new IncrementImpl(
                        new LocalVariableReferenceImpl(
                                variableAssignment.getVariableName(),
                                variableAssignment.getVariableType(),
                                variableAssignment.getVariableIndex()),
                        operatorType == OperatorType.MINUS ? decrementConstant : incrementConstant,
                        incrementConstant.getType(),
                        Affix.PREFIX
                ));
            }
        };
    }

    /**
     * Corrects a float postfix increment/decrement. The compiler implements postfix increment of as:
     * <pre>{@code
     * fload_1      [var]
     * dup          [var, var]
     * fconst_1     [var, var, 1]
     * fadd         [var, var + 1]
     * fstore_1     [var]
     * }</pre>
     *
     * @return A decompiler delegate that corrects the stack for float postfix increment.
     */
    private static DecompilerDelegate correctFloatPostfixIncrement() {
        return correctPostfixFloatingPointIncrement(constant(-1f), constant(1f));
    }

    private static DecompilerDelegate correctDoublePostfixIncrement() {
        return correctPostfixFloatingPointIncrement(constant(-1d), constant(1d));
    }

    private static DecompilerDelegate correctPostfixFloatingPointIncrement(Constant decrementConstant, Constant incrementConstant) {
        final ModelQuery<DecompilationContext, VariableAssignment> query = lastStatement().as(VariableAssignment.class)
                .join(assignedValue().as(BinaryOperator.class)
                        .where(leftOperand().is(ofType(LocalVariableReference.class)))
                        .and(rightOperand().is(equalTo(incrementConstant))));

        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Optional<VariableAssignment> variableAssignmentOptional = query.from(context);

                if (!variableAssignmentOptional.isPresent()) {
                    return;
                }

                final VariableAssignment variableAssignment = variableAssignmentOptional.get();
                final LocalVariableReferenceImpl local = new LocalVariableReferenceImpl(
                        variableAssignment.getVariableName(),
                        variableAssignment.getVariableType(),
                        variableAssignment.getVariableIndex()
                );

                if (!context.getStack().peek().equals(local)) {
                    return;
                }

                final BinaryOperator binaryOperator = variableAssignment.getValue().as(BinaryOperator.class);

                context.removeStatement(context.getStatements().size() - 1);
                context.getStack().swap(new IncrementImpl(
                        local,
                        binaryOperator.getOperatorType() == OperatorType.MINUS ? decrementConstant : incrementConstant,
                        incrementConstant.getType(),
                        Affix.POSTFIX
                ));
            }
        };
    }
}
