package uk.jamesdal.perfmock.GraphVisitor;

import uk.jamesdal.perfmock.GraphVisitor.impl.ProcessorImpl;
import uk.jamesdal.perfmock.production.ProductionTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class GraphProduction implements ProductionTest.Runner {

    private final RandomGraph randomGraph = new RandomGraph(20);

    @Override
    public void run() {
        randomGraph.randomVertices(0.1);
        GraphVisitor graphVisitor = new GraphVisitor(Executors.newFixedThreadPool(3), new ProcessorImpl(randomGraph));
        try {
            graphVisitor.visit(randomGraph.first());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
