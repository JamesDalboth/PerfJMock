package uk.jamesdal.perfmock.GoodMessageUploader;

import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.BadMessageUploader.HttpMessageSender;
import uk.jamesdal.perfmock.BadMessageUploader.Message;
import uk.jamesdal.perfmock.BadMessageUploader.MessageResponse;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.lib.concurrent.Synchroniser;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.PerfTest;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

public class GoodMessageUploaderTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule) {{
        setThreadingPolicy(new Synchroniser());
    }};

    private final HttpMessageSender messageSender = ctx.mock(HttpMessageSender.class);
    private final MessageResponse response = ctx.mock(MessageResponse.class);

    private final String message = "msg";
    private final String destination = "dst";


    @Test
    @PerfTest(iterations = 2000, warmups = 0)
    public void uploadPerf() {
        GoodMessageUploader service = new GoodMessageUploader(messageSender, new PerfThreadFactory(perfRule.getSimulation()));

        ctx.checking(new Expectations() {{
            allowing(messageSender).send(new Message(message), destination); will(returnValue(response)); taking(seconds(1));
            allowing(response).isSuccessful(); will(returnValue(true)); taking(milli(100));
        }});

        service.sendMessage(message, destination);
    }

}