package me.leon

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM.*
import org.ejml.kotlin.*
import org.junit.Test

class MatrixTest {
    @Test
    fun matrixTe() {

        val m1 =
            DMatrixRMaj(
                    arrayOf(
                        doubleArrayOf(6.0, 24.0, 1.0),
                        doubleArrayOf(13.0, 16.0, 10.0),
                        doubleArrayOf(20.0, 17.0, 15.0)
                    )
                )
                .also { println(it) }

        //        val m2 = DMatrixRMaj(
        //            arrayOf(
        //                doubleArrayOf(1.0, 0.0, 0.0),
        //                doubleArrayOf(0.0, 2.0, 0.0),
        //                doubleArrayOf(0.0, 0.0, 3.0)
        //            )
        //        ).also { println(it) }
        val col = DMatrixRMaj(doubleArrayOf(.0, 2.0, 9.0)).also { println(it) }

        (m1 * col).print()
        m1.inv().print()
    }

    fun DMatrixRMaj.inv(): DMatrixRMaj {
        invert(this, this)
        return this
    }

}
