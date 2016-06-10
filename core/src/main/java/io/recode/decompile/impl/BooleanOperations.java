package io.recode.decompile.impl;

import io.recode.decompile.*;
import io.recode.model.*;
import io.recode.classfile.ByteCode;
import io.recode.model.impl.BranchImpl;
import io.recode.model.impl.CompareImpl;
import io.recode.model.impl.UnaryOperatorImpl;
import io.recode.util.Lists;
import io.recode.util.Pair;
import io.recode.util.Priority;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static io.recode.classfile.ByteCode.*;
import static io.recode.decompile.DecompilationContextQueries.lastStatement;
import static io.recode.decompile.DecompilationContextQueries.secondToLastStatement;
import static io.recode.decompile.DecompilationStateSelectors.*;
import static io.recode.decompile.DecompilerDelegates.forQuery;
import static io.recode.model.AST.constant;
import static io.recode.model.ModelQueries.*;
import static io.recode.util.Lists.optionallyCollect;
import static io.recode.util.Lists.zip;

/**
 * The <code>BooleanOperations</code> decompilation delegation provides handling of operations related
 * to boolean operation. Plain instruction handling of lt, gt, eq etc instructions are provided, as well
 * as post-instruction handling transformation to map stack/statement patterns to syntax elements.
 */
public final class BooleanOperations implements DecompilerDelegation {

    private final ModelQuery<DecompilationContext, Branch> branchIfOperatorIsNotZero = secondToLastStatement()
            .as(Branch.class)
            .where(rightComparativeOperand().is(equalTo(constant(0))))
                .and(operatorTypeIs(OperatorType.NE))
                .and(leftComparativeOperand().is(ofRuntimeType(boolean.class)));

    @Override
    public void configure(DecompilerConfigurationBuilder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.lcmp).then(lcmp());
        configurationBuilder.on(ByteCode.fcmpl).then(fcmpl());
        configurationBuilder.on(ByteCode.fcmpg).then(fcmpg());
        configurationBuilder.on(ByteCode.dcmpl).then(dcmpl());
        configurationBuilder.on(ByteCode.dcmpg).then(dcmpg());
        configurationBuilder.on(ByteCode.ifne).then(ifne());
        configurationBuilder.on(ByteCode.ifeq).then(ifeq());
        configurationBuilder.on(ByteCode.iflt).then(iflt());
        configurationBuilder.on(ByteCode.ifge).then(ifge());
        configurationBuilder.on(ByteCode.ifgt).then(ifgt());
        configurationBuilder.on(ByteCode.ifle).then(ifle());
        configurationBuilder.on(ByteCode.if_icmpne).then(if_icmpne());
        configurationBuilder.on(ByteCode.if_icmpeq).then(if_icmpeq());
        configurationBuilder.on(ByteCode.if_icmpge).then(if_icmpge());
        configurationBuilder.on(ByteCode.if_icmpgt).then(if_icmpgt());
        configurationBuilder.on(ByteCode.if_icmple).then(if_icmple());
        configurationBuilder.on(ByteCode.if_icmplt).then(if_icmplt());
        configurationBuilder.on(ByteCode.if_acmpeq).then(if_acmpeq());
        configurationBuilder.on(ByteCode.if_acmpne).then(if_acmpne());

        configurationBuilder.after(ByteCode.iconst_0)
                .when(lastStatementIs(ElementType.GOTO).and(elementsAreStacked(constant(1), constant(0))))
                .then(forQuery(branchIfOperatorIsNotZero).apply(invertBoolean()));

        configurationBuilder.after(ByteCode.iconst_0)
                .when(lastStatementIs(ElementType.GOTO).and(elementsAreStacked(constant(1), constant(0))))
                .then(forQuery(secondToLastStatement().as(Branch.class)).apply(binaryBranchToBooleanCompare()));

        configurationBuilder.after(integerStoreInstructions())
                .withPriority(Priority.LOW)
                .then(coerceAssignedIntegerToBoolean());

        configurationBuilder.after(invokeinterface, invokespecial, invokestatic, invokevirtual)
                .when(elementIsStacked(ElementType.METHOD_CALL))
                .then(coerceConstantIntegerMethodParameterToBoolean());

