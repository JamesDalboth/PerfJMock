package uk.jamesdal.perfmock.GraphVisitor;

import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.concurrent.executors.PerfSimTimeExecutorService;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class GraphVisitor {

    private final PerfThreadFactory threadFactory;
    private final NodeProcessor nodeProcessor;

    public GraphVisitor(PerfThreadFactory threadFactory, NodeProcessor nodeProcessor) {
        this.threadFactory = threadFactory;
        this.nodeProcessor = nodeProcessor;
    }

    public void visit(Node start) throws ExecutionException, InterruptedException {
        List<Node> visitedNodes = new ArrayList<>();
        Queue<Future<Node[]>> toVisit = new ArrayDeque<>();

        ExecutorService executorService = PerfSimTimeExecutorService.fixedThreadPool(4, threadFactory);

        Future<Node[]> future = executorService.submit(visitNodes(start));
        toVisit.add(future);
        visitedNodes.add(start);

        while (!toVisit.isEmpty()) {
            future = toVisit.poll();
            Node[] children = future.get();
            for (Node child : children) {
                if (!visitedNodes.contains(child)) {
                    toVisit.add(executorService.submit(visitNodes(child)));
                    visitedNodes.add(child);
                }
            }
        }
    }

    public Callable<Node[]> visitNodes(Node node) {
        return () -> {
            boolean success = nodeProcessor.process(node);
            if (!success) {
                return new Node[0];
            }

            return nodeProcessor.getChildren(node);
        };
    }
}
