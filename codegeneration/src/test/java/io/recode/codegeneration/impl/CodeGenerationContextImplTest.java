package io.recode.codegeneration.impl;

import io.recode.TypeResolver;
import io.recode.classfile.ClassFileResolver;
import io.recode.codegeneration.CodeGenerationContext;
import io.recode.codegeneration.CodeGenerationDelegate;
import io.recode.codegeneration.CodeStyle;
import io.recode.decompile.CodePointer;
import io.recode.decompile.Decompiler;
import org.junit.Test;

import static io.recode.codegeneration.impl.TestUtils.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CodeGenerationContextImplTest {

    private final CodeGenerationDelegate codeGenerationDelegate = mock(CodeGenerationDelegate.class);

    private final TypeResolver typeResolver = mock(TypeResolver.class);

    private final ClassFileResolver classFileResolver = mock(ClassFileResolver.class);

    private final CodeStyle codeStyle = mock(CodeStyle.class);

    private final Decompiler decompiler = mock(Decompiler.class);
    private final CodeGenerationContextImpl context = new CodeGenerationContextImpl(
            codeGenerationDelegate,
            typeResolver,
            classFileResolver,
            decompiler,
            codeStyle);

    @Test
    public void constructorShouldNotAcceptInvalidArguments() {
        assertThrown(() -> new CodeGenerationContextImpl(null, typeResolver, classFileResolver, decompiler, codeStyle), AssertionError.class);
        assertThrown(() -> new CodeGenerationContextImpl(codeGenerationDelegate, null, classFileResolver, decompiler, codeStyle), AssertionError.class);
        assertThrown(() -> new CodeGenerationContextImpl(codeGenerationDelegate, typeResolver, null, decompiler, codeStyle), AssertionError.class);
        assertThrown(() -> new CodeGenerationContextImpl(codeGenerationDelegate, typeResolver, classFileResolver, null, codeStyle), AssertionError.class);
        assertThrown(() -> new CodeGenerationContextImpl(codeGenerationDelegate, typeResolver, classFileResolver, decompiler, null), AssertionError.class);
    }

    @Test
    public void constructorShouldRetainCodeStyleAndDependencies() {
        assertEquals(codeStyle, context.getCodeStyle());
        assertEquals(classFileResolver, context.getClassFileResolver());
        assertEquals(typeResolver, context.getTypeResolver());
        assertEquals(decompiler, context.getDecompiler());
    }

    @Test
    public void indentationShouldInitiallyBeZero() {
        assertEquals(0, context.getIndentationLevel());
    }

    @Test
    public void delegateShouldNotAcceptNullArgument() {
        assertThrown(() -> context.delegate(null), AssertionError.class);
    }

    @Test
    public void delegateShouldCallCodeGenerationDelegate() {
        final CodePointer codePointer = mock(CodePointer.class);

        context.delegate(codePointer);

        verify(codeGenerationDelegate).delegate(eq(context), eq(codePointer));
    }

    @Test
    public void subSectionShouldReturnContextWithIncreasedIndentation() {
        final CodeGenerationContext newContext = context.subSection();

        assertEquals(1, newContext.getIndentationLevel());
    }
}
