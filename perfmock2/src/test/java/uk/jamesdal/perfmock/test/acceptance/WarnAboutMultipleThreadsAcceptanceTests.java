package uk.jamesdal.perfmock.test.acceptance;

import static org.junit.Assert.assertThat;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.hamcrest.Matchers;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.Mockery;
import uk.jamesdal.perfmock.lib.concurrent.Blitzer;

import junit.framework.TestCase;

@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
public class WarnAboutMultipleThreadsAcceptanceTests extends TestCase {
    BlockingQueue<Throwable> exceptionsOnBackgroundThreads = new LinkedBlockingQueue<Throwable>();

    private ThreadFactory exceptionCapturingThreadFactory = new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    try {
                        exceptionsOnBackgroundThreads.put(e);
                    } catch (InterruptedException e1) {
                        throw new ThreadDeath();
                    }
                }
            });
            return t;
        }
    };

    Blitzer blitzer = new Blitzer(1, Executors.newFixedThreadPool(1, exceptionCapturingThreadFactory));

    public void testKillsThreadsThatTryToCallMockeryThatIsNotThreadSafe() throws InterruptedException {
        Mockery mockery = new Mockery();
        
        final MockedType mock = mockery.mock(MockedType.class, "mock");
        
        mockery.checking(new Expectations() {{
            allowing (mock).doSomething();
        }});
        
        blitzer.blitz(new Runnable() {
            public void run() {
                mock.doSomething();
            }            
        });

        Throwable exception = exceptionsOnBackgroundThreads.take();
        assertThat(exception.getMessage(), Matchers.containsString("the Mockery is not thread-safe"));
    }
    
    @Override
    public void tearDown() {
        blitzer.shutdown();
    }
}
