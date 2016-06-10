package io.recode;

import java.util.Optional;

public interface CodeLocation {

    String getClassName();

    String getMethodName();

    String getFileName();

    int getLineNumber();

    Optional<CodeLocation> getCaller();
}
