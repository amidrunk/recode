package io.recode.decompile;

import io.recode.classfile.ByteCode;
import io.recode.model.Statement;
import org.junit.Test;

import java.io.IOException;

import static io.recode.decompile.DecompilationContextQueries.lastStatement;
import static io.recode.test.Assertions.assertThrown;
import static io.recode.util.Sequences.emptySequence;
import static io.recode.util.Sequences.sequenceOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DecompilerDelegatesTest {

    private final DecompilerElementDelegate transformation = mock(DecompilerElementDelegate.class);
    private final DecompilationContext context = mock(DecompilationContext.class);
    private final CodeStream codeStream = mock(CodeStream.class);

    @Test
    public void forQueryShouldNotAcceptNullModelQuery() {
        assertThrown(() -> DecompilerDelegates.forQuery(null), AssertionError.class);
    }

    @Test
    public void forQueryShouldNotAcceptNullTransformation() {
        assertThrown(() -> DecompilerDelegates.forQuery(lastStatement()).apply(null), AssertionError.class);
    }

    @Test
    public void forQueryShouldApplyTransformationIfQueryMatches() throws IOException {
        final DecompilerDelegate delegate = DecompilerDelegates.forQuery(lastStatement()).apply(transformation);
        final Statement statement = mock(Statement.class);

        when(context.getStatements()).thenReturn(sequenceOf(statement));

        delegate.apply(context, codeStream, ByteCode.nop);

        verify(transformation).apply(eq(context), eq(codeStream), eq(ByteCode.nop), eq(statement));
    }

    @Test
    public void forQueryShouldNotApplyTransformationIfQueryDoesNotMatch() throws IOException {
        final DecompilerDelegate delegate = DecompilerDelegates.forQuery(lastStatement()).apply(transformation);

        when(context.getStatements()).thenReturn(emptySequence());

        delegate.apply(context, codeStream, ByteCode.nop);

        verifyZeroInteractions(transformation);
    }
}