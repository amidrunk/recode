package io.recode.util;

public final class Strings {

    public static String rightPad(String string, int length, char padCharacter) {
        assert string != null : "String can't be null";
        assert length >= 0 : "Length must be positive";

        if (string.length() >= length) {
            return string;
        }

        final StringBuilder buffer = new StringBuilder(length);

        buffer.append(string);

        while (buffer.length() < length) {
            buffer.append(padCharacter);
        }

        return buffer.toString();
    }

}
