package uk.jamesdal.perfmock.perf.models;

import uk.jamesdal.perfmock.perf.PerfModel;

public class VoidModel implements PerfModel {
    @Override
    public double sample() {
        return 0;
    }
}
