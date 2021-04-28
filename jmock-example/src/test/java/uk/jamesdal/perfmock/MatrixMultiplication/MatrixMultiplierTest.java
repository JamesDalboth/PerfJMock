package uk.jamesdal.perfmock.MatrixMultiplication;

import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Rule;
import org.junit.Test;
import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.integration.junit4.perf.PerfMockery;
import uk.jamesdal.perfmock.perf.PerfRule;
import uk.jamesdal.perfmock.perf.postproc.reportgenerators.ConsoleReportGenerator;

import java.util.Random;

public class MatrixMultiplierTest {

    @Rule
    public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

    @Rule
    public PerfMockery ctx = new PerfMockery(perfRule);

    private final MatrixPrettyPrinter printer = ctx.mock(MatrixPrettyPrinter.class);

    @Test
    public void matrixPerfTest() {
        MatrixMultiplier matrixMultiplier = new MatrixMultiplier(printer);

        Random random = new Random();
        int size = 1000;

        double[][] data1 = new double[size][];
        double[][] data2 = new double[size][];

        for (int i = 0; i < size; i++) {
            data1[i] = random.doubles(size).toArray();
            data2[i] = random.doubles(size).toArray();
        }

        ctx.repeat(50, 0, () -> {
            ctx.checking(new Expectations() {{
                oneOf(printer).print(with(any(RealMatrix.class))); taking(milli(10));
            }});

            matrixMultiplier.multiplyAndPrint(data1, data2);
        });
    }

}