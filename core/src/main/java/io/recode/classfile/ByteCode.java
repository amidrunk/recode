package io.recode.classfile;

import java.util.Arrays;

public final class ByteCode {

    public static final int illegal = -1;
    public static final int nop = 0;
    public static final int aconst_null = 1;
    public static final int iconst_m1 = 2;
    public static final int iconst_0 = 3;
    public static final int iconst_1 = 4;
    public static final int iconst_2 = 5;
    public static final int iconst_3 = 6;
    public static final int iconst_4 = 7;
    public static final int iconst_5 = 8;
    public static final int lconst_0 = 9;
    public static final int lconst_1 = 10;
    public static final int fconst_0 = 11;
    public static final int fconst_1 = 12;
    public static final int fconst_2 = 13;
    public static final int dconst_0 = 14;
    public static final int dconst_1 = 15;
    public static final int bipush = 16;
    public static final int sipush = 17;
    public static final int ldc = 18;
    public static final int ldcw = 19;
    public static final int ldc2w = 20;
    public static final int iload = 21;
    public static final int lload = 22;
    public static final int fload = 23;
    public static final int dload = 24;
    public static final int aload = 25;
    public static final int iload_0 = 26;
    public static final int iload_1 = 27;
    public static final int iload_2 = 28;
    public static final int iload_3 = 29;
    public static final int lload_0 = 30;
    public static final int lload_1 = 31;
    public static final int lload_2 = 32;
    public static final int lload_3 = 33;
    public static final int fload_0 = 34;
    public static final int fload_1 = 35;
    public static final int fload_2 = 36;
    public static final int fload_3 = 37;
    public static final int dload_0 = 38;
    public static final int dload_1 = 39;
    public static final int dload_2 = 40;
    public static final int dload_3 = 41;
    public static final int aload_0 = 42;
    public static final int aload_1 = 43;
    public static final int aload_2 = 44;
    public static final int aload_3 = 45;
    public static final int iaload = 46;
    public static final int laload = 47;
    public static final int faload = 48;
    public static final int daload = 49;
    public static final int aaload = 50;
    public static final int baload = 51;
    public static final int caload = 52;
    public static final int saload = 53;
    public static final int istore = 54;
    public static final int lstore = 55;
    public static final int fstore = 56;
    public static final int dstore = 57;
    public static final int astore = 58;
    public static final int istore_0 = 59;
    public static final int istore_1 = 60;
    public static final int istore_2 = 61;
    public static final int istore_3 = 62;
    public static final int lstore_0 = 63;
    public static final int lstore_1 = 64;
    public static final int lstore_2 = 65;
    public static final int lstore_3 = 66;
    public static final int fstore_0 = 67;
    public static final int fstore_1 = 68;
    public static final int fstore_2 = 69;
    public static final int fstore_3 = 70;
    public static final int dstore_0 = 71;
    public static final int dstore_1 = 72;
    public static final int dstore_2 = 73;
    public static final int dstore_3 = 74;
    public static final int astore_0 = 75;
    public static final int astore_1 = 76;
    public static final int astore_2 = 77;
    public static final int astore_3 = 78;
    public static final int iastore = 79;
    public static final int lastore = 80;
    public static final int fastore = 81;
    public static final int dastore = 82;
    public static final int aastore = 83;
    public static final int bastore = 84;
    public static final int castore = 85;
    public static final int sastore = 86;
    public static final int pop = 87;
    public static final int pop2 = 88;
    public static final int dup = 89;
    public static final int dup_x1 = 90;
    public static final int dup_x2 = 91;
    public static final int dup2 = 92;
    public static final int dup2_x1 = 93;
    public static final int dup2_x2 = 94;
    public static final int swap = 95;
    public static final int iadd = 96;
    public static final int ladd = 97;
    public static final int fadd = 98;
    public static final int dadd = 99;
    public static final int isub = 100;
    public static final int lsub = 101;
    public static final int fsub = 102;
    public static final int dsub = 103;
    public static final int imul = 104;
    public static final int lmul = 105;
    public static final int fmul = 106;
    public static final int dmul = 107;
    public static final int idiv = 108;
    public static final int ldiv = 109;
    public static final int fdiv = 110;
    public static final int ddiv = 111;
    public static final int irem = 112;
    public static final int lrem = 113;
    public static final int frem = 114;
    public static final int drem = 115;
    public static final int ineg = 116;
    public static final int lneg = 117;
    public static final int fneg = 118;
    public static final int dneg = 119;
    public static final int ishl = 120;
    public static final int lshl = 121;
    public static final int ishr = 122;
    public static final int lshr = 123;
    public static final int iushr = 124;
    public static final int lushr = 125;
    public static final int iand = 126;
    public static final int land = 127;
    public static final int ior = 128;
    public static final int lor = 129;
    public static final int ixor = 130;
    public static final int lxor = 131;
    public static final int iinc = 132;
    public static final int i2l = 133;
    public static final int i2f = 134;
    public static final int i2d = 135;
    public static final int l2i = 136;
    public static final int l2f = 137;
    public static final int l2d = 138;
    public static final int f2i = 139;
    public static final int f2l = 140;
    public static final int f2d = 141;
    public static final int d2i = 142;
    public static final int d2l = 143;
    public static final int d2f = 144;
    public static final int i2b = 145;
    public static final int i2c = 146;
    public static final int i2s = 147;
    public static final int lcmp = 148;
    public static final int fcmpl = 149;
    public static final int fcmpg = 150;
    public static final int dcmpl = 151;
    public static final int dcmpg = 152;
    public static final int ifeq = 153;
    public static final int ifne = 154;
    public static final int iflt = 155;
    public static final int ifge = 156;
    public static final int ifgt = 157;
    public static final int ifle = 158;
    public static final int if_icmpeq = 159;
    public static final int if_icmpne = 160;
    public static final int if_icmplt = 161;
    public static final int if_icmpge = 162;
    public static final int if_icmpgt = 163;
    public static final int if_icmple = 164;
    public static final int if_acmpeq = 165;
    public static final int if_acmpne = 166;
    public static final int goto_ = 167;
    public static final int jsr = 168;
    public static final int ret = 169;
    public static final int tableswitch = 170;
    public static final int lookupswitch = 171;
    public static final int ireturn = 172;
    public static final int lreturn = 173;
    public static final int freturn = 174;
    public static final int dreturn = 175;
    public static final int areturn = 176;
    public static final int return_ = 177;
    public static final int getstatic = 178;
    public static final int putstatic = 179;
    public static final int getfield = 180;
    public static final int putfield = 181;
    public static final int invokevirtual = 182;
    public static final int invokespecial = 183;
    public static final int invokestatic = 184;
    public static final int invokeinterface = 185;
    public static final int invokedynamic = 186;
    public static final int new_ = 187;
    public static final int newarray = 188;
    public static final int anewarray = 189;
    public static final int arraylength = 190;
    public static final int athrow = 191;
    public static final int checkcast = 192;
    public static final int instanceof_ = 193;
    public static final int monitorenter = 194;
    public static final int monitorexit = 195;
    public static final int wide = 196;
    public static final int multianewarray = 197;
    public static final int if_acmp_null = 198;
    public static final int if_acmp_nonnull = 199;
    public static final int goto_w = 200;
    public static final int jsr_w = 201;
    public static final int breakpoint = 202;
    public static final int bytecodecount = 203;
    public static final int string_add = 256;
    public static final int bool_not = 257;
    public static final int bool_and = 258;
    public static final int bool_or = 259;
    public static final int ishll = 270;
    public static final int lshll = 271;
    public static final int ishrl = 272;
    public static final int lshrl = 273;
    public static final int iushrl = 274;
    public static final int lushrl = 275;
    public static final int nullchk = 276;
    public static final int error = 277;
    public static final int dontgoto = 168;
    public static final int preshift = 9;
    public static final int premask = 511;
    public static final int intcode = 0;
    public static final int longcode = 1;
    public static final int floatcode = 2;
    public static final int doublecode = 3;
    public static final int objectcode = 4;
    public static final int bytecode = 5;
    public static final int charcode = 6;
    public static final int shortcode = 7;
    public static final int voidcode = 8;
    public static final int typecodecount = 9;

