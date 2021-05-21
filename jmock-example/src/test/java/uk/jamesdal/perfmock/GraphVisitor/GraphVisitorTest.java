package uk.jamesdal.perfmock.GraphVisitor;

import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.lib.concurrent.Synchroniser;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

import java.util.concurrent.ExecutionException;

public class GraphVisitorTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule) {{
        setThreadingPolicy(new Synchroniser());
    }};

    private final NodeProcessor nodeProcessor = ctx.mock(NodeProcessor.class);

    private final Node N1 = ctx.mock(Node.class, "n1");
    private final Node N2 = ctx.mock(Node.class, "n2");
    private final Node N3 = ctx.mock(Node.class, "n3");
    private final Node N4 = ctx.mock(Node.class, "n4");

    private final ComplexGraph complexGraph = new ComplexGraph(ctx);
    private final RandomGraph randomGraph = new RandomGraph(20, ctx);

    @Test
    @PerfTest(iterations = 2000, warmups = 0)
    public void simpleTest() throws ExecutionException, InterruptedException {
        GraphVisitor graphVisitor = new GraphVisitor(ctx.getThreadFactory(), nodeProcessor);
        ctx.checking(complexGraph.createNodeExpectations(nodeProcessor));

        graphVisitor.visit(complexGraph.first());
    }

    @Test
    @PerfTest(iterations = 2000, warmups = 0)
    public void randomTest() throws ExecutionException, InterruptedException {
        GraphVisitor graphVisitor = new GraphVisitor(ctx.getThreadFactory(), nodeProcessor);

        randomGraph.randomVertices(0.1);

        ctx.checking(randomGraph.createNodeExpectations(nodeProcessor));

        graphVisitor.visit(randomGraph.first());
    }
}