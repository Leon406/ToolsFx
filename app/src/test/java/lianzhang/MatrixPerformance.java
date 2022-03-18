package lianzhang;

import static org.ejml.dense.row.CommonOps_DDRM.*;

import org.ejml.data.DMatrixRMaj;
import org.jblas.DoubleMatrix;
import org.jblas.Solve;

public class MatrixPerformance {
    // http://ejml.org/wiki/index.php?title=Main_Page
    static void TstEjmpMatrix() {

        DMatrixRMaj x = new DMatrixRMaj(2000, 5000);
        DMatrixRMaj y = new DMatrixRMaj(2000, 5000);
        DMatrixRMaj z = new DMatrixRMaj(5000, 4000);
        DMatrixRMaj r = new DMatrixRMaj(4000, 5000);
        DMatrixRMaj r1 = new DMatrixRMaj(4000, 4000);
        for (int i = 0; i < x.numRows; i++) {
            for (int k = 0; k < x.numCols; k++) {
                x.unsafe_set(i, k, i * 2 + k + 1);
            }
        }
        System.out.println("aaaa\n");
        for (int i = 0; i < y.numRows; i++) {
            for (int k = 0; k < y.numCols; k++) {

                y.unsafe_set(i, k, i * 3 + k + 2);
                // System.out.print("  "+y.get(i,k));
            }
            // System.out.println();
        }
        System.out.println("\n");

        for (int i = 0; i < z.numRows; i++) {
            for (int k = 0; k < z.numCols; k++) {

                z.unsafe_set(i, k, i * 3 + k + 2);
                // System.out.print("  "+y.get(i,k));
            }
            // System.out.println();
        }
        System.out.println("\n");

        concatRows(x, y, r);
        mult(r, z, r1);
        invert(r1, r1);

        for (int i = 0; i < x.numRows; i++) {
            for (int k = 0; k < x.numCols; k++) {

                // System.out.print("  "+x.get(i,k));
            }
            //  System.out.println();
        }
    }

    static void TstJblasMatrix() {
        DoubleMatrix x = new DoubleMatrix(2000, 5000);
        DoubleMatrix y = new DoubleMatrix(2000, 5000);
        DoubleMatrix z = new DoubleMatrix(5000, 4000);

        for (int i = 0; i < x.rows; i++) {
            for (int k = 0; k < x.columns; k++) {
                x.put(i, k, i * 2 + k + 1);

                // System.out.print("  "+x.get(i,k));
            }
            // System.out.println();
        }
        System.out.println("\n");
        for (int i = 0; i < y.rows; i++) {
            for (int k = 0; k < y.columns; k++) {

                y.put(i, k, i * 3 + k + 2);
                // System.out.print("  "+y.get(i,k));
            }
            // System.out.println();
        }
        System.out.println("\n");

        for (int i = 0; i < z.rows; i++) {
            for (int k = 0; k < z.columns; k++) {

                z.put(i, k, i * 3 + k + 2);
                // System.out.print("  "+y.get(i,k));
            }
            // System.out.println();
        }
        System.out.println("\n");

        x = DoubleMatrix.concatVertically(x, y);
        x = x.mmul(z);
        x = Solve.pinv(x);

        for (int i = 0; i < x.rows; i++) {
            for (int k = 0; k < x.columns; k++) {

                // System.out.print("  "+x.get(i,k));
            }
            // System.out.println();
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        TstEjmpMatrix();
        long end = System.currentTimeMillis();
        System.out.printf("EjmpMatrix: %d ms\n", end - start);
        start = end;
        TstJblasMatrix();
        end = System.currentTimeMillis();
        System.out.println("JblasMatrix: " + (end - start) + " ms");
    }
}
