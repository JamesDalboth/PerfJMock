package uk.jamesdal.perfmock.perf.models;

import org.apache.commons.math3.distribution.ConstantRealDistribution;
import uk.jamesdal.perfmock.api.Invocation;
import uk.jamesdal.perfmock.perf.PerfModel;

public class Constant extends ConstantRealDistribution implements PerfModel {
    public Constant(double value) {
        super(value);
    }

    @Override
    public double sample() {
        return super.sample();
    }

    // Not Used
    @Override
    public void setInvocation(Invocation invocation) {}
}
