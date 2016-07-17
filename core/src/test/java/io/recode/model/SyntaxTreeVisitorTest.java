package io.recode.model;

import io.recode.classfile.ReferenceKind;
import io.recode.model.impl.*;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static io.recode.model.AST.constant;
import static io.recode.model.AST.local;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class SyntaxTreeVisitorTest {

    private final SyntaxTreeVisitor.Callback callback = mock(SyntaxTreeVisitor.Callback.class);

    @Test
    public void visitShouldNotAcceptInvalidParameters() {
        assertThrown(() -> SyntaxTreeVisitor.visit(null, callback), AssertionError.class);
        assertThrown(() -> SyntaxTreeVisitor.visit(constant(1), null), AssertionError.class);
    }

    @Test
    public void constantCanBeVisited() {
        final Element element = constant("foo");

        SyntaxTreeVisitor.visit(element, callback);

        verify(callback).visit(any(), eq(element));
    }

    @Test
    public void returnValueCanBeVisited() {
        final Constant value = constant("foo");
        final ReturnValue returnValue = AST.$return(value);

        SyntaxTreeVisitor.visit(returnValue, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(value));
        inOrder.verify(callback).visit(any(), eq(returnValue));
    }

    @Test
    public void binaryOperatorCanBeVisited() {
        final Expression left = constant(1);
        final Expression right = constant(2);
        final BinaryOperator operator = AST.plus(left, right);

        SyntaxTreeVisitor.visit(operator, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(left));
        inOrder.verify(callback).visit(any(), eq(right));
        inOrder.verify(callback).visit(any(), eq(operator));
    }

    @Test
    public void returnCanBeVisited() {
        final Element return_ = AST.$return();

        SyntaxTreeVisitor.visit(return_, callback);

        verify(callback).visit(any(), eq(return_));
    }

    @Test
    public void variableReferenceCanBeVisited() {
        final LocalVariableReference reference = AST.local("foo", String.class, 1);

        SyntaxTreeVisitor.visit(reference, callback);

        verify(callback).visit(any(), eq(reference));
    }

    @Test
    public void instanceMethodCallCanBeVisited() {
        final LocalVariableReference instance = AST.local("foo", String.class, 1);
        final Constant param1 = constant(1);
        final MethodCall methodCall = AST.call(instance, "bar", String.class, param1);

        SyntaxTreeVisitor.visit(methodCall, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(instance));
        inOrder.verify(callback).visit(any(), eq(param1));
        inOrder.verify(callback).visit(any(), eq(methodCall));
    }

    @Test
    public void staticMethodCallCanBeVisited() {
        final Constant param1 = constant(1);
        final MethodCall methodCall = AST.call(String.class, "bar", String.class, param1);

        SyntaxTreeVisitor.visit(methodCall, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(param1));
        inOrder.verify(callback).visit(any(), eq(methodCall));
    }

    @Test
    public void instanceFieldReferenceCanBeVisited() {
        final LocalVariableReference instance = AST.local("foo", String.class, 1);
        final FieldReference reference = AST.field(instance, String.class, "myField");

        SyntaxTreeVisitor.visit(reference, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(instance));
        inOrder.verify(callback).visit(any(), eq(reference));
    }

    @Test
    public void staticFieldReferenceCanBeVisited() {
        final FieldReference reference = AST.field(String.class, int.class, "myField");

        SyntaxTreeVisitor.visit(reference, callback);

        verify(callback).visit(any(), eq(reference));
    }

    @Test
    public void variableAssignmentCanBeVisited() {
        final Constant value = constant("bar");
        final VariableAssignment assignment = AST.set(0, "foo", value);

        SyntaxTreeVisitor.visit(assignment, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(value));
        inOrder.verify(callback).visit(any(), eq(assignment));
    }

    @Test
    public void lambdaCanBeVisited() {
        final Expression self = AST.local("me", String.class, 1);
        final Lambda lambda = new LambdaImpl(Optional.of(self), ReferenceKind.GET_STATIC, Runnable.class, "run", MethodSignature.parse("()V"), getClass(), "foo", MethodSignature.parse("()V"), Collections.emptyList());

        SyntaxTreeVisitor.visit(lambda, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(self));
        inOrder.verify(callback).visit(any(), eq(lambda));
    }

    @Test
    public void branchCanBeVisited() {
        final Constant left = constant(1);
        final Constant right = constant(2);
        final Branch branch = new BranchImpl(left, OperatorType.EQ, right, 1234);

        SyntaxTreeVisitor.visit(branch, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(left));
        inOrder.verify(callback).visit(any(), eq(right));
        inOrder.verify(callback).visit(any(), eq(branch));
    }

    @Test
    public void newCanBeVisited() {
        final Constant param1 = constant(1);
        final Constant param2 = constant(2);
        final NewInstance newInstance = AST.newInstance(String.class, param1, param2);

        SyntaxTreeVisitor.visit(newInstance, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(param1));
        inOrder.verify(callback).visit(any(), eq(param2));
        inOrder.verify(callback).visit(any(), eq(newInstance));
    }

    @Test
    public void newArrayCanBeVisited() {
        final Constant length = constant(4);
        final Constant element1 = constant("foo");
        final Constant element2 = constant("bar");
        final NewArray newArray = new NewArrayImpl(String[].class, String.class, length, Arrays.<ArrayInitializer>asList(new ArrayInitializerImpl(0, element1), new ArrayInitializerImpl(1, element2)));

        SyntaxTreeVisitor.visit(newArray, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(length));
        inOrder.verify(callback).visit(any(), eq(element1));
        inOrder.verify(callback).visit(any(), eq(element2));
        inOrder.verify(callback).visit(any(), eq(newArray));
    }

    @Test
    public void arrayStoreCanBeVisited() {
        final LocalVariableReference array = local("foo", String[].class, 1);
        final Constant index = constant(1);
        final Constant value = constant("bar");
        final ArrayStore arrayStore = new ArrayStoreImpl(array, index, value);

        SyntaxTreeVisitor.visit(arrayStore, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(array));
        inOrder.verify(callback).visit(any(), eq(index));
        inOrder.verify(callback).visit(any(), eq(value));
        inOrder.verify(callback).visit(any(), eq(arrayStore));
    }

    @Test
    public void instanceFieldAssignmentCanBeVisited() {
        final FieldReferenceImpl fieldReference = new FieldReferenceImpl(constant("foo"), String.class, int.class, "length");
        final Constant value = constant(1234);
        final FieldAssignment assignment = new FieldAssignmentImpl(fieldReference, value);

        SyntaxTreeVisitor.visit(assignment, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(fieldReference.getTargetInstance().get()));
        inOrder.verify(callback).visit(any(), eq(fieldReference));
        inOrder.verify(callback).visit(any(), eq(value));
        inOrder.verify(callback).visit(any(), eq(assignment));
    }

    @Test
    public void staticFieldAssignmentCanBeVisited() {
        final FieldReferenceImpl fieldReference = new FieldReferenceImpl(null, String.class, int.class, "length");
        final Constant value = constant(1234);
        final FieldAssignment assignment = new FieldAssignmentImpl(fieldReference, value);

        SyntaxTreeVisitor.visit(assignment, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(fieldReference));
        inOrder.verify(callback).visit(any(), eq(value));
        inOrder.verify(callback).visit(any(), eq(assignment));
    }

    @Test
    public void castCanBeVisited() {
        final Constant constant = constant("foo");
        final TypeCast typeCast = AST.cast(constant).to(String.class);

        SyntaxTreeVisitor.visit(typeCast, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(constant));
        inOrder.verify(callback).visit(any(), eq(typeCast));
    }

    @Test
    public void arrayLoadCanBeVisited() {
        final LocalVariableReference array = local("array", String[].class, 0);
        final Constant index = constant(1);
        final ArrayLoadImpl arrayLoad = new ArrayLoadImpl(array, index, String.class);

        SyntaxTreeVisitor.visit(arrayLoad, callback);

        final InOrder inOrder = Mockito.inOrder(callback);

        inOrder.verify(callback).visit(any(), eq(array));
        inOrder.verify(callback).visit(any(), eq(index));
        inOrder.verify(callback).visit(any(), eq(arrayLoad));
    }

    @Test
    public void allocateCanBeVisited() {
        final InstanceAllocationImpl allocateInstance = new InstanceAllocationImpl(String.class);

        SyntaxTreeVisitor.visit(allocateInstance, callback);

        verify(callback).visit(any(), eq(allocateInstance));
    }

    @Test
    public void searchCanBeAborted() {
        final Constant constant = constant(1);
        final VariableAssignment assignment = AST.set(0, "myVar", constant);

        doAnswer(i -> {
            ((SyntaxTreeVisitor.Walker) i.getArguments()[0]).abort();
            return null;
        }).when(callback).visit(any(), eq(constant));

        SyntaxTreeVisitor.visit(assignment, callback);

        verify(callback).visit(any(), eq(constant));
        verify(callback, times(0)).visit(any(), eq(assignment));
        verifyNoMoreInteractions(callback);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchShouldNotAcceptInvalidArguments() {
        final Predicate predicate = mock(Predicate.class);

        assertThrown(() -> SyntaxTreeVisitor.search((Element) null, predicate), AssertionError.class);
        assertThrown(() -> SyntaxTreeVisitor.search(AST.constant(1), null), AssertionError.class);
    }

    @Test
    public void searchShouldReturnEmptyOptionalIfNoMatchingElementIsFound() {
        final Optional<Element> optional = SyntaxTreeVisitor.search(AST.constant(1), e -> false);

        assertFalse(optional.isPresent());
    }

    @Test
    public void searchShouldReturnFirstMatchingElement() {
        final Constant left = constant(1);
        final Constant right = constant(2);
        final BinaryOperator operator = AST.plus(left, right);

        final Optional<Element> result = SyntaxTreeVisitor.search(operator, e -> e.getElementType() == ElementType.CONSTANT);

        assertEquals(Optional.of(left), result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchElementsShouldNotAcceptInvalidArguments() {
        assertThrown(() -> SyntaxTreeVisitor.search((Element[]) null, mock(Predicate.class)), AssertionError.class);
        assertThrown(() -> SyntaxTreeVisitor.search(new Element[0], null), AssertionError.class);
    }

    @Test
    public void searchElementsShouldReturnEmptyOptionalIfNoMatchingElementIsFound() {
        final Optional<Element> result = SyntaxTreeVisitor.search(new Element[]{constant(1)}, e -> false);

        assertFalse(result.isPresent());
    }

    @Test
    public void searchElementsShouldReturnMatchingElement() {
        final Optional<Element> result = SyntaxTreeVisitor.search(new Element[]{constant(1), constant(2)}, e -> e.as(Constant.class).getConstant().equals(2));

        assertEquals(Optional.of(constant(2)), result);
    }

    @Test
    public void collectShouldReturnAllMatchingElementsInModel() {
        final Element[] elements = new Element[] {
            AST.constant(1),
            AST.call(String.class, "valueOf", String.class, AST.constant(2)),
            AST.$return(AST.constant(3))
        };

        final List<Element> result = SyntaxTreeVisitor.collect(elements, e -> e.getElementType() == ElementType.CONSTANT);

        assertEquals(Arrays.asList(AST.constant(1), AST.constant(2), AST.constant(3)), result);
    }
}
