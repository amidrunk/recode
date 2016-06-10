package io.recode.decompile.impl;

import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFileFormatException;
import io.recode.decompile.*;
import io.recode.model.Expression;

import java.io.IOException;
import java.util.List;

public final class StackInstructions implements DecompilerDelegation {

    @Override
    public void configure(DecompilerConfigurationBuilder decompilerConfigurationBuilder) {
        assert decompilerConfigurationBuilder != null : "Decompilation configuration builder can't be null";

        decompilerConfigurationBuilder.on(ByteCode.pop).then(pop());
        decompilerConfigurationBuilder.on(ByteCode.pop2).then(pop2());
        decompilerConfigurationBuilder.on(ByteCode.dup).then(dup());
        decompilerConfigurationBuilder.on(ByteCode.dup_x1).then(dup_x1());
        decompilerConfigurationBuilder.on(ByteCode.dup_x2).then(dup_x2());
        decompilerConfigurationBuilder.on(ByteCode.dup2).then(dup2());
        decompilerConfigurationBuilder.on(ByteCode.dup2_x1).then(dup2_x1());
        decompilerConfigurationBuilder.on(ByteCode.dup2_x2).then(dup2_x2());
        decompilerConfigurationBuilder.on(ByteCode.swap).then(swap());
    }

    public static DecompilerDelegate pop() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                if (!context.isStackCompliantWithComputationalCategories(1)) {
                    throw new ClassFileFormatException("Stack must comply with computational categories [1]; " +
                            "actual stack was " + context.getStackedExpressions());
                }

                if (!context.reduce()) {
                    throw new ClassFileFormatException("Stack is empty; exactly one operand required for <pop>");
                }
            }
        };
    }

    public static DecompilerDelegate pop2() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                if (context.isStackCompliantWithComputationalCategories(1, 1)) {
                    if (!context.reduce() || !context.reduce()) {
                        throw new ClassFileFormatException("Stack contains irreducible operand; exactly " +
                                "two reducible operands expected, was  " + context.getStackedExpressions());
                    }

                    return;
                }

                if (context.isStackCompliantWithComputationalCategories(2)) {
                    if (!context.reduce()) {
                        throw new ClassFileFormatException("Stack contains irreducible operand; exactly one " +
                                "reducible operand expected, was " + context.getStackedExpressions());
                    }

                    return;
                }

                throw new ClassFileFormatException("Stack is not valid for <pop2>; expected stack with types of " +
                        "computational categories [2] or [1, 1], was " + context.getStackedExpressions());
            }
        };
    }

    public static DecompilerDelegate dup() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                if (!context.isStackCompliantWithComputationalCategories(1)) {
                    throw new ClassFileFormatException("Stack must comply with computational type categories [1], " +
                            "actual stack was " + context.getStackedExpressions());
                }

                context.push(context.peek());
            }
        };
    }

    public static DecompilerDelegate dup_x1() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                if (!context.isStackCompliantWithComputationalCategories(1, 1)) {
                    throw new ClassFileFormatException("Stack must comply with computational type categories [1, 1], " +
                            "actual stack was " + context.getStackedExpressions());
                }

                context.insert(-2, context.peek());
            }
        };
    }

    public static DecompilerDelegate dup_x2() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                if (context.isStackCompliantWithComputationalCategories(1, 1, 1)) {
                    context.insert(-3, context.peek());
                } else if (context.isStackCompliantWithComputationalCategories(2, 1)) {
                    context.insert(-2, context.peek());
                } else {
                    throw new ClassFileFormatException("Stack must comply with computational type categories [1, 1, 1] or [1, 2], " +
                            "actual stack was " + context.getStackedExpressions());
                }
            }
        };
    }

    public static DecompilerDelegate dup2() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final List<Expression> stack = context.getStackedExpressions();

                if (context.isStackCompliantWithComputationalCategories(1, 1)) {
                    context.push(stack.get(stack.size() - 2));
                    context.push(stack.get(stack.size() - 1));
                } else if (context.isStackCompliantWithComputationalCategories(2)) {
                    context.push(stack.get(stack.size() - 1));
                } else {
                    throw new ClassFileFormatException("Stack must comply with computational type categories [1, 1] or [2], " +
                            "actual stack was " + context.getStackedExpressions());
                }
            }
        };
    }

    public static DecompilerDelegate dup2_x1() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final List<Expression> stack = context.getStackedExpressions();

                if (context.isStackCompliantWithComputationalCategories(1, 1, 1)) {
                    context.insert(-3, stack.get(stack.size() - 2));
                    context.insert(-3, stack.get(stack.size() - 1));
                } else if (context.isStackCompliantWithComputationalCategories(1, 2)) {
                    context.insert(-2, stack.get(stack.size() - 1));
                } else {
                    throw new ClassFileFormatException("Stack must comply with computational type categories [1, 1, 1] or [2, 1], " +
                            "actual stack was " + context.getStackedExpressions());
                }
            }
        };
    }

    public static DecompilerDelegate dup2_x2() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                final List<Expression> stack = context.getStackedExpressions();

                if (context.isStackCompliantWithComputationalCategories(1, 1, 1, 1)) {
                    context.insert(-4, stack.get(stack.size() - 2));
                    context.insert(-4, stack.get(stack.size() - 1));
                } else if (context.isStackCompliantWithComputationalCategories(2, 1, 1)) {
                    context.insert(-3, stack.get(stack.size() - 2));
                    context.insert(-3, stack.get(stack.size() - 1));
                } else if (context.isStackCompliantWithComputationalCategories(1, 1, 2)) {
                    context.insert(-3, context.peek());
                } else if (context.isStackCompliantWithComputationalCategories(2, 2)) {
                    context.insert(-2, context.peek());
                } else {
                    throw new ClassFileFormatException("Stack must comply with computational type categories [1, 1, 1, 1], [2, 1, 1], [1, 1, 2] or [2, 2]" +
                            "actual stack was " + context.getStackedExpressions());
                }
            }
        };
    }

    public static DecompilerDelegate swap() {
        return new DecompilerDelegate() {
            @Override
            public void apply(DecompilationContext context, CodeStream codeStream, int byteCode) throws IOException {
                if (!context.isStackCompliantWithComputationalCategories(1, 1)) {
                    throw new ClassFileFormatException("Stack must comply with computational type categories [1, 1], " +
                            "actual stack was " + context.getStackedExpressions());
                } else {
                    context.insert(-1, context.pop());
                }
            }
        };
    }
}
