package uk.jamesdal.perfmock.WeatherMan;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import uk.jamesdal.perfmock.api.Invocation;
import uk.jamesdal.perfmock.perf.PerfModel;

public class Uniform implements PerfModel {
    private final UniformRealDistribution uniform;

    public Uniform(Double lower, Double upper) {
        this.uniform = new UniformRealDistribution(lower, upper);
    }

    @Override
    public double sample() {
        return uniform.sample();
    }

    // Not Used
    @Override
    public void setInvocation(Invocation invocation) {}
}
