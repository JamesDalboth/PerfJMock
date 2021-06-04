package uk.jamesdal.perfmock.perf.postproc.reportgenerators;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import uk.jamesdal.perfmock.perf.postproc.PerfStatistics;
import uk.jamesdal.perfmock.perf.postproc.ReportGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HtmlReportGenerator implements ReportGenerator {

    private PerfStatistics stats;

    @Override
    public void setStats(PerfStatistics stats) {
        this.stats = stats;
    }

    @Override
    public void generateReport(String testName) {
        double minMeasuredTime = stats.minMeasuredTime();
        double maxMeasuredTime = stats.maxMeasuredTime();

        List<Double> measuredTimes = stats.getMeasuredTimes();

        int[] buckets = new int[20];
        Arrays.fill(buckets, 0);

        for (Double measuredTime : measuredTimes) {
            Double norm = (measuredTime - minMeasuredTime) / (maxMeasuredTime - minMeasuredTime);
            int bucket = Math.min(19, (int) Math.floor(norm * 20));
            buckets[bucket]++;
        }

        try {
            renderTemplate(buckets, minMeasuredTime, maxMeasuredTime, testName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateReport() {
        generateReport(null);
    }

    private void renderTemplate(int[] bucket, double min, double max, String testName) throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("template.html");
        String path = Paths.get(url.toURI()).toAbsolutePath().toString();

        TemplateEngine templateEngine = new TemplateEngine();
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateEngine.setTemplateResolver(templateResolver);

        double unit = (max - min) / 20;

        Context context = new Context();
        if (Objects.isNull(testName)) {
            context.setVariable("title", "PerfMock Results");
            context.setVariable("test.name", "");
        } else {
            context.setVariable("title", "PerfMock Results for " + testName);
            context.setVariable("testname", testName);
        }

        for (int i = 0; i < 20; i++) {
            context.setVariable("range" + (i + 1), round(i * unit + min) + "ms");
            context.setVariable("val" + (i + 1), bucket[i]);
        }

        context.setVariable("testaverage", round(stats.meanMeasuredTime()) + "ms");
        context.setVariable("testmin", round(stats.minMeasuredTime()) + "ms");
        context.setVariable("testmax", round(stats.maxMeasuredTime()) + "ms");
        context.setVariable("testvar", round(Math.sqrt(stats.varMeasuredTime())) + "ms");
        context.setVariable("testtotal", round(stats.totalRuntime()) + "ms");

        String html = templateEngine.process(path, context);

        File file;
        if (testName == null) {
            file = new File("PerfHtmlReport.html");
        } else {
            file = new File("PerfHtmlReport-" + testName + ".html");
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(html);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
