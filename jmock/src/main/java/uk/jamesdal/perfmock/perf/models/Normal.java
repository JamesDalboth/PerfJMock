package uk.jamesdal.perfmock.perf.models;

import org.apache.commons.math3.distribution.NormalDistribution;
import uk.jamesdal.perfmock.perf.PerfModel;

public class Normal extends NormalDistribution implements PerfModel {
    public Normal(Double mean, Double var) {
        super(mean, Math.sqrt(var));
    }
}
