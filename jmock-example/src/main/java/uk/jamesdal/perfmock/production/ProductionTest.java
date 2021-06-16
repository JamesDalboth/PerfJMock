package uk.jamesdal.perfmock.production;

import uk.jamesdal.perfmock.FriendServiceExecutor.FSNativeProduction;
import uk.jamesdal.perfmock.WeatherMan.WeatherProduction;
import uk.jamesdal.perfmock.perf.postproc.IterResult;
import uk.jamesdal.perfmock.perf.postproc.PerfStatistics;
import uk.jamesdal.perfmock.perf.postproc.ReportGenerator;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;


import java.util.ArrayList;
import java.util.List;

public class ProductionTest {
    private static final int ITERATIONS = 100;
    private static final ReportGenerator REPORT_GENERATOR = new ConsoleReportGenerator();

    private static final Runner runner = new WeatherProduction();

    public static void main(String[] args) {
        List<IterResult> results = new ArrayList<>();
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.currentTimeMillis();
            runner.run();
            long end = System.currentTimeMillis();
            long runTime = end - start;

            results.add(new IterResult(runTime, 0));
        }

        REPORT_GENERATOR.setStats(new PerfStatistics(results));
        REPORT_GENERATOR.generateReport();
    }

    public interface Runner {
        void run();
    }
}
