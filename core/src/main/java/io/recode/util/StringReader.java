package io.recode.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringReader {

    private final String string;

    private int offset = 0;

    public StringReader(String string) {
        assert string != null : "String can't be null";

        this.string = string;
    }

    public String remainder() {
        return string.substring(offset);
    }

    public boolean skip(int count) {
        assert count > 0 : "Count must be > 0";

        if (offset + count > string.length()) {
            return false;
        }

        offset += count;

        return true;
    }

    public boolean read(String exactString) {
        assert exactString != null : "String can't be null";

        if (!remainder().startsWith(exactString)) {
            return false;
        }

        offset += exactString.length();

        return true;
    }

    public int read() {
        if (offset >= string.length()) {
            return -1;
        }

        return string.charAt(offset++);
    }

    public int peek() {
        if (offset >= string.length()) {
            return -1;
        }

        return string.charAt(offset);
    }

    public Optional<String> readUntil(Pattern pattern) {
        assert pattern != null : "Pattern can't be null";

        final String remainder = remainder();
        final Matcher matcher = pattern.matcher(remainder);

        if (!matcher.find()) {
            return Optional.empty();
        }

        final String matchedString = remainder.substring(0, matcher.start());

        offset += matchedString.length();

        return Optional.of(matchedString);
    }

}
