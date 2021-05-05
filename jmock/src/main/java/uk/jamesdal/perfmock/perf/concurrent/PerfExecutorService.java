package uk.jamesdal.perfmock.perf.concurrent;

import uk.jamesdal.perfmock.perf.Simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class PerfExecutorService implements ExecutorService {

    public static final int KEEP_ALIVE = 10;
    private final Worker worker;
    private final BlockingQueue<Runnable> workQueue;
    private final int coreSize;
    private final Simulation simulation;

    private boolean shutdown;

    public static PerfExecutorService fixedThreadPool(int coreSize, PerfThreadFactory perfThreadFactory) {
        return new PerfExecutorService(coreSize, new LinkedBlockingQueue<>(), perfThreadFactory.getSimulation());
    }

    public PerfExecutorService(int coreSize, BlockingQueue<Runnable> tasks, Simulation simulation) {
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

        private boolean running;
        private Thread thread;

        private Worker(long[] threadIds) {
            this.threadIds = threadIds;
            this.running = true;
        }

        @Override
        public void run() {
            int tasksCompleted = 0;

            Runnable task;
            while (running) {
                long currentThread = getNextThread(tasksCompleted, threadIds);
                Thread.currentThread().setName(Long.toString(currentThread));
                task = getTask();
                if (task == null) {
                    continue;
                }

                task.run();
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

        private Runnable getTask() {
            try {
                return workQueue.poll(KEEP_ALIVE, TimeUnit.MILLISECONDS);
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
