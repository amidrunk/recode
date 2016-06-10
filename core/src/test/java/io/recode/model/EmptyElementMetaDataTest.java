package io.recode.model;

import org.junit.Test;

import static io.recode.test.Assertions.assertThrown;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class EmptyElementMetaDataTest {

    @Test
    public void instanceShouldNotContainProperties() {
        final EmptyElementMetaData instance = new EmptyElementMetaData();

        assertFalse(instance.hasLineNumber());
        assertThrown(() -> instance.getLineNumber(), IllegalStateException.class);
        assertFalse(instance.hasProgramCounter());
        assertThrown(() -> instance.getProgramCounter(), IllegalStateException.class);
    }
}