package uk.jamesdal.perfmock.perf;

import uk.jamesdal.perfmock.perf.events.EventTypes;

public abstract class SimEvent {
    protected final double simTime;
    protected final double realTime;

    protected long threadId;

    protected SimEvent(double simTime, double realTime, long threadId) {
        this.simTime = simTime;
        this.realTime = realTime;
        this.threadId = threadId;
    }

    protected SimEvent(double simTime, double realTime) {
        this.simTime = simTime;
        this.realTime = realTime;
        this.threadId = Thread.currentThread().getId();
    }

    public double getSimTime() {
        return simTime;
    }

    public double getRealTime() {
        return realTime;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public abstract EventTypes getType();
}
