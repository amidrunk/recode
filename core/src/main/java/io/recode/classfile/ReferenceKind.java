package io.recode.classfile;

public enum ReferenceKind {

    GET_FIELD(1),
    GET_STATIC(2),
    PUT_FIELD(3),
    PUT_STATIC(4),
    INVOKE_VIRTUAL(5),
    INVOKE_STATIC(6),
    INVOKE_SPECIAL(7),
    NEW_INVOKE_SPECIAL(8),
    INVOKE_INTERFACE(9);

    private final int value;

    ReferenceKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ReferenceKind valueOf(int value) {
        for (ReferenceKind kind : values()) {
            if (kind.getValue() == value) {
                return kind;
            }
        }

        throw new IllegalArgumentException("Invalid method handle kind: " + value);
    }
}
