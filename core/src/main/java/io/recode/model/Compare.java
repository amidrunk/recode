package io.recode.model;

/**
 * A <code>Compare</code> expression results in an integer and is created when long, float and
 * double comparative operations are executed. This is an intermediate syntax element, i.e. it
 * does not map directly to the java syntax but is expected to be transformed to an appropriate
 * element at some stage in the decompilation process.
 */
public interface Compare extends Expression {

    Expression getLeftOperand();

    Expression getRightOperand();

}
