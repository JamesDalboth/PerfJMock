package uk.jamesdal.perfmock.GraphVisitor;

public class Pair<T, T1> {
    private final T left;
    private final T1 right;

    public Pair(T left, T1 right) {
        this.left = left;
        this.right = right;
    }

    public static Pair<Integer, Integer> create(int i, int i1) {
        return new Pair<>(i, i1);
    }

    public T getLeft() {
        return left;
    }

    public T1 getRight() {
        return right;
    }
}
