package io.recode.decompile;

import io.recode.Caller;
import io.recode.CodeLocation;
import io.recode.classfile.ClassFile;
import io.recode.classfile.ClassPathClassFileResolver;
import io.recode.classfile.Method;
import io.recode.classfile.impl.ClassFileReaderImpl;
import io.recode.decompile.impl.DecompilerImpl;
import io.recode.model.AST;
import io.recode.model.Element;
import io.recode.test.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultCodeRangeDecompilerTest {

    private final DefaultCodeRangeDecompiler defaultCodeRangeDecompiler = new DefaultCodeRangeDecompiler(new DecompilerImpl());

    @Test
    public void linesWithinMethodCanBeDecompiled() throws Exception {
        // code for decompilation
        nop1();
        nop2();
        nop3();

        // test
        final int ref = Caller.me().getLineNumber();
        final Element[] codePointers = defaultCodeRangeDecompiler.decompileRange(getThisMethod(), ref - 5, ref - 2);

        assertEquals(3, codePointers.length);
        assertEquals(AST.call(AST.local("this", getClass(), 0), "nop1", void.class), codePointers[0]);
        assertEquals(AST.call(AST.local("this", getClass(), 0), "nop2", void.class), codePointers[1]);
        assertEquals(AST.call(AST.local("this", getClass(), 0), "nop3", void.class), codePointers[2]);
    }

    @Test
    public void codeRangeNotContainedWithinMethodCannotBeDecompiled() {
        final int ref = Caller.me().getLineNumber();

        assertThrown(() -> defaultCodeRangeDecompiler.decompileRange(getThisMethod(), ref - 3, ref + 1), IllegalArgumentException.class);
        assertThrown(() -> defaultCodeRangeDecompiler.decompileRange(getThisMethod(), ref, ref + 50), IllegalArgumentException.class);
    }

    @Test
    public void methodWithoutLineNumberTableCannotBeDecompiled() throws IOException {
        final Method method = mock(Method.class);

        when(method.getLineNumberTable()).thenReturn(Optional.empty());

        assertThrown(() -> defaultCodeRangeDecompiler.decompileRange(method, 1, 2), IllegalArgumentException.class);
    }

    private Method getThisMethod() {
        final CodeLocation caller = Caller.me().getCaller().get();

        return getThisClassFile().getMethods().stream()
                .filter(m -> m.getName().equals(caller.getMethodName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Method '" + caller.getMethodName() + "' not found in class file"));
    }

    private ClassFile getThisClassFile() {
        return new ClassPathClassFileResolver(new ClassFileReaderImpl()).resolveClassFile(getClass());
    }

    private void nop1() {}
    private void nop2() {}
    private void nop3() {}
}