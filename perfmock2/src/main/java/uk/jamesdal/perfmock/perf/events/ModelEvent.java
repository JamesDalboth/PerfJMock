package uk.jamesdal.perfmock.perf.events;

import uk.jamesdal.perfmock.perf.SimEvent;

public class ModelEvent extends SimEvent {

    private final double sampledModelTime;

    public ModelEvent(double simTime, double realTime, double sampledModelTime) {
        super(simTime, realTime);
        this.sampledModelTime = sampledModelTime;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.MODEL;
    }
}
