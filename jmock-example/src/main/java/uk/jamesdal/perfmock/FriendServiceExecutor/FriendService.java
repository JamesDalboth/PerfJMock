package uk.jamesdal.perfmock.FriendServiceExecutor;

import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.concurrent.executors.PerfSimTimeExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class FriendService {

    private final FriendApi api;
    private final PerfThreadFactory threadFactory;

    public FriendService(FriendApi api, PerfThreadFactory threadFactory) {
        this.api = api;
        this.threadFactory = threadFactory;
    }


    public List<ProfilePic> getFriendsProfilePictures() {
        List<Integer> friendIds = api.getFriends();

        List<Future<ProfilePic>> futureTasks = new ArrayList<>();

        PerfSimTimeExecutorService executorService =
                PerfSimTimeExecutorService.fixedThreadPool(3, threadFactory);
        for (Integer id : friendIds) {
            int priority = id < 50 ? 2 : 1;
            Future<ProfilePic> future = executorService.submit(getProfilePic(id), priority);
            futureTasks.add(future);
        }

        List<ProfilePic> results = new ArrayList<>();
        for (Future<ProfilePic> futureTask : futureTasks) {
            try {
                results.add(futureTask.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return results;
    }

    private Callable<ProfilePic> getProfilePic(Integer id) {
        return () -> api.getProfilePic(id);
    }
}
