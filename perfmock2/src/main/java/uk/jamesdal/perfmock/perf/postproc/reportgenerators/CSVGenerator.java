package uk.jamesdal.perfmock.perf.postproc.reportgenerators;

import uk.jamesdal.perfmock.perf.postproc.PerfStatistics;
import uk.jamesdal.perfmock.perf.postproc.ReportGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVGenerator implements ReportGenerator {
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
            writeToCSV(testName, minMeasuredTime, maxMeasuredTime, measuredTimes.size(), buckets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToCSV(String testName, double min, double max, int bucketCount, int[] buckets) throws IOException {
        FileWriter csvWriter = new FileWriter("perfmock2-" + testName + "-data.csv");
        csvWriter.append(min + ",");
        csvWriter.append(max + ",");
        csvWriter.append(bucketCount + "\n");

        List<String> bucketData = new ArrayList<>();
        for (int bucket : buckets) {
            bucketData.add(String.valueOf(bucket));
        }

        csvWriter.append(String.join(",", bucketData));
        csvWriter.append("\n");

        csvWriter.flush();
        csvWriter.close();
    }

    @Override
    public void generateReport() {
        generateReport(null);
    }
}
