package uk.jamesdal.perfmock.GraphVisitor;

public interface NodeProcessor {
    Node[] getChildren(Node node);

    boolean process(Node node);
}
