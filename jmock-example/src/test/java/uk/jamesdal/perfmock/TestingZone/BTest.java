package uk.jamesdal.perfmock.TestingZone;

import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.FriendServiceExecutor.ConsoleHtmlReporter;
import uk.jamesdal.perfmock.TestingZone.impl.BImpl;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.CSVGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.jamesdal.perfmock.perf.postproc.PerfStatistics.matchesDistr;

public class BTest {
    @Rule
    public PerfRule perfRule = new PerfRule(new CSVGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule);

    //public static final Normal B_DISTR  = new Normal(100.0, 5.0);

    private final MockObject object = ctx.mock(MockObject.class);
    private final B b = new BImpl(object);

    @Test
    public void bTest() {
        ctx.repeat("bTest", 2000, 100, () -> {
            ctx.checking( new Expectations() {{
                allowing(object).run(); taking(new Normal(100.0, 5.0));
            }});
            b.run();
        });
    }
}
