package uk.jamesdal.perfmock.perf.generators;

import org.apache.commons.math3.distribution.RealDistribution;

public class IntegerGenerator implements Generator<Integer> {

    private RealDistribution distribution;

    public IntegerGenerator(RealDistribution distribution) {
        this.distribution = distribution;
    }

    @Override
    public Integer generate() {
        return Math.toIntExact(Math.round(distribution.sample()));
    }
}
