package uk.jamesdal.perfmock.perf.events;

import uk.jamesdal.perfmock.perf.SimEvent;
import uk.jamesdal.perfmock.perf.concurrent.PerfCallable;

public class TaskFinishEvent extends SimEvent {

    private final PerfCallable<?> perfCallable;

    public TaskFinishEvent(double simTime, double realTime,
                           PerfCallable<?> perfCallable) {
        super(simTime, realTime);
        this.perfCallable = perfCallable;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.TASK_FINISH;
    }

    public PerfCallable<?> getPerfCallable() {
        return perfCallable;
    }
}
