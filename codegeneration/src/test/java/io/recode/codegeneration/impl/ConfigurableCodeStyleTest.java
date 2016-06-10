package io.recode.codegeneration.impl;

import io.recode.codegeneration.CodeStyle;
import org.junit.Test;

import static io.recode.codegeneration.impl.TestUtils.assertThrown;
import static org.junit.Assert.*;

public class ConfigurableCodeStyleTest {

    @Test
    public void codeStyleWithOmitThisCanBeConfigured() {
        final CodeStyle codeStyle = new ConfigurableCodeStyle.Builder()
                .setShouldOmitThis(true)
                .build();

        assertTrue(codeStyle.shouldOmitThis());
    }

    @Test
    public void codeStyleWithNotOmitThisCanBeConfigured() {
        final CodeStyle codeStyle = new ConfigurableCodeStyle.Builder()
                .setShouldOmitThis(false)
                .build();

        assertFalse(codeStyle.shouldOmitThis());
    }

    @Test
    public void codeStyleWithSimpleTypeNamesCanBeConfigured() {
        final CodeStyle codeStyle = new ConfigurableCodeStyle.Builder()
                .setUseSimpleClassNames(true)
                .build();

        assertEquals("String", codeStyle.getTypeName(String.class));
    }

    @Test
    public void codeStyleWithQualifiedTypeNamesCanBeConfigured() {
        final CodeStyle codeStyle = new ConfigurableCodeStyle.Builder()
                .setUseSimpleClassNames(false)
                .build();

        assertEquals("java.lang.String", codeStyle.getTypeName(String.class));
    }

    @Test
    public void getTypeNameShouldNeverAcceptNullType() {
        assertThrown(() -> new ConfigurableCodeStyle.Builder().build().getTypeName(null), AssertionError.class);
    }
}