        configurationBuilder.map(ElementType.BINARY_OPERATOR)
                .forQuery(value().where(operatorType().is(equalTo(OperatorType.NE))).and(leftOperand().get(runtimeType()).is(equalTo(boolean.class))).and(rightOperand().is(equalTo(constant(0)))))
                .to(source -> Optional.of(source.as(BinaryOperator.class).getLeftOperand()));

    }

    public static DecompilerDelegate lcmp() {
        return cmp();
    }

    public static DecompilerDelegate fcmpl() {
        return cmp();
    }

    public static DecompilerDelegate fcmpg() {
        return cmp();
    }

    public static DecompilerDelegate dcmpl() {
        return cmp();
    }

    public static DecompilerDelegate dcmpg() {
        return cmp();
    }

    private static DecompilerDelegate cmp() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Expression rightOperand = context.getStack().pop();
                final Expression leftOperand = context.getStack().pop();

                context.getStack().push(new CompareImpl(leftOperand, rightOperand));
            }
        };
    }

    public static DecompilerDelegate if_icmpne() {
        return if_cmp(OperatorType.NE);
    }

    public static DecompilerDelegate if_icmpeq() {
        return if_cmp(OperatorType.EQ);
    }

    public static DecompilerDelegate if_icmpge() {
        return if_cmp(OperatorType.GE);
    }

    public static DecompilerDelegate if_icmpgt() {
        return if_cmp(OperatorType.GT);
    }

    public static DecompilerDelegate if_icmple() {
        return if_cmp(OperatorType.LE);
    }

    public static DecompilerDelegate if_icmplt() {
        return if_cmp(OperatorType.LT);
    }

    public static DecompilerDelegate if_acmpeq() {
        return if_cmp(OperatorType.EQ);
    }

    public static DecompilerDelegate if_acmpne() {
        return if_cmp(OperatorType.NE);
    }

    public static DecompilerDelegate ifne() {
        return ifcmp0(OperatorType.NE);
    }

    public static DecompilerDelegate ifeq() {
        return ifcmp0(OperatorType.EQ);
    }

    public static DecompilerDelegate iflt() {
        return ifcmp0(OperatorType.LT);
    }

    public static DecompilerDelegate ifge() {
        return ifcmp0(OperatorType.GE);
    }

    public static DecompilerDelegate ifgt() {
        return ifcmp0(OperatorType.GT);
    }

    public static DecompilerDelegate ifle() {
        return ifcmp0(OperatorType.LE);
    }

    private static DecompilerDelegate if_cmp(OperatorType operatorType) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final int programCounter = context.getProgramCounter().get();
                final int relativeOffset = codeStream.nextSignedShort();
                final Expression rightOperand = context.pop();
                final Expression leftOperand = context.pop();

                context.enlist(new BranchImpl(leftOperand, operatorType, rightOperand, programCounter + relativeOffset));
            }
        };
    }

    private static DecompilerDelegate coerceAssignedIntegerToBoolean() {
        final ModelQuery<DecompilationContext, VariableAssignment> query = lastStatement().as(VariableAssignment.class)
                .where(assignedVariableTypeIs(boolean.class))
                .and(assignedValue().is(equalTo(constant(0)).or(equalTo(constant(1)))));

        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final Optional<VariableAssignment> result = query.from(context);

                if (result.isPresent()) {
                    final VariableAssignment variableAssignment = result.get().as(VariableAssignment.class);

                    if (variableAssignment.getVariableType().equals(boolean.class)) {
                        if (variableAssignment.getValue().equals(constant(1))) {
                            context.getStatements().last().swap(variableAssignment.withValue(constant(true)));
                        } else if (variableAssignment.getValue().equals(constant(0))) {
                            context.getStatements().last().swap(variableAssignment.withValue(constant(false)));
                        }
                    }
                }
            }
        };
    }

    private static DecompilerDelegate coerceConstantIntegerMethodParameterToBoolean() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final MethodCall methodCall = context.peek().as(MethodCall.class);

                final Optional<List<Pair<Type, Expression>>> newArgs = optionallyCollect(zip(methodCall.getSignature().getParameterTypes(), methodCall.getParameters()), typeAndValue -> {
                    if (typeAndValue.left().equals(boolean.class) && typeAndValue.right().getType().equals(int.class)) {
                        if (typeAndValue.right().equals(constant(1))) {
                            return typeAndValue.right(constant(true));
                        } else if (typeAndValue.right().equals(constant(0))) {
                            return typeAndValue.right(constant(false));
                        }
                    }

                    return typeAndValue;
                });

                if (newArgs.isPresent()) {
                    context.getStack().swap(methodCall.withParameters(Lists.collect(newArgs.get(), Pair::right)));
                }
            }
        };
    }

    private DecompilerElementDelegate<Branch> binaryBranchToBooleanCompare() {
        return (context, codeStream, byteCode, _result) -> {
            final Expression falseValue = context.getStack().pop();
            final Expression trueValue = context.getStack().pop();

            context.getStatements().last().remove();

            context.push(reduceBooleanOperation(context, trueValue, falseValue, Optional.<Expression>empty()).get());
        };
    }

    private Optional<Expression> reduceBooleanOperation(DecompilationContext context, Expression trueValue, Expression falseValue, Optional<Expression> nextBoolean) {
        final Optional<Branch> branchOptional = lastStatement().as(Branch.class).from(context);

        if (!branchOptional.isPresent()) {
            return nextBoolean;
        }

        final Branch branch = context.getStatements().last().remove().as(Branch.class);
        final Expression booleanExpression;

        if (branch.getTargetProgramCounter() == trueValue.getMetaData().getProgramCounter()) {
            final Expression logicalOperator = getLogicalOperator(context, branch, false);
            final Optional<Expression> result = reduceBooleanOperation(context, trueValue,
                    findFalseJumpDestination(nextBoolean.get()),
                    Optional.of(logicalOperator));

            return Optional.of(context.getModelFactory().binary(result.get(), OperatorType.OR, nextBoolean.get(), boolean.class));
        } else if (branch.getTargetProgramCounter() == falseValue.getMetaData().getProgramCounter()) {
            final Expression logicalOperator = getLogicalOperator(context, branch, true);

            booleanExpression = (nextBoolean.isPresent() ? context.getModelFactory().binary(logicalOperator, OperatorType.AND, nextBoolean.get(), boolean.class) : logicalOperator);

            return reduceBooleanOperation(context, trueValue, falseValue, Optional.of(booleanExpression));
        }

        return nextBoolean;
    }

    private Expression findFalseJumpDestination(Expression expression) {
        if (expression.getElementType() != ElementType.BINARY_OPERATOR) {
            return expression;
        }

        BinaryOperator binaryOperator = expression.as(BinaryOperator.class);

        while (binaryOperator.getLeftOperand().getElementType() == ElementType.BINARY_OPERATOR) {
            binaryOperator = binaryOperator.getLeftOperand().as(BinaryOperator.class);
        }

        return binaryOperator.getLeftOperand();
    }

    private Expression getLogicalOperator(DecompilationContext context, Branch currentBranch, boolean inclusive) {
        final Expression leftOperand;
        final Expression rightOperand;

        if (currentBranch.getLeftOperand().getElementType() == ElementType.COMPARE && currentBranch.getRightOperand().equals(constant(0))) {
            final Compare compare = currentBranch.getLeftOperand().as(Compare.class);

            leftOperand = compare.getLeftOperand();
            rightOperand = compare.getRightOperand();
        } else {
            leftOperand = currentBranch.getLeftOperand();
            rightOperand = currentBranch.getRightOperand();
        }

        final OperatorType operatorType = getLogicalOperatorType(currentBranch, inclusive);

        return context.getModelFactory().binary(leftOperand, operatorType, rightOperand, boolean.class);
    }

    private OperatorType getLogicalOperatorType(Branch currentBranch, boolean inclusive) {
        OperatorType operatorType;
        if (!inclusive) {
            operatorType = currentBranch.getOperatorType();
        } else {
            switch (currentBranch.getOperatorType()) {
                case EQ:
                    operatorType = OperatorType.NE;
                    break;
                case NE:
                    operatorType = OperatorType.EQ;
                    break;
                case GE:
                    operatorType = OperatorType.LT;
                    break;
                case GT:
                    operatorType = OperatorType.LE;
                    break;
                case LE:
                    operatorType = OperatorType.GT;
                    break;
                case LT:
                    operatorType = OperatorType.GE;
                    break;
                default:
                    throw new UnsupportedOperationException("Can't transform operator " + currentBranch.getOperatorType());
            }
        }
        return operatorType;
    }

    private static DecompilerElementDelegate<Branch> invertBoolean() {
        return (context, codeStream, byteCode, result) -> {
            context.getStack().pop();
            context.getStack().pop();
            context.getStatements().tail(-2).remove();
            context.push(new UnaryOperatorImpl(result.getLeftOperand(), OperatorType.NOT, boolean.class));
        };
    }

    private static DecompilerDelegate ifcmp0(final OperatorType operatorType) {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final int pc = context.getProgramCounter().get();
                final int relativeOffset = codeStream.nextSignedShort();

                context.enlist(new BranchImpl(context.getStack().pop(), operatorType, constant(0), pc + relativeOffset));
            }
        };
    }
}
