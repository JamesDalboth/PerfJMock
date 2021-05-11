package uk.jamesdal.perfmock.perf.generators;

public class ConstGenerator<V> implements Generator<V> {

    private final V constant;

    public ConstGenerator(V constant) {
        this.constant = constant;
    }

    @Override
    public V generate() {
        return constant;
    }
}
