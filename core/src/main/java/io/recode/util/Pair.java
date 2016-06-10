package io.recode.util;

public final class Pair<L, R> {

    private final L left;

    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L left() {
        return left;
    }

    public <S> Pair<S, R> left(S left) {
        return new Pair<>(left, right);
    }

    public R right() {
        return right;
    }

    public <S> Pair<L, S> right(S right) {
        return new Pair<>(left, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (left != null ? !left.equals(pair.left) : pair.left != null) return false;
        if (right != null ? !right.equals(pair.right) : pair.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pair(" + left + ", " + right + ")";
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

}
