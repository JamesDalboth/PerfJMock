package uk.jamesdal.perfmock.perf.concurrent.executors;

import uk.jamesdal.perfmock.perf.Simulation;
import uk.jamesdal.perfmock.perf.concurrent.PerfFutureTask;
import uk.jamesdal.perfmock.perf.concurrent.queues.PerfLinkedBlockingQueue;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;

import java.util.concurrent.*;

public class PerfThreadPoolExecutor extends ThreadPoolExecutor {

    private final Simulation simulation;

    public static ExecutorService newFixedThreadPool(int nThreads, PerfThreadFactory threadFactory) {
        return new PerfThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>(), threadFactory);
    }

    public static ExecutorService newFixedThreadPoolRoundRobin(int nThreads, PerfThreadFactory threadFactory) {
        ExecutorService es = new PerfThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new PerfLinkedBlockingQueue<>(nThreads), threadFactory);

        // Initilise threads
        for (int i = 0; i < nThreads; i++) {
            es.submit(() -> null);
        }

        return es;
    }

    public PerfThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, PerfThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.simulation = threadFactory.getSimulation();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(task);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new PerfFutureTask<T>(callable, simulation);
    }
}
