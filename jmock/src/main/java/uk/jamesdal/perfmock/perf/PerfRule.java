package uk.jamesdal.perfmock.perf;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.jamesdal.perfmock.perf.annotations.PerfRequirement;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.postproc.PerfRequirements;
import uk.jamesdal.perfmock.perf.postproc.PerfStatistics;
import uk.jamesdal.perfmock.perf.postproc.ReportGenerator;
import uk.jamesdal.perfmock.perf.postproc.RequirementChecker;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

import java.util.Objects;

public class PerfRule implements TestRule {
    private final Simulation simulation;

    public PerfRule() {
        this(new ConsoleReportGenerator());
    }

    public PerfRule(ReportGenerator reportGenerator) {
        this.simulation = new Simulation(reportGenerator);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        PerfTest perfTestAnnotation = description.getAnnotation(PerfTest.class);

        PerfRequirement[] requirements;
        PerfRequirements perfRequirements =
                description.getAnnotation(PerfRequirements.class);
        if (perfRequirements != null) {
            requirements = perfRequirements.value();
        } else {
            PerfRequirement perfRequirement =
                    description.getAnnotation(PerfRequirement.class);
            if (perfRequirement != null) {
                requirements = new PerfRequirement[] {perfRequirement};
            } else {
                requirements = new PerfRequirement[0];
            }
        }

        Statement statement = base;
        if (Objects.nonNull(perfTestAnnotation)) {
            statement = new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    long iterations = perfTestAnnotation.iterations();
                    long warmups = perfTestAnnotation.warmups();

                    simulation.enable();

                    for (int i = 0; i < warmups; i++) {
                        run(base);
                    }

                    for (int i = 0; i < iterations; i++) {
                        runWithSave(base);
                    }

                    try {
                        RequirementChecker.doesStatsMatchRequirements(
                                simulation.getStats(), requirements
                        );
                    } finally {
                        simulation.genReport();
                        simulation.disable();
                    }
                }
            };
        }

        return statement;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void run(Runnable task) {
        simulation.reset();

        simulation.play();
        task.run();
        simulation.pause();
    }

    public void runWithSave(Runnable task) {
        run(task);

        simulation.save();
    }

    public void run(Statement base) {
        run(() -> {
            try {
                base.evaluate();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void runWithSave(Statement base) {
        runWithSave(() -> {
            try {
                base.evaluate();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public PerfStatistics perfResults() {
        return simulation.getStats();
    }

    public PerfThreadFactory getThreadFactory() {
        return new PerfThreadFactory(simulation);
    }

    public void genReport() {
        simulation.genReport();
    }
}
