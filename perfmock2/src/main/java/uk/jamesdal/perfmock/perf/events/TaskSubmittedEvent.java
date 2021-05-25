package uk.jamesdal.perfmock.perf.events;

import uk.jamesdal.perfmock.perf.SimEvent;
import uk.jamesdal.perfmock.perf.concurrent.PerfFutureTask;

public class TaskSubmittedEvent extends SimEvent {
    private final PerfFutureTask<?> task;

    public TaskSubmittedEvent(PerfFutureTask<?> task, double simTime, double realTime) {
        super(simTime, realTime);
        this.task = task;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.TASK_SUBMITTED;
    }

    public PerfFutureTask<?> getTask() {
        return task;
    }
}
