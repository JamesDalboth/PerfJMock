package uk.jamesdal.perfmock.GraphVisitor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class GraphVisitor {

    private final ExecutorService executorService;
    private final NodeProcessor nodeProcessor;

    public GraphVisitor(ExecutorService executorService, NodeProcessor nodeProcessor) {
        this.executorService = executorService;
        this.nodeProcessor = nodeProcessor;
    }

    public void visit(Node start) throws ExecutionException, InterruptedException {
        List<Node> visitedNodes = new ArrayList<>();
        Queue<Future<Node[]>> toVisit = new ArrayDeque<>();


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

        executorService.shutdown();
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
