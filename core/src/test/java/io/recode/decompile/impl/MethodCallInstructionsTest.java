package io.recode.decompile.impl;

import io.recode.RuntimeTypeResolver;
import io.recode.TypeResolver;
import io.recode.classfile.*;
import io.recode.classfile.impl.DefaultConstantPool;
import io.recode.decompile.*;
import io.recode.model.Expression;
import io.recode.model.MethodSignature;
import io.recode.model.impl.ConstantImpl;
import io.recode.model.impl.LocalVariableReferenceImpl;
import io.recode.model.impl.MethodCallImpl;
import io.recode.util.Iterators;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static io.recode.model.AST.call;
import static io.recode.model.AST.constant;
import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodCallInstructionsTest {

    private final Decompiler decompiler = mock(Decompiler.class);
    private final Method method = mock(Method.class);
    private final ClassFile classFile = mock(ClassFile.class);
    private final ProgramCounterImpl pc = new ProgramCounterImpl(-1);
    private final TypeResolver typeResolver = new RuntimeTypeResolver();
    private final LineNumberCounter lineNumberCounter = mock(LineNumberCounter.class);
    private final DecompilationContext context = new DecompilationContextImpl(decompiler, method, pc, lineNumberCounter,typeResolver, 0);

    @Before
    public void setup() {
        when(method.getClassFile()).thenReturn(classFile);
    }

    @Test
    public void configureShouldNotAcceptNullConfigurationBuilder() {
        assertThrown(() -> new MethodCallInstructions().configure(null), AssertionError.class);
    }

    @Test
    public void configureShouldConfigureSupportForMethodCalls() {
        final DecompilerConfiguration it = configuration();

        assertNotNull(it.getDecompilerDelegate(context, ByteCode.invokeinterface));
        assertNotNull(it.getDecompilerDelegate(context, ByteCode.invokespecial));
    }

    @Test
    public void invokeInterfaceWithNoArgsAndNoReturnShouldPushMethodCall() {
        final ConstantPool constantPool = interfaceMethodRefPool("java/lang/String", "myMethod", "()V");
        final LocalVariableReferenceImpl instance = new LocalVariableReferenceImpl("myVariable", String.class, 1);

        context.push(instance);

        decompile(constantPool, in(ByteCode.invokeinterface, 0, 1, 1), MethodCallInstructions.invokeinterface());

        assertArrayEquals(new Object[]{
                new MethodCallImpl(
                        String.class,
                        "myMethod",
                        MethodSignature.parse("()V"),
                        instance,
                        new Expression[0])
        }, context.getStackedExpressions().toArray());
    }

    @Test
    public void invokeInterfaceShouldFailIfSubsequentByteIsZero() {
        final ConstantPool constantPool = interfaceMethodRefPool("java/lang/String", "myMethod", "()V");

        context.push(new ConstantImpl("foo", String.class));

        assertThrown(() -> {
            decompile(constantPool, in(ByteCode.invokeinterface, 0, 1, 0), MethodCallInstructions.invokeinterface());
        }, ClassFileFormatException.class);
    }

    @Test
    public void invokeInterfaceWithArgumentsShouldPushMethodCallWithArgs() {
        final ConstantPool constantPool = interfaceMethodRefPool("java/lang/String", "myMethod", "(Ljava/lang/String;)V");

        final LocalVariableReferenceImpl instance = new LocalVariableReferenceImpl("this", String.class, 0);
        final ConstantImpl arg1 = new ConstantImpl(1234, int.class);

        context.push(instance);
        context.push(arg1);

        decompile(constantPool, in(ByteCode.invokeinterface, 0, 1, 1), MethodCallInstructions.invokeinterface());

        assertArrayEquals(new Object[]{
                new MethodCallImpl(
                        String.class,
                        "myMethod",
                        MethodSignature.parse("(Ljava/lang/String;)V"),
                        instance,
                        new Expression[]{arg1})
        }, context.getStackedExpressions().toArray());
    }

    @Test
    public void invokeVirtualWithoutArgsShouldPushMethodCallWithNoArgs() {
        final ConstantPool constantPool = methodRefPool("java/lang/String", "toString", "()Ljava/lang/String;");
        final Expression target = constant("foo");

        context.getStack().push(target);

        decompile(constantPool, in(ByteCode.invokevirtual, 0, 1), configuration().getDecompilerDelegate(context, ByteCode.invokevirtual));

        assertEquals(Arrays.asList(call(target, "toString", String.class)), Iterators.toList(context.getStack().iterator()));
    }

    @Test
    public void invokeVirtualWithArgumentsShouldPushMethodCallWithArgs() {
        final ConstantPool constantPool = methodRefPool("java/lang/String", "substring", "(II)Ljava/lang/String;");
        final Expression target = constant("foo");

        context.getStack().push(target);
        context.getStack().push(constant(1234));
        context.getStack().push(constant(2345));

        decompile(constantPool, in(ByteCode.invokevirtual, 0, 1), configuration().getDecompilerDelegate(context, ByteCode.invokevirtual));

        assertEquals(Arrays.asList(call(target, "substring", String.class, constant(1234), constant(2345))), Iterators.toList(context.getStack().iterator()));
    }

    @Test
    public void invokeStaticWithNoArgumentsShouldPushMethodCallToStack() {
        final ConstantPool constantPool = methodRefPool("java/lang/String", "empty", "()Ljava/lang/String;");

        decompile(constantPool, in(ByteCode.invokestatic, 0, 1), configuration().getDecompilerDelegate(context, ByteCode.invokestatic));

        assertEquals(Arrays.asList(call(String.class, "empty", String.class)), Iterators.toList(context.getStack().iterator()));
    }

    @Test
    public void invokeStaticWithArgumentsShouldPushMethodCallToStack() {
        final ConstantPool constantPool = methodRefPool("java/lang/String", "valueOf", "(I)Ljava/lang/String;");

        context.getStack().push(constant(1234));

        decompile(constantPool, in(ByteCode.invokestatic, 0, 1), configuration().getDecompilerDelegate(context, ByteCode.invokestatic));

        assertEquals(Arrays.asList(call(String.class, "valueOf", String.class, constant(1234))), Iterators.toList(context.getStack().iterator()));
    }

    private DefaultConstantPool methodRefPool(String declaringClass, String name, String signature) {
        return new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.MethodRefEntry(2, 3))
                .addEntry(new ConstantPoolEntry.ClassEntry(4))
                .addEntry(new ConstantPoolEntry.NameAndTypeEntry(5, 6))
                .addEntry(new ConstantPoolEntry.UTF8Entry(declaringClass))
                .addEntry(new ConstantPoolEntry.UTF8Entry(name))
                .addEntry(new ConstantPoolEntry.UTF8Entry(signature))
                .create();
    }

    private DefaultConstantPool interfaceMethodRefPool(String declaringClass, String name, String signature) {
        return new DefaultConstantPool.Builder()
                .addEntry(new ConstantPoolEntry.InterfaceMethodRefEntry(2, 3))
                .addEntry(new ConstantPoolEntry.ClassEntry(4))
                .addEntry(new ConstantPoolEntry.NameAndTypeEntry(5, 6))
                .addEntry(new ConstantPoolEntry.UTF8Entry(declaringClass))
                .addEntry(new ConstantPoolEntry.UTF8Entry(name))
                .addEntry(new ConstantPoolEntry.UTF8Entry(signature))
                .create();
    }

    private DecompilationContext decompile(ConstantPool constantPool, InputStream in, DecompilerDelegate extension) {
        when(classFile.getConstantPool()).thenReturn(constantPool);

        final InputStreamCodeStream cs = new InputStreamCodeStream(in, pc);

        try {
            extension.apply(context, cs, cs.nextInstruction());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return context;
    }

    private InputStream in(int ... byteCodes) {
        final byte[] buf = new byte[byteCodes.length];

        for (int i = 0; i < byteCodes.length; i++) {
            buf[i] = (byte) byteCodes[i];
        }

        return new ByteArrayInputStream(buf);
    }

    private DecompilerConfiguration configuration() {
        final DecompilerConfigurationBuilder builder = DecompilerConfigurationImpl.newBuilder();

        new MethodCallInstructions().configure(builder);

        return builder.build();
    }
}
