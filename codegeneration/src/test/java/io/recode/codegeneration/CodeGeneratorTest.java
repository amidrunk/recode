package io.recode.codegeneration;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class CodeGeneratorTest {

    @Test
    public void generateCodeToStringShouldGenerateCodeAndReturnTextDescription() {
        final CodeGenerator codeGenerator = (e, out) -> out.print("bar");
        final String description = codeGenerator.generateCode("foo", StandardCharsets.UTF_8);

        assertEquals("bar", description);
    }
}
