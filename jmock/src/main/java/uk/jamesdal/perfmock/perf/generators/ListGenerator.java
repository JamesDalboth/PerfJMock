package uk.jamesdal.perfmock.perf.generators;

import org.apache.commons.math3.distribution.RealDistribution;

import java.util.ArrayList;
import java.util.List;

public class ListGenerator<V> implements Generator<List<V>> {
    private final Generator<V> elemGenerator;
    private final RealDistribution sizeDistr;

    public ListGenerator(Generator<V> elemGenerator, RealDistribution sizeDistr) {
        this.elemGenerator = elemGenerator;
        this.sizeDistr = sizeDistr;
    }

    @Override
    public List<V> generate() {
        int size = new IntegerGenerator(sizeDistr).generate();
        List<V> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(elemGenerator.generate());
        }
        return list;
    }
}
