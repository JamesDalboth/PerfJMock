package uk.jamesdal.perfmock.perf.concurrent;

import uk.jamesdal.perfmock.perf.Simulation;
import uk.jamesdal.perfmock.perf.events.TaskSubmittedEvent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PerfFutureTask<T> extends FutureTask<T> implements Comparable<PerfFutureTask<T>> {
    private final Simulation simulation;
    private final Callable<T> task;
    private final int priority;

    public PerfFutureTask(Callable<T> callable, Simulation simulation) {
        super(new PerfCallable<>(callable, simulation));
        this.simulation = simulation;
        this.task = callable;
        simulation.addEvent(new TaskSubmittedEvent(
                this,
                simulation.getSimTime(),
                simulation.getRealTime()
        ));
        this.priority = -1;
    }

    public PerfFutureTask(Callable<T> callable, Simulation simulation,
                          int priority) {
        super(new PerfCallable<>(callable, simulation));
        this.simulation = simulation;
        this.task = callable;
        simulation.addEvent(new TaskSubmittedEvent(
                this,
                simulation.getSimTime(),
                simulation.getRealTime()
        ));
        this.priority = priority;
    }

    @Override
    public T get() throws ExecutionException, InterruptedException {
        simulation.pause();
        T res = super.get();
        simulation.createTaskJoinEvent(this.task);
        simulation.play();
        return res;
    }

    @Override
    public int compareTo(PerfFutureTask<T> o) {
        return -Integer.compare(priority, o.priority);
    }
}
