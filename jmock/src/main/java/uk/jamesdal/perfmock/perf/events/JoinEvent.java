package uk.jamesdal.perfmock.perf.events;

import uk.jamesdal.perfmock.perf.SimEvent;

public class JoinEvent extends SimEvent {
    public JoinEvent(double simTime, double realTime) {
        super(simTime, realTime);
    }

    @Override
    public EventTypes getType() {
        return EventTypes.JOIN;
    }
}
