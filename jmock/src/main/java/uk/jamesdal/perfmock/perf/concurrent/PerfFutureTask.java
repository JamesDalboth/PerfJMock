package uk.jamesdal.perfmock.perf.concurrent;

import uk.jamesdal.perfmock.perf.Simulation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PerfFutureTask<T> extends FutureTask<T> {
    private final Simulation simulation;
    private final Callable<T> task;

    public PerfFutureTask(Callable<T> callable, Simulation simulation) {
        super(new PerfCallable<>(callable, simulation));
        this.simulation = simulation;
        this.task = callable;
    }

    @Override
    public T get() throws ExecutionException, InterruptedException {
        simulation.pause();
        T res = super.get();
        simulation.createTaskJoinEvent(this.task);
        simulation.play();
        return res;
    }
}
