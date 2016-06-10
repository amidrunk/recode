package io.recode.classfile;

import io.recode.classfile.ByteCode;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.recode.classfile.ByteCode.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ByteCodeTest {

    @Test
    public void isValidShouldBeFalseForInvalidInstructions() {
        assertFalse(ByteCode.isValid(-1));
        assertFalse(ByteCode.isValid(256));
    }

    @Test
    public void isValidShouldBeTrueForValidInstructions() {
        for (int i = 0; i < 256; i++) {
            assertTrue(ByteCode.isValid(i));
        }
    }

    @Test
    @Ignore("Make this fail and fix the error")
    public void isLoadInstructionShouldReturnTrueForLoad() {
        final int[] instructions = {
                iload,
                ByteCode.iload_0,
                ByteCode.iload_1,
                ByteCode.iload_2,
                ByteCode.iload_3
        };

        for (int instruction : instructions) {
            assertTrue(ByteCode.isLoadInstruction(instruction));
        }
    }

    @Test
    public void loadInstructionsShouldReturnAllAvailableLoadInstructions() {
        final List<Integer> instructions = new ArrayList<>();

        for (int instruction : ByteCode.loadInstructions()) {
            instructions.add(instruction);
        }

        assertTrue(instructions.containsAll(Arrays.asList(iload, iload_0, iload_1, iload_2, iload_3)));
        assertTrue(instructions.containsAll(Arrays.asList(fload, fload_0, fload_1, fload_2, fload_3)));
        assertTrue(instructions.containsAll(Arrays.asList(dload, dload_0, dload_1, dload_2, dload_3)));
        assertTrue(instructions.containsAll(Arrays.asList(lload, lload_0, lload_1, lload_2, lload_3)));
        assertTrue(instructions.containsAll(Arrays.asList(aload, aload_0, aload_1, aload_2, aload_3)));
    }

    @Test
    public void primitiveLoadInstructionsShouldReturnAllAvailableLoadInstructions() {
        final List<Integer> instructions = new ArrayList<>();

        for (int instruction : ByteCode.primitiveLoadInstructions()) {
            instructions.add(instruction);
        }

        assertTrue(instructions.containsAll(Arrays.asList(iload, iload_0, iload_1, iload_2, iload_3)));
        assertTrue(instructions.containsAll(Arrays.asList(fload, fload_0, fload_1, fload_2, fload_3)));
        assertTrue(instructions.containsAll(Arrays.asList(dload, dload_0, dload_1, dload_2, dload_3)));
        assertTrue(instructions.containsAll(Arrays.asList(lload, lload_0, lload_1, lload_2, lload_3)));
        assertFalse(instructions.containsAll(Arrays.asList(aload, aload_0, aload_1, aload_2, aload_3)));
    }
}