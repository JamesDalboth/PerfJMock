package uk.jamesdal.perfmock.FriendServiceThreads.impl;

import uk.jamesdal.perfmock.FriendServiceThreads.FriendApi;
import uk.jamesdal.perfmock.FriendServiceThreads.ProfilePic;
import uk.jamesdal.perfmock.production.SleepFailure;

import java.util.Collections;
import java.util.List;

public class ApiImpl implements FriendApi {

    private final List<Integer> ids = Collections.nCopies(21, 1);

    @Override
    public List<Integer> getFriends() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return ids;
    }

    @Override
    public ProfilePic getProfilePic(Integer id) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new SleepFailure();
        }
        return new ProfilePic();
    }
}
