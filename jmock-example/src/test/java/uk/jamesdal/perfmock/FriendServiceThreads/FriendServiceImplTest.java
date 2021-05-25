package uk.jamesdal.perfmock.FriendServiceThreads;

import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.lib.concurrent.Synchroniser;
import uk.jamesdal.perfmock.perf.annotations.PerfComparator;
import uk.jamesdal.perfmock.perf.annotations.PerfRequirement;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.models.Normal;
import uk.jamesdal.perfmock.perf.annotations.PerfMode;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;

public class FriendServiceImplTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule) {{
        setThreadingPolicy(new Synchroniser());
    }};

    private final FriendApi api = ctx.mock(FriendApi.class);

    private final List<Integer> ids = Collections.nCopies(21, 1);

    @Test
    public void getProfilePictures() {
        FriendService service = new FriendService(api, perfRule.getThreadFactory());

        ctx.repeat(2000, 100, () -> {
            ctx.checking(new Expectations() {{
                oneOf(api).getFriends(); will(returnValue(ids)); taking(seconds(1));
                allowing(api).getProfilePic(with(any(Integer.class))); will(returnValue(new ProfilePic())); taking(seconds(5));
            }});

            List<ProfilePic> res = null;
            try {
                res = service.getFriendsProfilePictures();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(res.size(), ids.size());
        });

        assertThat(ctx.perfResults().meanMeasuredTime() , lessThan(37000.0));
    }

}