package uk.jamesdal.perfmock.TestingZone;

import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.CSVGenerator;

public class ATest {
    @Rule
    public PerfRule perfRule = new PerfRule(new CSVGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule);

    private final B b = ctx.mock(B.class);
    private final C c = ctx.mock(C.class);

    @Test
    @PerfTest(iterations = 2000, warmups = 100)
    public void aTest() {
        A a = new A();

        ctx.checking(new Expectations() {{
            oneOf(b).doSomething(); taking(milli(100));
            oneOf(c).condition(); will(returnValue(false)); taking(milli(50));
        }});

        a.run(b, c);
    }
}