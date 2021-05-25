package uk.jamesdal.perfmock.FriendServiceThreads;

import uk.jamesdal.perfmock.FriendServiceThreads.impl.ApiImpl;
import uk.jamesdal.perfmock.production.ProductionTest;

public class FTProduction implements ProductionTest.Runner {
    @Override
    public void run() {
        FriendService friendService = new FriendService(new ApiImpl(), Thread::new);
        try {
            friendService.getFriendsProfilePictures();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
