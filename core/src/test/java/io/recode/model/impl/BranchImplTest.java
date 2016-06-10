package io.recode.model.impl;

import io.recode.model.*;
import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BranchImplTest {

    private final Expression rightOperand = mock(Expression.class);

    private final Expression leftOperand = mock(Expression.class);
    private final Branch exampleBranch = new BranchImpl(leftOperand, OperatorType.NE, rightOperand, 1234);

    @Test
    public void constructorShouldValidateParameters() {
        assertThrown(() -> new BranchImpl(null, OperatorType.NE, rightOperand, 1234), AssertionError.class);
        assertThrown(() -> new BranchImpl(leftOperand, null, rightOperand, 1234), AssertionError.class);
        assertThrown(() -> new BranchImpl(leftOperand, OperatorType.NE, null, 1234), AssertionError.class);
        assertThrown(() -> new BranchImpl(leftOperand, OperatorType.NE, rightOperand, -1), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainParameters() {
        assertEquals(leftOperand, exampleBranch.getLeftOperand());
        assertEquals(OperatorType.NE, exampleBranch.getOperatorType());
        assertEquals(rightOperand, exampleBranch.getRightOperand());
        assertEquals(1234, exampleBranch.getTargetProgramCounter());
        assertEquals(ElementType.BRANCH, exampleBranch.getElementType());
    }

    @Test
    public void instanceShouldBeEqualToItSelf() {
        assertEquals(exampleBranch, exampleBranch);
    }

    @Test
    public void instanceShouldNotBeEqualToNullOrDifferentType() {
        assertFalse(exampleBranch.equals(null));
        assertFalse(exampleBranch.equals("foo"));
    }

    @Test
    public void instancesWithEqualPropertiesShouldBeEqual() {
        final Branch other = new BranchImpl(leftOperand, OperatorType.NE, rightOperand, 1234);

        assertEquals(other, exampleBranch);
        assertEquals(other.hashCode(), exampleBranch.hashCode());
    }

    @Test
    public void branchWithMetaDataCanBeCreated() {
        final ElementMetaData metaData = mock(ElementMetaData.class);

        assertNotNull(new BranchImpl(leftOperand, OperatorType.EQ, rightOperand, 1234).getMetaData());
        assertEquals(metaData, new BranchImpl(leftOperand, OperatorType.EQ, rightOperand, 1234, metaData).getMetaData());
    }
}
