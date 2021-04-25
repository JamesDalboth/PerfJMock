package uk.jamesdal.perfmock.perf.events;

import uk.jamesdal.perfmock.perf.SimEvent;

public class PauseEvent extends SimEvent {

    private final long runTime;

    public PauseEvent(double simTime, long runTime) {
        super(simTime);
        this.runTime = runTime;
    }

    @Override
    public EventTypes getType() {
        return EventTypes.PAUSE;
    }
}
