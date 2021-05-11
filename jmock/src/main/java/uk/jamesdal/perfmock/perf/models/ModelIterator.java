package uk.jamesdal.perfmock.perf.models;

import uk.jamesdal.perfmock.api.Invocation;
import uk.jamesdal.perfmock.perf.PerfModel;

import java.util.Arrays;
import java.util.Collection;

public class ModelIterator implements PerfModel {

    public final Collection<PerfModel> modelList;

    public int index;

    public ModelIterator(PerfModel... models) {
        this.modelList = Arrays.asList(models);
        index = 0;
    }

    @Override
    public double sample() {
        return modelList.iterator().next().sample();
    }

    // Not Used
    @Override
    public void setInvocation(Invocation invocation) {}
}