    public static boolean isLoadInstruction(int byteCode) {
        return false;
    }

    public static int[] integerLoadInstructions() {
        return new int[]{
                iload,
                iload_0,
                iload_1,
                iload_2,
                iload_3
        };
    }

    public static int[] integerStoreInstructions() {
        return new int[]{
                istore,
                istore_0,
                istore_1,
                istore_2,
                istore_3,
        };
    }

    public static int[] longStoreInstructions() {
        return new int[]{
                lstore,
                lstore_0,
                lstore_1,
                lstore_2,
                lstore_3
        };
    }

    public static int[] doubleStoreInstructions() {
        return new int[]{
                dstore,
                dstore_0,
                dstore_1,
                dstore_2,
                dstore_3
        };
    }

    public static int[] floatStoreInstructions() {
        return new int[]{
                fstore,
                fstore_0,
                fstore_1,
                fstore_2,
                fstore_3
        };
    }

    public static int[] primitiveLoadInstructions() {
        return new int[]{
                iload,
                iload_0,
                iload_1,
                iload_1,
                iload_2,
                iload_3,
                lload,
                lload_0,
                lload_1,
                lload_2,
                lload_3,
                fload,
                fload_0,
                fload_1,
                fload_2,
                fload_3,
                dload,
                dload_0,
                dload_1,
                dload_2,
                dload_3
        };
    }

    public static int[] loadInstructions() {
        return new int[]{
                iload,
                iload_0,
                iload_1,
                iload_1,
                iload_2,
                iload_3,
                lload,
                lload_0,
                lload_1,
                lload_2,
                lload_3,
                fload,
                fload_0,
                fload_1,
                fload_2,
                fload_3,
                dload,
                dload_0,
                dload_1,
                dload_2,
                dload_3,
                aload,
                aload_0,
                aload_1,
                aload_2,
                aload_3
        };
    }

