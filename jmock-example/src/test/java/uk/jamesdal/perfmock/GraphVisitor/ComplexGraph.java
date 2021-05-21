package uk.jamesdal.perfmock.GraphVisitor;

import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComplexGraph {

    private final List<Pair<Integer, Integer>> vertices = new ArrayList<Pair<Integer, Integer>>() {{
        add(Pair.create(1, 2));
        add(Pair.create(1, 4));
        add(Pair.create(2, 5));
        add(Pair.create(5, 3));
        add(Pair.create(5, 7));
        add(Pair.create(5, 8));
        add(Pair.create(4, 7));
        add(Pair.create(4, 6));
        add(Pair.create(6, 7));
        add(Pair.create(7, 8));
        add(Pair.create(6, 11));
        add(Pair.create(6, 9));
        add(Pair.create(8, 9));
        add(Pair.create(8, 10));
        add(Pair.create(9, 11));
        add(Pair.create(9, 10));
        add(Pair.create(10, 12));
        add(Pair.create(10, 13));
        add(Pair.create(12, 13));
        add(Pair.create(11, 12));
    }};

    public ComplexGraph(PerfMockery ctx) {
        createMocks(ctx);
    }

    private final HashMap<Integer, Node> nodes = new HashMap<>();
    private final HashMap<Node, List<Node>> edges = new HashMap<>();

    public void createMocks(PerfMockery ctx) {
        for (Pair<Integer, Integer> pair : vertices) {
            Integer left = pair.getLeft();
            Integer right = pair.getRight();

            Node leftNode;
            Node rightNode;

            if (!nodes.containsKey(left)) {
                Node node = ctx.mock(Node.class, left.toString());
                nodes.put(left, node);
                edges.put(node, new ArrayList<>());
                leftNode = node;
            } else {
                leftNode = nodes.get(left);
            }

            if (!nodes.containsKey(right)) {
                Node node = ctx.mock(Node.class, right.toString());
                nodes.put(right, node);
                edges.put(node, new ArrayList<>());
                rightNode = node;
            } else {
                rightNode = nodes.get(right);
            }

            edges.get(leftNode).add(rightNode);
            edges.get(rightNode).add(leftNode);
        }
    }

    public Expectations createNodeExpectations(NodeProcessor nodeProcessor) {
        return new Expectations() {{
            for (Node node : nodes.values()) {
                allowing(nodeProcessor).process(node); will(returnValue(true)); taking(seconds(1));
            }

            for (Node node : edges.keySet()) {
                List<Node> children = edges.get(node);
                oneOf(nodeProcessor).getChildren(node); will(returnValue(children.toArray(new Node[0]))); taking(seconds(3));
            }
        }};
    }

    public Node first() {
        return nodes.get(1);
    }
}
