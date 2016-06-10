package io.recode.codegeneration.impl;

import io.recode.codegeneration.CodeStyle;

import java.lang.reflect.Type;

public final class ConfigurableCodeStyle implements CodeStyle {

    private final boolean useSimpleClassNames;

    private final boolean shouldOmitThis;

    private ConfigurableCodeStyle(boolean useSimpleClassNames, boolean shouldOmitThis) {
        this.useSimpleClassNames = useSimpleClassNames;
        this.shouldOmitThis = shouldOmitThis;
    }

    @Override
    public String getTypeName(Type type) {
        assert type != null : "Type can't be null";

        if (useSimpleClassNames && type instanceof Class) {
            return ((Class) type).getSimpleName();
        }

        return type.getTypeName();
    }

    @Override
    public boolean shouldOmitThis() {
        return shouldOmitThis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurableCodeStyle that = (ConfigurableCodeStyle) o;

        if (shouldOmitThis != that.shouldOmitThis) return false;
        if (useSimpleClassNames != that.useSimpleClassNames) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (useSimpleClassNames ? 1 : 0);
        result = 31 * result + (shouldOmitThis ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConfigurableCodeStyle{" +
                "useSimpleClassNames=" + useSimpleClassNames +
                ", shouldOmitThis=" + shouldOmitThis +
                '}';
    }

    public static class Builder {

        private boolean useSimpleClassNames = true;

        private boolean shouldOmitThis = true;

        public Builder setUseSimpleClassNames(boolean useSimpleClassNames) {
            this.useSimpleClassNames = useSimpleClassNames;
            return this;
        }

        public Builder setShouldOmitThis(boolean shouldOmitThis) {
            this.shouldOmitThis = shouldOmitThis;
            return this;
        }

        public CodeStyle build() {
            return new ConfigurableCodeStyle(useSimpleClassNames, shouldOmitThis);
        }
    }

}
