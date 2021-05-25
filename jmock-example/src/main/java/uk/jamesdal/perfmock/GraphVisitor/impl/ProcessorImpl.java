package uk.jamesdal.perfmock.GraphVisitor.impl;

import uk.jamesdal.perfmock.GraphVisitor.Node;
import uk.jamesdal.perfmock.GraphVisitor.NodeProcessor;
import uk.jamesdal.perfmock.GraphVisitor.RandomGraph;
import uk.jamesdal.perfmock.production.SleepFailure;

public class ProcessorImpl implements NodeProcessor {

    private final RandomGraph randomGraph;

    public ProcessorImpl(RandomGraph randomGraph) {
        this.randomGraph = randomGraph;
    }

    @Override
    public Node[] getChildren(Node node) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return randomGraph.getChildren(node).toArray(new Node[0]);
    }

    @Override
    public boolean process(Node node) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return true;
    }
}
