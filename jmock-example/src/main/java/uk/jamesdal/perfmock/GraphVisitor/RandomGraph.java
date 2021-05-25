package uk.jamesdal.perfmock.GraphVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomGraph {

    public RandomGraph(int verticesNo) {
        for (int i = 0; i < verticesNo; i++) {
            Node node = new Node() {};
            nodes.put(i, node);
            edges.put(node, new ArrayList<>());
        }
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

    public Node first() {
        return nodes.get(0);
    }

    public List<Node> getChildren(Node node) {
        return edges.get(node);
    }
}
