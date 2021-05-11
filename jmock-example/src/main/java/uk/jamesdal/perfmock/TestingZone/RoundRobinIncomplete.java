package uk.jamesdal.perfmock.TestingZone;

import uk.jamesdal.perfmock.perf.concurrent.executors.PerfSimTimeExecutorService;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RoundRobinIncomplete {
    private final PerfThreadFactory threadFactory;
    private final MockObject smallTask;
    private final MockObject largeTask;

    public RoundRobinIncomplete(PerfThreadFactory threadFactory, MockObject smallTask, MockObject largeTask) {
        this.threadFactory = threadFactory;
        this.smallTask = smallTask;
        this.largeTask = largeTask;
    }

    public void run() {
//        ExecutorService executorService = PerfThreadPoolExecutor.newFixedThreadPoolRoundRobin(
//                2, threadFactory
//        );

        ExecutorService executorService = new PerfSimTimeExecutorService(
                2,
                new LinkedBlockingQueue<>(),
                threadFactory.getSimulation()
        );

        List<Future<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            futures.add(executorService.submit(createTask(smallTask)));
            futures.add(executorService.submit(createTask(largeTask)));
        }

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Callable<Void> createTask(MockObject obj) {
        return () -> {
            obj.run();
            return null;
        };
    }

    private Callable<Void> emptyTask() {
        return () -> null;
    }
}
