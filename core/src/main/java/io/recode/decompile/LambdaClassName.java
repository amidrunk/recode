package io.recode.decompile;

public class LambdaClassName {

    private final String declaringClassName;
    private final String type;
    private final int index;
    private final String tag;

    public LambdaClassName(String declaringClassName, String type, int index, String tag) {
        this.declaringClassName = declaringClassName;
        this.type = type;
        this.index = index;
        this.tag = tag;
    }

    public String getDeclaringClassName() {
        return declaringClassName;
    }

    public String getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public String getTag() {
        return tag;
    }

    public static LambdaClassName from(String string) {
        int n = string.indexOf("$$");

        if (n == -1) {
            throw new IllegalArgumentException("Not a lambda class name: " + string);
        }

        int m = string.indexOf('$', n + 2);

        if (m == -1) {
            throw new IllegalArgumentException("Not a lambda class name: " + string + ", expected type/index separation");
        }

        int k = string.indexOf('/', m + 1);

        if (k == -1) {
            throw new IllegalArgumentException("Not a lambda class name: " + string + ", expected id separator");
        }

        return new LambdaClassName(
                string.substring(0, n),
                string.substring(n + 2, m),
                Integer.parseInt(string.substring(m + 1, k)),
                string.substring(k + 1)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LambdaClassName that = (LambdaClassName) o;

        if (index != that.index) return false;
        if (!declaringClassName.equals(that.declaringClassName)) return false;
        if (!type.equals(that.type)) return false;
        return tag.equals(that.tag);

    }

    @Override
    public int hashCode() {
        int result = declaringClassName.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + index;
        result = 31 * result + tag.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LambdaClassName{" +
                "declaringClassName='" + declaringClassName + '\'' +
                ", type='" + type + '\'' +
                ", index=" + index +
                ", tag='" + tag + '\'' +
                '}';
    }
}
