package io.recode.codegeneration;

import io.recode.TypeResolver;
import io.recode.classfile.ClassFileResolver;
import io.recode.decompile.CodePointer;
import io.recode.decompile.Decompiler;

/**
 * A <code>CodeGenerationContext</code> is used to encapsulate the process of generating code for a
 * syntax tree. The context contains the positional context of an element and provides access to
 * resources required for decompilation.
 */
public interface CodeGenerationContext {

    /**
     * Returns the current indentation level. This will be increased when a sub-section is created.
     * @return The current indentation level. Used to indent the generated code as per the configured code style.
     */
    int getIndentationLevel();

    /**
     * Creates a sub context. A sub context should be created when a new code block is entered, such as
     * when an if-statement or similar is recreated.
     *
     * @return A new section with a higher indentation level.
     */
    CodeGenerationContext subSection();

    /**
     * Delegates generation of the provided code pointer to a handler valid within this context. This
     * would typically be dispatched back to the original code generator.
     *
     * @param codePointer The code pointer that should be generated.
     */
    void delegate(CodePointer codePointer);

    TypeResolver getTypeResolver();

    ClassFileResolver getClassFileResolver();

    CodeStyle getCodeStyle();

    Decompiler getDecompiler();

}
