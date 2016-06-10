package io.recode.decompile.impl;

import io.recode.RuntimeTypeResolver;
import io.recode.classfile.ByteCode;
import io.recode.classfile.ClassFile;
import io.recode.classfile.ConstantPool;
import io.recode.classfile.Method;
import io.recode.classfile.impl.FieldRefDescriptorImpl;
import io.recode.decompile.CodeStream;
import io.recode.decompile.DecompilationContext;
import io.recode.decompile.DecompilerConfiguration;
import io.recode.decompile.DecompilerConfigurationBuilder;
import io.recode.model.AST;
import io.recode.model.Constant;
import io.recode.model.Expression;
import io.recode.model.LocalVariableReference;
import io.recode.model.impl.FieldAssignmentImpl;
import io.recode.model.impl.FieldReferenceImpl;
import io.recode.util.Iterators;
import io.recode.util.SingleThreadedStack;
import io.recode.util.Stack;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static io.recode.decompile.impl.FieldInstructions.putfield;
import static io.recode.decompile.impl.FieldInstructions.putstatic;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class FieldInstructionsTest {

    private final DecompilationContext context = mock(DecompilationContext.class);
    private final CodeStream codeStream = mock(CodeStream.class);
    private final Method exampleMethod = mock(Method.class);
    private final ClassFile exampleClassFile = mock(ClassFile.class);
    private final ConstantPool constantPool = mock(ConstantPool.class);
    private final Stack<Expression> stack = new SingleThreadedStack<>();

    @Before
    public void setup() {
        when(context.getMethod()).thenReturn(exampleMethod);
        when(exampleMethod.getClassFile()).thenReturn(exampleClassFile);
        when(exampleClassFile.getConstantPool()).thenReturn(constantPool);
        when(context.getStack()).thenReturn(stack);

        doAnswer(i -> new RuntimeTypeResolver().resolveType(((String) i.getArguments()[0]).replace('/', '.')))
                .when(context).resolveType(anyString());
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> new FieldInstructions().configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForByteCodes() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.putfield));
        assertNotNull(it.getDecompilerDelegate(mock(DecompilationContext.class), ByteCode.putstatic));
    }

    @Test
    public void putfieldExtensionShouldPopOperandsAndPushAssignment() throws IOException {
        final Expression assignedFieldValue = AST.constant("foo");
        final Expression declaringInstance = AST.constant("bar");

        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/Integer", "Ljava/lang/String;", "foo"));
        when(context.pop()).thenReturn(assignedFieldValue, declaringInstance);

        putfield().apply(context, CodeStreamTestUtils.codeStream(0, 1), ByteCode.putfield);

        verify(context).enlist(eq(new FieldAssignmentImpl(
                new FieldReferenceImpl(declaringInstance, Integer.class, String.class, "foo"),
                assignedFieldValue)
        ));
    }

    @Test
    public void putstaticExtensionShouldPopValueAndPushAssignment() throws IOException {
        final Constant value = AST.constant("foo");

        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/Integer", "Ljava/lang/String;", "foo"));
        when(context.pop()).thenReturn(value);

        putstatic().apply(context, CodeStreamTestUtils.codeStream(0, 1), ByteCode.putstatic);

        verify(context).enlist(eq(new FieldAssignmentImpl(
                new FieldReferenceImpl(null, Integer.class, String.class, "foo"),
                value
        )));
    }

    @Test
    public void getfieldShouldPushFieldReference() throws IOException {
        final LocalVariableReference local = AST.local("foo", String.class, 1);

        stack.push(local);

        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/String", "I", "bar"));

        execute(ByteCode.getfield, 0, 1);

        assertEquals(Arrays.asList(AST.field(local, int.class, "bar")), Iterators.toList(stack.iterator()));
    }

    @Test
    public void getstaticShouldPushFieldReference() throws IOException {
        when(constantPool.getFieldRefDescriptor(eq(1))).thenReturn(new FieldRefDescriptorImpl("java/lang/String", "I", "bar"));

        execute(ByteCode.getstatic, 0, 1);

        assertEquals(Arrays.asList(AST.field(String.class, int.class, "bar")), Iterators.toList(stack.iterator()));
    }

    private void execute(int byteCode, int ... code) throws IOException {
        configuration().getDecompilerDelegate(context, byteCode)
                .apply(context, CodeStreamTestUtils.codeStream(code), byteCode);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder configurationBuilder = DecompilerConfigurationImpl.newBuilder();

        new FieldInstructions().configure(configurationBuilder);

        return configurationBuilder.build();
    }
}
