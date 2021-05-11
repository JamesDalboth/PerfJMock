package uk.jamesdal.perfmock.perf.generators;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public class IntegerGenerator implements Generator<Integer> {

    private RealDistribution realDistribution;
    private IntegerDistribution integerDistribution;

    public IntegerGenerator(RealDistribution distribution) {
        this.realDistribution = distribution;
    }

    public IntegerGenerator(IntegerDistribution distribution) {
        this.integerDistribution = distribution;
    }

    @Override
    public Integer generate() {
        if (integerDistribution == null) {
            return Math.toIntExact(Math.round(realDistribution.sample()));
        }
        return integerDistribution.sample();
    }
}