    public static String toString(int byteCode) {
        final int targetByteCode = byteCode & 0xFF;

        return Arrays.asList(ByteCode.class.getFields()).stream().filter(f -> {
            try {
                return f.get(null).equals(targetByteCode);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + f.getName());
            }
        }).findFirst().map(java.lang.reflect.Field::getName).orElseThrow(() -> new IllegalArgumentException("Invalid byte code value: " + targetByteCode));
    }

    public static boolean isValid(int byteCode) {
        return byteCode >= 0 && byteCode <= 255;
    }

    /**
     * Returns the width of a byte code, i.e. the number of bytes the byte code requires.
     * This will be greater than 1 bytes that requires an operand such as a constant pool
     * reference.
     *
     * @param byteCode The byte code whose width should be returned.
     * @return The width of the byte code.
     */
    public static int getWidth(int byteCode) {
        switch (byteCode) {
            case aaload:
            case aastore:
            case aconst_null:
            case aload_0:
            case aload_1:
            case aload_2:
            case aload_3:
            case areturn:
            case arraylength:
            case astore_0:
            case astore_1:
            case astore_2:
            case astore_3:
            case athrow:
            case baload:
            case bastore:
            case breakpoint:
            case caload:
            case castore:
            case d2f:
            case d2i:
            case d2l:
            case dadd:
            case daload:
            case dastore:
            case dcmpg:
            case dcmpl:
            case dconst_0:
            case dconst_1:
            case ddiv:
            case dload_0:
            case dload_1:
            case dload_2:
            case dload_3:
            case dmul:
            case dneg:
            case drem:
            case dreturn:
            case dstore_0:
            case dstore_1:
            case dstore_2:
            case dstore_3:
            case dsub:
            case dup:
            case dup_x1:
            case dup_x2:
            case dup2:
            case dup2_x1:
            case dup2_x2:
            case f2d:
            case f2i:
            case f2l:
            case fadd:
            case faload:
            case fastore:
            case fcmpg:
            case fcmpl:
            case fconst_0:
            case fconst_1:
            case fconst_2:
            case fdiv:
            case fmul:
            case fneg:
            case frem:
            case freturn:
            case fstore_0:
            case fstore_1:
            case fstore_2:
            case fstore_3:
            case fsub:
            case i2b:
            case i2c:
            case i2d:
            case i2f:
            case i2s:
            case iadd:
            case iaload:
            case iand:
            case iastore:
            case iconst_m1:
            case iconst_0:
            case iconst_1:
            case iconst_2:
            case iconst_3:
            case iconst_4:
            case iconst_5:
            case idiv:
            case iload_0:
            case iload_1:
            case iload_2:
            case iload_3:
            case imul:
            case ineg:
            case ior:
            case irem:
            case ireturn:
            case ishl:
            case ishr:
            case istore_0:
            case istore_1:
            case istore_2:
            case istore_3:
            case isub:
            case iushr:
            case ixor:
            case l2d:
            case l2f:
            case l2i:
            case ladd:
            case laload:
            case land:
            case lastore:
            case lcmp:
            case lconst_0:
            case lconst_1:
            case ldiv:
            case lload_0:
            case lload_1:
            case lload_2:
            case lload_3:
            case lmul:
            case lneg:
            case lor:
            case lrem:
            case lreturn:
            case lshl:
            case lshr:
            case lstore_0:
            case lstore_1:
            case lstore_2:
            case lstore_3:
            case lsub:
            case lushr:
            case lxor:
            case monitorenter:
            case monitorexit:
            case nop:
            case pop:
            case pop2:
            case return_:
            case saload:
            case sastore:
            case swap:
                return 1;
            case aload:
            case astore:
            case bipush:
            case dload:
            case dstore:
            case fload:
            case fstore:
            case iload:
            case istore:
            case ldc:
            case lload:
            case lstore:
            case newarray:
            case ret:
                return 2;
            case anewarray:
            case checkcast:
            case getfield:
            case getstatic:
            case goto_:
            case if_acmpeq:
            case if_acmpne:
            case if_icmpeq:
            case if_icmpge:
            case if_icmpgt:
            case if_icmple:
            case if_icmplt:
            case if_icmpne:
            case ifeq:
            case ifge:
            case ifgt:
            case ifle:
            case iflt:
            case ifne:
            case if_acmp_nonnull:
            case if_acmp_null:
            case iinc:
            case instanceof_:
            case invokespecial:
            case invokestatic:
            case invokevirtual:
            case jsr:
            case ldcw:
            case ldc2w:
            case new_:
            case putfield:
            case putstatic:
            case sipush:
                return 3;
            case multianewarray:
                return 4;
            case goto_w:
            case invokedynamic:
            case invokeinterface:
            case jsr_w:
                return 5;
            case wide:
                throw new IllegalArgumentException("Instruction 'wide' extends next instruction");
            case tableswitch:
                throw new IllegalArgumentException("Instruction 'tableswitch' is variable in length");
            case lookupswitch:
                throw new IllegalArgumentException("Instruction 'lookupswitch' is variable in length");
            default:
                throw new IllegalArgumentException("Invalid byte code " + byteCode);
        }
    }
}
