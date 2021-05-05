package uk.jamesdal.perfmock.perf;

import uk.jamesdal.perfmock.perf.events.EventTypes;
import uk.jamesdal.perfmock.perf.events.ForkEvent;
import uk.jamesdal.perfmock.perf.events.JoinEvent;
import uk.jamesdal.perfmock.perf.events.ModelEvent;
import uk.jamesdal.perfmock.perf.events.PauseEvent;
import uk.jamesdal.perfmock.perf.events.PlayEvent;
import uk.jamesdal.perfmock.perf.events.TaskFinishEvent;
import uk.jamesdal.perfmock.perf.events.TaskJoinEvent;
import uk.jamesdal.perfmock.perf.postproc.IterResult;
import uk.jamesdal.perfmock.perf.postproc.PerfStatistics;
import uk.jamesdal.perfmock.perf.postproc.ReportGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

public class Simulation {
    private final List<IterResult> results;
    private final AtomicLong fakeThreadId;
    private final ReportGenerator reportGenerator;

    private List<Long> threadIds;
    private List<SimEvent> history;

    public Simulation(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
        this.results = new ArrayList<>();
        this.fakeThreadId = new AtomicLong();

        this.threadIds = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    // Reset timeline to beginning
    public void reset() {
        history = new ArrayList<>();
        threadIds = new ArrayList<>();
    }

    // Save Simulation results
    public void save() {
        results.add(new IterResult(getRealTime(), getSimTime()));
    }

    public void genReport() {
        reportGenerator.setStats(getStats());
        reportGenerator.generateReport();
    }

    public PerfStatistics getStats() {
        return new PerfStatistics(results);
    }

    public double getSimTime() {
        return getSimTime(getThreadId());
    }

    public double getSimTime(long id) {
        if (history.size() == 0) {
            return 0.0;
        }

        SimEvent lastEvent = backwardsSearch(history, id);
        if (lastEvent == null) {
            throw new EventMissingException();
        }

        return lastEvent.getSimTime();
    }

    public double getRealTime() {
        return getRealTime(getThreadId());
    }

    public double getRealTime(long id) {
        if (history.size() == 0) {
            return 0.0;
        }

        SimEvent lastEvent = backwardsSearch(history, id);
        if (lastEvent == null) {
            throw new EventMissingException();
        }

        return lastEvent.getRealTime();
    }

    public Long getThreadWithSmallestSimTime(long[] threadIds) {
        double smallestSimTime = 0.0;
        Long quickestThread = null;
        for (Long threadId : threadIds) {
            double simTime = getSimTime(threadId);
            if (quickestThread == null || smallestSimTime > simTime) {
                quickestThread = threadId;
                smallestSimTime = simTime;
            }
        }

        return quickestThread;
    }

    // Event Methods
    /////////////////////////////////

    // Add play event for current thread
    public void play() {
        // Check last play/pause event is either pause or null
        long id = getThreadId();
        SimEvent last = getLastPlayPauseEvent(
                history, id, history.size() - 1
        );

        if (Objects.isNull(last) || last.getType() == EventTypes.PAUSE) {
            long curTime = System.currentTimeMillis();
            PlayEvent playEvent = new PlayEvent(
                    getSimTime(), curTime, getRealTime()
            );
            addEvent(playEvent);
        }
    }

    // Add pause event for current thread
    public void pause() {
        pause(getThreadId());
    }

    // Add pause event for given thread
    public void pause(long id) {
        long curTime = System.currentTimeMillis();
        // Find matching play event
        SimEvent last = getLastPlayPauseEvent(
                history, id, history.size() - 1
        );

        if (last == null || last.getType() != EventTypes.PLAY) {
            System.out.println("Could not find matching play event");
            return;
        }
        PlayEvent playEvent = (PlayEvent) last;
        double diff = curTime - playEvent.getRunTime();

        addEvent(new PauseEvent(
                getSimTime(id) + diff,
                curTime,
                getRealTime(id) + diff
        ), id);
    }

    public void createJoinEvent(long child) {
        double parentSimTime = getSimTime();
        double childSimTime = getSimTime(child);

        JoinEvent joinEvent = new JoinEvent(
                Math.max(parentSimTime, childSimTime), getRealTime()
        );
        addEvent(joinEvent);
    }

    public void setUpNewThread(long child) {
        threadIds.add(child);
        addEvent(new ForkEvent(getSimTime(), getRealTime(), child), child);
    }

    public long setUpFakeThread() {
        long child = -1L - fakeThreadId.getAndIncrement();
        setUpNewThread(child);
        return child;
    }

    public void createTaskJoinEvent(Callable<?> task) {
        double parentSimTime = getSimTime();
        TaskFinishEvent event =
                callableBackwardsSearch(history, task);
        if (event == null) {
            throw new EventMissingException();
        }
        double childSimTime = event.getSimTime();

        TaskJoinEvent taskJoinEvent = new TaskJoinEvent(
                Math.max(parentSimTime, childSimTime), getRealTime()
        );
        addEvent(taskJoinEvent);
    }

    public void addModel(double time) {
        addEvent(
                new ModelEvent(getSimTime() + time, getRealTime(), time)
        );
    }

    public synchronized void addEvent(SimEvent event) {
        addEvent(event, getThreadId());
    }

    public synchronized void addEvent(SimEvent event, long id) {
        event.setThreadId(id);
        history.add(event);
    }

    // Helpers
    //////////////////////

    private SimEvent getLastPlayPauseEvent(List<SimEvent> history,
                                           long threadId, int searchIndex) {
        SimEvent event = backwardsSearch(
                history,
                new EventTypes[]{
                        EventTypes.PLAY, EventTypes.PAUSE, EventTypes.FORK
                },
                threadId,
                searchIndex
        );

        if (event == null) {
            return null;
        }

        // If found play event return
        if (event.getType().equals(EventTypes.PLAY) ||
                event.getType().equals(EventTypes.PAUSE)) {
            return event;
        }

        // Else event is fork
        ForkEvent forkEvent = (ForkEvent) event;

        long parentId = forkEvent.getParent();

        return getLastPlayPauseEvent(
                this.history, parentId, history.indexOf(forkEvent) - 1
        );
    }

    // Search a History for event which matches one given in types with id
    private SimEvent backwardsSearch(List<SimEvent> history, EventTypes[] types,
                                     Long threadId, int searchIndex) {
        for (int i = searchIndex; i >= 0; i--) {
            SimEvent event = history.get(i);
            if (Objects.isNull(types)) {
                if (threadId == null || event.getThreadId() == threadId) {
                    return event;
                }

                continue;
            }

            for (EventTypes type : types) {
                if (event.getType().equals(type) &&
                        (threadId == null || event.getThreadId() == threadId)) {
                    return event;
                }
            }
        }

        return null;
    }

    // Search a History for event which matches one given in types with thread id
    private SimEvent backwardsSearch(List<SimEvent> history, long threadId) {
        return backwardsSearch(
                history, null, threadId, history.size() - 1
        );
    }

    // Search a History for task finish event which matches callable
    private TaskFinishEvent callableBackwardsSearch(List<SimEvent> history,
                                                    Callable<?> task) {
        for (int i = history.size() - 1; i > 0; i--) {
            SimEvent event = history.get(i);
            if (event.getType() == EventTypes.TASK_FINISH) {
                TaskFinishEvent finishEvent = (TaskFinishEvent) event;
                if (finishEvent.getPerfCallable().getTask().equals(task)) {
                    return finishEvent;
                }
            }
        }

        return null;
    }

    private Long getThreadId() {
        long id = Thread.currentThread().getId();
        if (!threadIds.contains(id) && id != 1) {
            id = Long.parseLong(Thread.currentThread().getName());
        }
        return id;
    }
}
