package uk.jamesdal.perfmock.FriendServiceExecutor;

import uk.jamesdal.perfmock.FriendServiceExecutor.impl.ApiImpl;
import uk.jamesdal.perfmock.perf.concurrent.queues.RoundRobinLinkedBlockingQueue;
import uk.jamesdal.perfmock.production.ProductionTest;

import java.util.concurrent.*;

public class FSRRProduction implements ProductionTest.Runner {
    @Override
    public void run() {
        FriendService friendService = new FriendServiceImpl(
                new ApiImpl(),
                new ThreadPoolExecutor(3, 3,
                        0L, TimeUnit.MILLISECONDS,
                        new RoundRobinLinkedBlockingQueue<>(3),
                        (ThreadFactory) Thread::new)
        );
        friendService.getFriendsProfilePictures();
    }
}
