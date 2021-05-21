package uk.jamesdal.perfmock.FriendServiceExecutor;

import java.util.List;

interface FriendApi {
    List<Integer> getFriends();

    Profile getProfilePic(Integer id);
}
