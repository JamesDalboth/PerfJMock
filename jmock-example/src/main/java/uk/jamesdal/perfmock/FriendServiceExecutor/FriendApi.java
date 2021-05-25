package uk.jamesdal.perfmock.FriendServiceExecutor;

import java.util.List;

public interface FriendApi {
    List<Integer> getFriends();

    Profile getProfilePic(Integer id);
}
