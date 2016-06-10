package io.recode.model;

/**
 * <code>AllocateInstance</code> is an intermediate syntax element that will be replaced during
 * byte code decompilation. The Java Byte Code format models an object instantiation by (1) allocating
 * memory for the instance and (2) invoking the constructor given the newly allocated memory. Since the
 * semantics in the Java syntax is to simply "new" an object, the allocation-initialize scheme will be
 * reduced to a simple new-AST node in the testifj Java syntax model.
 */
public interface InstanceAllocation extends StatementAndExpression {

    default ElementType getElementType() {
        return ElementType.ALLOCATE;
    }

}
