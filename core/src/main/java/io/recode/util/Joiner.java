package io.recode.util;

import java.util.Collection;
import java.util.Iterator;

public final class Joiner {

    public static JoinContinuation join(Collection<?> elements) {
        assert elements != null : "Elements can't be null";

        return separator -> {
            assert separator != null : "Separator can't be null";

            final StringBuilder buffer = new StringBuilder();

            for (Iterator<?> i = elements.iterator(); i.hasNext(); ) {
                buffer.append(i.next());

                if (i.hasNext()) {
                    buffer.append(separator);
                }
            }

            return buffer.toString();
        };
    }

    @FunctionalInterface
    public interface JoinContinuation {

        String on(String separator);

    }

}
