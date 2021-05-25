package uk.jamesdal.perfmock.GraphVisitor;

import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.lib.concurrent.Synchroniser;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.concurrent.executors.PerfSimTimeExecutorService;
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

    private final ComplexGraph complexGraph = new ComplexGraph(ctx);
    private final RandomGraphGen randomGraphGen = new RandomGraphGen(20, ctx);

    @Test
    @PerfTest(iterations = 2000, warmups = 0)
    public void simpleTest() throws ExecutionException, InterruptedException {
        GraphVisitor graphVisitor = new GraphVisitor(
                PerfSimTimeExecutorService.fixedThreadPool(3, ctx.getThreadFactory()),
                nodeProcessor
        );
        ctx.checking(complexGraph.createNodeExpectations(nodeProcessor));

        graphVisitor.visit(complexGraph.first());
    }

    @Test
    @PerfTest(iterations = 2000, warmups = 100)
    public void randomTest() throws ExecutionException, InterruptedException {
        GraphVisitor graphVisitor = new GraphVisitor(
                PerfSimTimeExecutorService.fixedThreadPool(3, ctx.getThreadFactory()),
                nodeProcessor
        );

        ctx.ignore(() -> randomGraphGen.randomVertices(0.1));

        ctx.checking(randomGraphGen.createNodeExpectations(nodeProcessor));

        graphVisitor.visit(randomGraphGen.first());
    }
}