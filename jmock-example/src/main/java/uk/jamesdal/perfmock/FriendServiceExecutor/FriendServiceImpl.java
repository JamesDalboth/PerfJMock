package uk.jamesdal.perfmock.FriendServiceExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class FriendServiceImpl implements FriendService {

    private final FriendApi api;
    private final ExecutorService executorService;

    public FriendServiceImpl(FriendApi api, ExecutorService executorService) {
        this.api = api;
        this.executorService = executorService;
    }


    public List<Profile> getFriendsProfilePictures() {
        List<Integer> friendIds = api.getFriends();

        List<Future<Profile>> futureTasks = new ArrayList<>();

        for (Integer id : friendIds) {
            Future<Profile> future = executorService.submit(getProfilePic(id));
            futureTasks.add(future);
        }

        List<Profile> results = new ArrayList<>();
        for (Future<Profile> futureTask : futureTasks) {
            try {
                results.add(futureTask.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        return results;
    }

    private Callable<Profile> getProfilePic(Integer id) {
        return () -> api.getProfilePic(id);
    }
}
