package uk.jamesdal.perfmock.FriendServiceExecutor;

import uk.jamesdal.perfmock.perf.postproc.PerfStatistics;
import uk.jamesdal.perfmock.perf.postproc.ReportGenerator;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.HtmlReportGenerator;

public class ConsoleHtmlReporter implements ReportGenerator {

    private final ConsoleReportGenerator consoleReportGenerator;
    private final HtmlReportGenerator htmlReportGenerator;

    public ConsoleHtmlReporter(ConsoleReportGenerator consoleReportGenerator, HtmlReportGenerator htmlReportGenerator) {
        this.consoleReportGenerator = consoleReportGenerator;
        this.htmlReportGenerator = htmlReportGenerator;
    }

    @Override
    public void setStats(PerfStatistics stats) {
        consoleReportGenerator.setStats(stats);
        htmlReportGenerator.setStats(stats);
    }

    @Override
    public void generateReport(String testName) {
        consoleReportGenerator.generateReport(testName);
        htmlReportGenerator.generateReport(testName);
    }

    @Override
    public void generateReport() {
        consoleReportGenerator.generateReport();
        htmlReportGenerator.generateReport();
    }
}
