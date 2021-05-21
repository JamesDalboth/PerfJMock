package uk.jamesdal.perfmock.FriendServiceExecutor;

import uk.jamesdal.perfmock.api.Invocation;
import uk.jamesdal.perfmock.perf.PerfModel;
import uk.jamesdal.perfmock.perf.models.Normal;

public class PictureRetrievalModel implements PerfModel {
    private Invocation invocation;

    @Override
    public double sample() {
        Integer id = (Integer) invocation.getParameter(0);
        double mean = id < 50 ? 1 : 9;
        return new Normal(mean, 0.5).sample();
    }

    @Override
    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }
}
