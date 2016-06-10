package io.recode.codegeneration.impl;

import io.recode.classfile.Method;
import io.recode.codegeneration.*;
import io.recode.decompile.CodePointer;
import io.recode.decompile.impl.CodePointerImpl;
import io.recode.model.Constant;
import io.recode.model.Element;
import io.recode.model.ElementType;
import io.recode.util.Iterators;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;

import static io.recode.codegeneration.impl.TestUtils.assertThrown;
import static io.recode.model.AST.constant;
import static io.recode.model.ElementType.CONSTANT;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class SimpleCodeGeneratorConfigurationTest {

    private final CodeGeneratorConfiguration emptyConfiguration = SimpleCodeGeneratorConfiguration.configurer().configuration();
    private final CodeGenerationContext context = mock(CodeGenerationContext.class);
    private final Method method = mock(Method.class);
    private final CodeGeneratorConfigurer configurer = SimpleCodeGeneratorConfiguration.configurer();
    private final PrintWriter out = mock(PrintWriter.class);

    private final CodeGeneratorDelegate extension1 = mock(CodeGeneratorDelegate.class, "extension1");
    private final CodeGeneratorDelegate extension2 = mock(CodeGeneratorDelegate.class, "extension2");
    private final CodeGeneratorDelegate extension3 = mock(CodeGeneratorDelegate.class, "extension3");

    @Test
    public void getExtensionShouldReturnNullIfConfigurationIsEmpty() {
        assertNull(emptyConfiguration.getDelegate(context, new CodePointerImpl<>(method, constant(0))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void extendInBuilderShouldNotAcceptNullElementTypeOrExtension() {
        assertThrown(() -> configurer.on(null).then(mock(CodeGeneratorDelegate.class)), AssertionError.class);
        assertThrown(() -> configurer.on(ElementSelector.forType(ElementType.BRANCH)).then(null), AssertionError.class);
    }

    @Test
    public void getExtensionShouldNotAcceptNullCodePointer() {
        assertThrown(() -> emptyConfiguration.getDelegate(mock(CodeGenerationContext.class), null), AssertionError.class);
    }

    @Test
    public void getExtensionShouldReturnConfiguredExtension() {
        final CodeGeneratorConfiguration configuration = configurer
                .on(ElementSelector.forType(CONSTANT)).then(extension1)
                .configuration();

        final CodePointerImpl<Constant> codePointer = new CodePointerImpl<>(method, constant(1));
        final CodeGeneratorDelegate extension = configuration.getDelegate(context, codePointer);

        extension.apply(context, codePointer, out);

        verify(extension1).apply(eq(context), eq(codePointer), eq(out));
    }

    @Test
    public void twoExtensionsCanBeConfiguredForTheSameElementWithDifferentSelectors() {
        final CodeGeneratorConfiguration configuration = configurer
                .on(ElementSelector.<Constant>forType(CONSTANT)
                        .where(cp -> cp.getElement().getConstant().equals(1)))
                        .then(extension1)
                .on(ElementSelector.<Constant>forType(CONSTANT)
                        .where(cp -> cp.getElement().getConstant().equals(2)))
                        .then(extension2)
                .configuration();

        final CodePointerImpl codePointerWithConstant1 = new CodePointerImpl(method, constant(1));
        final CodePointerImpl codePointerWithConstant2 = new CodePointerImpl(method, constant(2));

        configuration.getDelegate(context, codePointerWithConstant1).apply(context, codePointerWithConstant1, out);

        verify(extension1).apply(eq(context), eq(codePointerWithConstant1), eq(out));
        verify(extension2, times(0)).apply(eq(context), eq(codePointerWithConstant1), eq(out));

        configuration.getDelegate(context, codePointerWithConstant2).apply(context, codePointerWithConstant2, out);

        verifyNoMoreInteractions(extension1);
        verify(extension2).apply(eq(context), eq(codePointerWithConstant2), eq(out));
    }

    @Test
    public void threeExtensionsCanBeConfiguredOnTehSameElementWithDifferentSelectors() {
        final CodeGeneratorConfiguration configuration = configurer
                .on(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(1)))).then(extension1)
                .on(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(2)))).then(extension2)
                .on(ElementSelector.<Constant>forType(CONSTANT).where(cp -> cp.getElement().equals(constant(3)))).then(extension3)
                .configuration();

        final CodePointerImpl codePointer1 = new CodePointerImpl<>(method, constant(1));
        final CodePointerImpl codePointer2 = new CodePointerImpl<>(method, constant(2));
        final CodePointerImpl codePointer3 = new CodePointerImpl<>(method, constant(3));

        configuration.getDelegate(context, codePointer1).apply(context, codePointer1, out);
        configuration.getDelegate(context, codePointer2).apply(context, codePointer2, out);
        configuration.getDelegate(context, codePointer3).apply(context, codePointer3, out);

        final InOrder inOrder = Mockito.inOrder(extension1, extension2, extension3);

        inOrder.verify(extension1, times(1)).apply(eq(context), eq(codePointer1), eq(out));
        inOrder.verify(extension2, times(1)).apply(eq(context), eq(codePointer2), eq(out));
        inOrder.verify(extension3, times(1)).apply(eq(context), eq(codePointer3), eq(out));

        verifyNoMoreInteractions(extension1, extension2, extension3);
    }

    @Test
    public void singleAroundAdviceCanBeConfigured() {
        final CodeGeneratorAdvice expectedAdvice = mock(CodeGeneratorAdvice.class);

        final CodeGeneratorConfiguration configuration = configurer
                .around(ElementSelector.forType(CONSTANT)).then(expectedAdvice)
                .configuration();

        final CodePointer codePointer = new CodePointerImpl<>(method, constant(1));
        final Iterator<CodeGeneratorAdvice<? extends Element>> advices = configuration.getAdvices(context, codePointer);

        assertEquals(Arrays.asList(expectedAdvice), Iterators.toList(advices));
    }

    @Test
    public void multipleAroundAdvicesCanBeConfigured() {
        final CodeGeneratorAdvice advice1 = mock(CodeGeneratorAdvice.class, "advice1");
        final CodeGeneratorAdvice advice2 = mock(CodeGeneratorAdvice.class, "advice2");

        final CodeGeneratorConfiguration configuration = configurer
                .around(ElementSelector.forType(CONSTANT)).then(advice1)
                .around(ElementSelector.forType(CONSTANT)).then(advice2)
                .configuration();

        final Iterator<CodeGeneratorAdvice<? extends Element>> advices = configuration.getAdvices(context, new CodePointerImpl<>(method, constant(1)));

        assertEquals(Arrays.asList(advice1, advice2), Iterators.toList(advices));
    }

    @Test
    public void getAdvicesShouldNotReturnAdviceIfElementSelectorDoesNotMatch() {
        final CodeGeneratorAdvice expectedAdvice = mock(CodeGeneratorAdvice.class);
        final CodeGeneratorConfiguration configuration = configurer
                .around(ElementSelector.forType(CONSTANT).where(cp -> false)).then(expectedAdvice)
                .configuration();

        final Iterator<CodeGeneratorAdvice<? extends Element>> advices = configuration
                .getAdvices(context, new CodePointerImpl<>(method, constant(1)));

        assertFalse(advices.hasNext());
    }

    @Test
    public void getAdviceShouldReturnEmptyListIfNoAdvicesExists() {
        assertFalse(configurer.configuration().getAdvices(context, new CodePointerImpl<>(method, constant(1))).hasNext());
    }

    @Test
    public void getAdviceShouldNotAcceptInvalidArguments() {
        final CodeGeneratorConfiguration configuration = configurer.configuration();

        assertThrown(() -> configuration.getAdvices(null, mock(CodePointer.class)), AssertionError.class);
        assertThrown(() -> configuration.getAdvices(context, null), AssertionError.class);
    }
}
