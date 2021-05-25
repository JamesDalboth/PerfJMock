package uk.jamesdal.perfmock.perf.concurrent;

import uk.jamesdal.perfmock.perf.Simulation;
import uk.jamesdal.perfmock.perf.events.TaskFinishEvent;

import java.util.concurrent.Callable;

public class PerfCallable<V> implements Callable<V> {
    private final Callable<V> task;
    private final Simulation simulation;

    public PerfCallable(Callable<V> task, Simulation simulation) {
        this.task = task;
        this.simulation = simulation;
    }

    @Override
    public V call() throws Exception {
        simulation.play();
        V res = task.call();
        simulation.pause();
        simulation.addEvent(
                new TaskFinishEvent(
                        simulation.getSimTime(),
                        simulation.getRealTime(),
                        this
                )
        );
        return res;
    }

    public Callable<V> getTask() {
        return task;
    }
}