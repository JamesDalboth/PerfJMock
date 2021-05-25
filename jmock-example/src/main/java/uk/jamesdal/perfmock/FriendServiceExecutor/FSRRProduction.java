package uk.jamesdal.perfmock.FriendServiceExecutor;

import uk.jamesdal.perfmock.FriendServiceExecutor.impl.ApiImpl;
import uk.jamesdal.perfmock.production.ProductionTest;

import java.util.concurrent.Executors;

public class FSNativeProduction implements ProductionTest.Runner {
    @Override
    public void run() {
        FriendService friendService = new FriendServiceImpl(new ApiImpl(), Executors.newFixedThreadPool(3));
        friendService.getFriendsProfilePictures();
    }
}
