package uk.jamesdal.perfmock.FriendServiceExecutor;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.lib.concurrent.Synchroniser;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.generators.IntegerGenerator;
import uk.jamesdal.perfmock.perf.generators.ListGenerator;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

import java.util.List;

public class FriendServiceTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule) {{
        setThreadingPolicy(new Synchroniser());
    }};

    private final FriendApi api = ctx.mock(FriendApi.class);
    private final ListGenerator<Integer> listGenerator = new ListGenerator<>(
            new IntegerGenerator(new UniformIntegerDistribution(0, 100)),
            new Normal(21.0, 5.0)
    );

    private List<Integer> ids;

    @Test
    @PerfTest(iterations = 2000, warmups = 0)
    public void simpleTest1() {
        FriendService service = new FriendService(
                api, new PerfThreadFactory(perfRule.getSimulation())
        );

        ids = listGenerator.generate();

        ctx.checking(new Expectations() {{
            oneOf(api).getFriends();
                will(returnValue(ids));
                taking(seconds(1));
            allowing(api).getProfilePic(with(any(Integer.class)));
                will(returnValue(new ProfilePic()));
                taking(seconds(new PictureRetrievalModel()));
        }});

        service.getFriendsProfilePictures();
    }
}