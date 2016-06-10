package io.recode;

import java.lang.reflect.Type;

public final class UnresolvedType implements Type {

    private final String typeName;

    public UnresolvedType(String typeName) {
        assert typeName != null && !typeName.isEmpty() : "Type name can't be null or empty";
        this.typeName = typeName;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnresolvedType that = (UnresolvedType) o;

        if (!typeName.equals(that.typeName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

    @Override
    public String toString() {
        return "UnresolvedType{" +
                "typeName='" + typeName + '\'' +
                '}';
    }
}
