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
import uk.jamesdal.perfmock.perf.concurrent.executors.PerfSimTimeExecutorService;
import uk.jamesdal.perfmock.perf.generators.IntegerGenerator;
import uk.jamesdal.perfmock.perf.generators.ListGenerator;
import uk.jamesdal.perfmock.perf.models.Constant;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.CSVGenerator;

import java.util.List;

public class FriendServiceImplTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new CSVGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule) {{
        setThreadingPolicy(new Synchroniser());
    }};

    public static final Normal FIXED_THREAD_POOL_3_DIST = new Normal(37800.0, 17407564.0);

    private final PerfThreadFactory perfThreadFactory = new PerfThreadFactory(perfRule.getSimulation());

    private final FriendApi api = ctx.mock(FriendApi.class);
    private final ListGenerator<Integer> listGenerator = new ListGenerator<>(
            new IntegerGenerator(new UniformIntegerDistribution(0, 100)),
            //new Normal(21.0, 5.0)
            new Constant(21.0)
    );

    private final Profile profile = ctx.mock(Profile.class);

    private List<Integer> ids = listGenerator.generate();

    @Test
    @PerfTest(iterations = 2000, warmups = 0)
    public void fixedPool1() {
        FriendServiceImpl service = new FriendServiceImpl(
                api, PerfSimTimeExecutorService.fixedThreadPool(3, perfThreadFactory)
        );

        ctx.ignore(() -> ids = listGenerator.generate());

        ctx.checking(new Expectations() {{
            oneOf(api).getFriends();
                will(returnValue(ids));
                taking(seconds(1));
            allowing(api).getProfilePic(with(any(Integer.class)));
                will(returnValue(profile));
                taking(seconds(new PictureRetrievalModel()));
        }});

        service.getFriendsProfilePictures();
    }

    @Test
    @PerfTest(iterations = 20000, warmups = 0)
    public void fixedPool2() {
        FriendServiceImpl service = new FriendServiceImpl(
                api, PerfSimTimeExecutorService.fixedThreadPool(2, perfThreadFactory)
        );

        ctx.ignore(() -> ids = listGenerator.generate());

        ctx.checking(new Expectations() {{
            oneOf(api).getFriends();
                will(returnValue(ids));
                taking(seconds(1));
            allowing(api).getProfilePic(with(any(Integer.class)));
                will(returnValue(profile));
                taking(seconds(new PictureRetrievalModel()));
        }});

        service.getFriendsProfilePictures();
    }

    @Test
    public void fixedPool3() {
        ctx.repeat("fixedPool3", 20000, 100, () -> {
            FriendServiceImpl service = new FriendServiceImpl(
                    api, PerfSimTimeExecutorService.fixedThreadPool(3, perfThreadFactory)
            );

            ctx.ignore(() -> ids = listGenerator.generate());

            ctx.checking(new Expectations() {{
                oneOf(api).getFriends();
                    will(returnValue(ids));
                    taking(seconds(1));
                allowing(api).getProfilePic(with(any(Integer.class)));
                    will(returnValue(profile));
                    taking(seconds(new Normal(5.0, 1.0)));
            }});

            service.getFriendsProfilePictures();
        });

        //assertTrue(ctx.perfResults().matchesDistribution(FIXED_THREAD_POOL_3_DIST, 0.01));
    }
}