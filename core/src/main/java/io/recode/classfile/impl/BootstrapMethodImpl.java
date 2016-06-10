package io.recode.classfile.impl;

import io.recode.classfile.BootstrapMethod;

import java.util.Arrays;

public class BootstrapMethodImpl implements BootstrapMethod {

    private final int bootstrapMethodRef;

    private final int[] bootstrapArguments;

    public BootstrapMethodImpl(int bootstrapMethodRef, int[] bootstrapArguments) {
        assert bootstrapMethodRef >= 0 : "Bootstrap method ref must be positive";
        assert bootstrapArguments != null : "Bootstrap arguments can't be null";

        this.bootstrapMethodRef = bootstrapMethodRef;
        this.bootstrapArguments = bootstrapArguments;
    }

    @Override
    public int getBootstrapMethodRef() {
        return bootstrapMethodRef;
    }

    @Override
    public int[] getBootstrapArguments() {
        return bootstrapArguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BootstrapMethodImpl that = (BootstrapMethodImpl) o;

        if (bootstrapMethodRef != that.bootstrapMethodRef) return false;
        if (!Arrays.equals(bootstrapArguments, that.bootstrapArguments)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bootstrapMethodRef;
        result = 31 * result + Arrays.hashCode(bootstrapArguments);
        return result;
    }

    @Override
    public String toString() {
        return "BootstrapMethodImpl{" +
                "bootstrapMethodRef=" + bootstrapMethodRef +
                ", bootstrapArguments=" + Arrays.toString(bootstrapArguments) +
                '}';
    }
}
