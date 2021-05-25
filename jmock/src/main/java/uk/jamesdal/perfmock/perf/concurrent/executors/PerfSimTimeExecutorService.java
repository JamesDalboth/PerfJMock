package uk.jamesdal.perfmock.perf.concurrent.executors;

import uk.jamesdal.perfmock.perf.Simulation;
import uk.jamesdal.perfmock.perf.concurrent.PerfFutureTask;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.events.BlockedEvent;
import uk.jamesdal.perfmock.perf.exceptions.ShutdownException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PerfSimTimeExecutorService implements ExecutorService {

    public static final int KEEP_ALIVE = 5;
    private final Worker worker;
    private final BlockingQueue<Runnable> workQueue;
    private final int coreSize;
    private final Simulation simulation;

    private boolean shutdown;

    public static PerfSimTimeExecutorService fixedThreadPool(int coreSize, PerfThreadFactory perfThreadFactory) {
        return new PerfSimTimeExecutorService(coreSize, new LinkedBlockingQueue<>(), perfThreadFactory.getSimulation());
    }


    public static PerfSimTimeExecutorService fixedThreadPoolPriority(int coreSize, PerfThreadFactory perfThreadFactory) {
        return new PerfSimTimeExecutorService(coreSize, new PriorityBlockingQueue<>(), perfThreadFactory.getSimulation());
    }

    public PerfSimTimeExecutorService(int coreSize, BlockingQueue<Runnable> tasks, Simulation simulation) {
        this.workQueue = tasks;
        this.coreSize = coreSize;
        this.shutdown = false;
        this.simulation = simulation;

        this.worker = initWorker();
    }

    @Override
    public void execute(Runnable command) {
        workQueue.offer(command);
    }

    @Override
    public void shutdown() {
        interuptWorker();
        shutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        interuptWorker();
        shutdown = true;
        return drainQueue();
    }

    private void interuptWorker() {
        worker.stopTasks();
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    // Not implemented
    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long millis = unit.toMillis(timeout);
        worker.getThread().join(millis);
        return !worker.getThread().isAlive();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (isShutdown()) {
            throw new ShutdownException();
        }
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Callable<T> task, int priority) {
        if (isShutdown()) {
            throw new ShutdownException();
        }
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task, priority);
        execute(ftask);
        return ftask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (isShutdown()) {
            throw new ShutdownException();
        }
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);
        return ftask;
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (isShutdown()) {
            throw new ShutdownException();
        }
        if (task == null) throw new NullPointerException();
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }

    // Not Implemented
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return null;
    }

    // Not Implemented
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    // Not Implemented
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return null;
    }

    // Not Implemented
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    private <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }

    private <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new PerfFutureTask<>(callable, simulation);
    }

    private <T> RunnableFuture<T> newTaskFor(Callable<T> callable, int priority) {
        return new PerfFutureTask<>(callable, simulation, priority);
    }

    private Worker initWorker() {
        long[] threadIds = new long[coreSize];
        for (int i = 0; i < coreSize; i++) {
            long threadId = simulation.setUpFakeThread();
            threadIds[i] = threadId;
        }

        Worker worker = new Worker(threadIds);
        Thread thread = new Thread(worker);
        thread.start();

        worker.setThread(thread);

        return worker;
    }

    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r))
                    taskList.add(r);
            }
        }
        return taskList;
    }

    private class Worker implements Runnable {
        private final long[] threadIds;

        private volatile boolean running;
        private Thread thread;

        private Worker(long[] threadIds) {
            this.threadIds = threadIds;
            this.running = true;
        }

        @Override
        public void run() {
            int tasksCompleted = 0;

            PerfFutureTask<?> task;
            while (running) {
                task = getTask();
                if (task == null) {
                    continue;
                }

                //System.out.println("GTask " + System.currentTimeMillis());

                double submittedTime = simulation.getTaskSubmittedTime(task);
                long currentThread = getNextThread(tasksCompleted, threadIds);
                double threadSimTime = simulation.getSimTime(currentThread);
                double waitTime = submittedTime - threadSimTime;

                //System.out.println("GThread " + System.currentTimeMillis());

                simulation.usingFakeThread(currentThread);
                if (waitTime > 0) {
                    simulation.addEvent(new BlockedEvent(
                            submittedTime, simulation.getRealTime()
                    ));
                }

                //System.out.println("BTask " + System.currentTimeMillis());
                task.run();
                //System.out.println("ATask " + System.currentTimeMillis());
                tasksCompleted++;
            }
        }

        public void stopTasks() {
            running = false;
        }

        private long getNextThread(int tasksCompleted, long[] threadIds) {
            if (tasksCompleted < coreSize) {
                return threadIds[tasksCompleted];
            }

            return simulation.getThreadWithSmallestSimTime(threadIds);
        }

        private PerfFutureTask<?> getTask() {
            try {
                return (PerfFutureTask<?>) workQueue.poll(
                        KEEP_ALIVE, TimeUnit.MILLISECONDS
                );
            } catch (InterruptedException retry) {
                return null;
            }
        }

        public void setThread(Thread thread) {
            this.thread = thread;
        }

        public Thread getThread() {
            return thread;
        }
    }
}
