package uk.jamesdal.perfmock.FriendServiceThreads;

import java.util.List;

public interface FriendApi {
    List<Integer> getFriends();

    ProfilePic getProfilePic(Integer id);
}
