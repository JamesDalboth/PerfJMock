package uk.jamesdal.perfmock.perf;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import uk.jamesdal.perfmock.perf.annotations.PerfRequirement;
import uk.jamesdal.perfmock.perf.annotations.PerfTest;
import uk.jamesdal.perfmock.perf.postproc.PerfRequirements;
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

                    for (int i = 0; i < warmups; i++) {
                        simulation.reset();

                        simulation.play();
                        base.evaluate();
                        simulation.pause();
                    }

                    for (int i = 0; i < iterations; i++) {
                        simulation.reset();

                        simulation.play();
                        base.evaluate();
                        simulation.pause();

                        simulation.save();

                    }

                    RequirementChecker.doesStatsMatchRequirements(
                            simulation.getStats(), requirements
                    );

                    simulation.genReport();
                }
            };
        }

        return statement;
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
