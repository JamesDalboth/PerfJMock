package uk.jamesdal.perfmock.TestingZone;

import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.lib.concurrent.Synchroniser;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

public class RoundRobinIncompleteTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule) {{
        setThreadingPolicy(new Synchroniser());
    }};

    private final MockObject smallTask = ctx.mock(MockObject.class, "small");
    private final MockObject largeTask = ctx.mock(MockObject.class, "large");

    @Test
    @PerfTest(iterations = 100, warmups = 0)
    public void test() {
        RoundRobinIncomplete example = new RoundRobinIncomplete(new PerfThreadFactory(perfRule.getSimulation()), smallTask, largeTask);
        ctx.checking(new Expectations() {{
            allowing(smallTask).run(); taking(seconds(1));
            allowing(largeTask).run(); taking(seconds(10));
        }});

        example.run();
    }

}