package uk.jamesdal.perfmock.perf.events;

import uk.jamesdal.perfmock.perf.SimEvent;

public class TaskJoinEvent extends SimEvent {
    public TaskJoinEvent(double simTime, double realTime) {
        super(simTime, realTime);
    }

    @Override
    public EventTypes getType() {
        return EventTypes.TASK_JOIN;
    }
}
