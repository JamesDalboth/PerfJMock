package uk.jamesdal.perfmock.perf.events;

import uk.jamesdal.perfmock.perf.SimEvent;

public class BlockedEvent extends SimEvent {
    public BlockedEvent(double simTime, double realTime) {
        super(simTime, realTime);
    }

    @Override
    public EventTypes getType() {
        return EventTypes.BLOCKED;
    }
}
