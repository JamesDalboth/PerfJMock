package uk.jamesdal.perfmock.perf;

import uk.jamesdal.perfmock.api.Invocation;

public interface PerfModel {
    double sample();

    void setInvocation(Invocation invocation);
}
