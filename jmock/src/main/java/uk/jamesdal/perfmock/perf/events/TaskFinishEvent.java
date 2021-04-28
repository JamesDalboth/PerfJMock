package uk.jamesdal.perfmock.perf.events;

import uk.jamesdal.perfmock.perf.SimEvent;
import uk.jamesdal.perfmock.perf.concurrent.PerfCallable;

public class TaskFinishEvent extends SimEvent {

    private final PerfCallable<?> task;

    public TaskFinishEvent(double simTime, double realTime, PerfCallable<?> task) {
        super(simTime, realTime);
        this.task = task;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.TASK_FINISH;
    }

    public PerfCallable<?> getTask() {
        return task;
    }
}
