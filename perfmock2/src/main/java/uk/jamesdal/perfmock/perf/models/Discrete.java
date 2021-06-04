package uk.jamesdal.perfmock.perf.models;

import uk.jamesdal.perfmock.api.Invocation;
import uk.jamesdal.perfmock.perf.PerfModel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Discrete implements PerfModel {

    private final Random rnd = new Random();

    private final int[] buckets;
    private final int dataCount;
    private final double minValue;
    private final double maxValue;

    public Discrete(String csvName) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(csvName));
        String[] header = csvReader.readLine().split(",");
        String[] body = csvReader.readLine().split(",");

        this.minValue = Double.parseDouble(header[0]);
        this.maxValue = Double.parseDouble(header[1]);
        this.dataCount = Integer.parseInt(header[2]);

        this.buckets = new int[body.length];
        for (int i = 0; i < body.length; i++) {
            buckets[i] = Integer.parseInt(body[i]);
        }
    }

    public Discrete(int[] buckets, int dataCount, double minValue, double maxValue) {
        this.buckets = buckets;
        this.dataCount = dataCount;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public double sample() {
        double u = rnd.nextDouble();
        int dataPoint = (int) Math.round(dataCount * u);

        int count = 0;
        for (int bucket = 0; bucket < buckets.length; bucket++) {
            int bucketVal = buckets[bucket];
            count += bucketVal;
            if (count >= dataPoint) {
                return (bucket * (maxValue - minValue) / buckets.length) + minValue;
            }
        }

        throw new SampleError();
    }

    // Not used
    @Override
    public void setInvocation(Invocation invocation) {}
}
