package uk.jamesdal.perfmock.integration.junit4.perf;

import uk.jamesdal.perfmock.AbstractExpectations;
import uk.jamesdal.perfmock.integration.junit4.JUnitRuleMockery;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.postproc.PerfStatistics;

public class PerfMockery extends JUnitRuleMockery {

    private final PerfRule perfRule;

    public PerfMockery(PerfRule perfRule) {
        this.perfRule = perfRule;
    }

    public void repeat(int iterations, Runnable task) {
        perfRule.getSimulation().enable();
        for (int i = 0; i < iterations; i++) {
            perfRule.runWithSave(task);
        }

        perfRule.genReport();
        perfRule.getSimulation().disable();
    }

    public void repeat(int iterations, int warmups, Runnable task) {
        perfRule.getSimulation().enable();
        for (int i = 0; i < warmups; i++) {
            perfRule.run(task);
        }

        repeat(iterations, task);
    }

    public void checking(AbstractExpectations expectations) {
        dispatcher.clear();
        expectations.setSimulation(perfRule.getSimulation());
        super.checking(expectations);
    }

    public PerfStatistics perfResults() {
        return perfRule.perfResults();
    }

    public PerfThreadFactory getThreadFactory() {
        return perfRule.getThreadFactory();
    }

    public void ignore(Runnable task) {
        perfRule.getSimulation().pause();
        perfRule.getSimulation().disable();
        task.run();
        perfRule.getSimulation().enable();
        perfRule.getSimulation().play();
    }
}
