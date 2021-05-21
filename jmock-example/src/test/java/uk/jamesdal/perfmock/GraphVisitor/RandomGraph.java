package uk.jamesdal.perfmock.GraphVisitor;

import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomGraph {

    public RandomGraph(int verticesNo, PerfMockery ctx) {
        createMocks(verticesNo, ctx);
    }

    private final HashMap<Integer, Node> nodes = new HashMap<>();
    private final Random random = new Random();

    private HashMap<Node, List<Node>> edges = new HashMap<>();

    public void randomVertices(double alpha) {
        for (Node n : edges.keySet()) {
            edges.put(n, new ArrayList<>());
        }

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i+1; j < nodes.size(); j++) {
                if (random.nextDouble() < alpha) {
                    edges.get(nodes.get(i)).add(nodes.get(j));
                    edges.get(nodes.get(j)).add(nodes.get(i));
                }
            }
        }
    }

    public void createMocks(int nodeNo, PerfMockery ctx) {
        for (int i = 0; i < nodeNo; i++) {
            Node node = ctx.mock(Node.class, "random" + i);
            nodes.put(i, node);
            edges.put(node, new ArrayList<>());
        }
    }

    public Expectations createNodeExpectations(NodeProcessor nodeProcessor) {
        return new Expectations() {{
            for (Node node : nodes.values()) {
                allowing(nodeProcessor).process(node); will(returnValue(true)); taking(seconds(0.5));
            }

            for (Node node : edges.keySet()) {
                List<Node> children = edges.get(node);
                allowing(nodeProcessor).getChildren(node); will(returnValue(children.toArray(new Node[0]))); taking(seconds(0.5));
            }
        }};
    }

    public Node first() {
        return nodes.get(0);
    }
}
