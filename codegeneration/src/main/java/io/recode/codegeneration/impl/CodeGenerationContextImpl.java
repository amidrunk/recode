package io.recode.codegeneration.impl;

import io.recode.TypeResolver;
import io.recode.classfile.ClassFileResolver;
import io.recode.decompile.CodePointer;
import io.recode.decompile.Decompiler;
import io.recode.codegeneration.CodeGenerationContext;
import io.recode.codegeneration.CodeGenerationDelegate;
import io.recode.codegeneration.CodeStyle;

public final class CodeGenerationContextImpl implements CodeGenerationContext {

    private final CodeGenerationDelegate codeGenerationDelegate;

    private final CodeStyle codeStyle;

    private final TypeResolver typeResolver;

    private final ClassFileResolver classFileResolver;

    private final Decompiler decompiler;

    private final int indentationLevel;

    public CodeGenerationContextImpl(CodeGenerationDelegate codeGenerationDelegate,
                                     TypeResolver typeResolver,
                                     ClassFileResolver classFileResolver,
                                     Decompiler decompiler,
                                     CodeStyle codeStyle) {
        this(codeGenerationDelegate, typeResolver, classFileResolver, decompiler, codeStyle, 0);
    }

    private CodeGenerationContextImpl(CodeGenerationDelegate codeGenerationDelegate,
                                      TypeResolver typeResolver,
                                      ClassFileResolver classFileResolver,
                                      Decompiler decompiler,
                                      CodeStyle codeStyle,
                                      int indentationLevel) {
        assert codeGenerationDelegate != null : "Code generation delegate can't be null";
        assert typeResolver != null : "Type resolver can't be null";
        assert decompiler != null : "Decompiler can't be null";
        assert classFileResolver != null : "Class file resolver can't be null";
        assert codeStyle != null : "Code style can't be null";

        this.indentationLevel = indentationLevel;
        this.typeResolver = typeResolver;
        this.classFileResolver = classFileResolver;
        this.decompiler = decompiler;
        this.codeStyle = codeStyle;
        this.codeGenerationDelegate = codeGenerationDelegate;
    }

    @Override
    public int getIndentationLevel() {
        return indentationLevel;
    }

    @Override
    public CodeGenerationContext subSection() {
        return new CodeGenerationContextImpl(
                codeGenerationDelegate,
                typeResolver,
                classFileResolver,
                decompiler,
                codeStyle,
                indentationLevel + 1);
    }

    @Override
    public void delegate(CodePointer codePointer) {
        assert codePointer != null : "Code pointer can't be null";

        codeGenerationDelegate.delegate(this, codePointer);
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    public ClassFileResolver getClassFileResolver() {
        return classFileResolver;
    }

    @Override
    public CodeStyle getCodeStyle() {
        return codeStyle;
    }

    @Override
    public Decompiler getDecompiler() {
        return decompiler;
    }
}
