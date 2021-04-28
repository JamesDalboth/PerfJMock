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
        simulation.setCallable(this, Thread.currentThread().getId());
    }

    @Override
    public V call() throws Exception {
        simulation.addRunningCallable(this);
        System.out.println(Thread.currentThread() + " Added to running");

        simulation.play();
        V res = task.call();
        simulation.pause();
        simulation.addEvent(new TaskFinishEvent(simulation.getSimTime(), simulation.getRealTime(), this));

        System.out.println(Thread.currentThread() + " finished call");

        Thread currentlyCalculating = simulation.getCurrentlyCalculatingThread();
        if (currentlyCalculating == Thread.currentThread()) {
            simulation.finishedCalculatingThread();
        }

        simulation.finishCallable(this);


        while(true) {
            simulation.waitUntilNoRunningCallables();

            System.out.println(Thread.currentThread() + " no running callables");

            boolean completed = simulation.checkIfCanComplete(this);

            if (completed) {
                System.out.println(Thread.currentThread() + " choosen as earliest thread");
                return res;
            }

            System.out.println(Thread.currentThread() + " not choosen as earliest");

            currentlyCalculating = simulation.getCurrentlyCalculatingThread();
            if (currentlyCalculating != null) {
                simulation.waitUntilCalculatingThreadFinished();
            }
        }
    }
}