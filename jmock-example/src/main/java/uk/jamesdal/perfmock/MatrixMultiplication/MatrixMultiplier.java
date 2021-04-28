package uk.jamesdal.perfmock.MatrixMultiplication;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class MatrixMultiplier {

    private final MatrixPrettyPrinter printer;

    public MatrixMultiplier(MatrixPrettyPrinter printer) {
        this.printer = printer;
    }

    public void multiplyAndPrint(double[][] data1, double[][] data2) {
        RealMatrix matrix1 = MatrixUtils.createRealMatrix(data1);
        RealMatrix matrix2 = MatrixUtils.createRealMatrix(data2);

        RealMatrix matrix3 = matrix1.multiply(matrix2);

        printer.print(matrix3);
    }
}
