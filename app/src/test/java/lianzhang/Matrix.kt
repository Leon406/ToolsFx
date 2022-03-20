package lianzhang

import kotlin.math.pow
import kotlin.math.sqrt

fun Array<IntArray>.determinant(n: Int): Int {
    var res: Int
    if (n == 1) res = this[0][0]
    else if (n == 2) {
        res = this[0][0] * this[1][1] - this[1][0] * this[0][1]
    } else {
        res = 0
        for (j1 in 0 until n) {
            val m = Array(n - 1) { IntArray(n - 1) }
            for (i in 1 until n) {
                var j2 = 0
                for (j in 0 until n) {
                    if (j == j1) continue
                    m[i - 1][j2] = this[i][j]
                    j2++
                }
            }
            res += ((-1.0).pow(1.0 + j1 + 1.0) * this[0][j1] * m.determinant(n - 1)).toInt()
        }
    }
    return res
}

fun List<Int>.reshape(dimension: Int) =
    if (sqrt(this.size.toDouble()) == dimension.toDouble()) {
        chunked(dimension).map { it.toIntArray() }.toTypedArray()
    } else {
        throw IllegalArgumentException("wrong dimension or list")
    }

/** 乘法模逆元 3*9 == 1 mod 26 3 9的互为关于26的模逆元 */
fun Int.modInverse(modular: Int = 26): Int {
    var q: Int
    var r1: Int
    var r2: Int
    var r: Int
    var t1: Int
    var t2: Int
    var t: Int
    r1 = modular
    r2 = this
    t1 = 0
    t2 = 1
    while (r1 != 1 && r2 != 0) {
        q = r1 / r2
        r = r1 % r2
        t = t1 - t2 * q
        r1 = r2
        r2 = r
        t1 = t2
        t2 = t
    }
    return (t1 + t2).takeIf { it >= 0 } ?: (t1 + t2 + 26)
}

/** 乘法取余 矩阵乘以 矢量 */
fun Array<IntArray>.multMod(col: IntArray, modular: Int): IntArray {
    return if (this[0]?.size == col.size) {
        this.foldIndexed(IntArray(col.size)) { i, acc, ints ->
            acc.apply {
                acc[i] = ints.foldIndexed(0) { j, acc2, int -> acc2 + int * col[j] } % modular
            }
        }
    } else {
        throw IllegalArgumentException("col size must be matrix's row")
    }
}

fun Array<IntArray>.showMatrix() {
    println(
        ">>>>>\tmatrix row $size  col ${this[0].size}\n" +
            joinToString("\n") { it.joinToString("  ") } +
            "\n<<<<<<"
    )
}

fun IntArray.showColum() {
    println(">>>>>\tcolum ${this.size}\n" + joinToString("\t\n") + "\n<<<<<<")
}

fun Array<IntArray>.invertModMatrix(modular: Int = 26): Array<IntArray> {

    val b: Array<IntArray> = Array(size) { IntArray(size) }
    val fac: Array<IntArray> = Array(size) { IntArray(size) }
    var p: Int
    var m: Int
    var n: Int
    var i: Int
    var j: Int
    var q = 0
    while (q < size) {
        p = 0
        while (p < size) {
            m = 0
            n = 0
            i = 0
            while (i < size) {
                j = 0
                while (j < size) {
                    b[i][j] = 0
                    if (i != q && j != p) {
                        b[m][n] = this[i][j]
                        if (n < size - 2) n++
                        else {
                            n = 0
                            m++
                        }
                    }
                    j++
                }
                i++
            }
            fac[q][p] = (-1.0).pow((q + p).toDouble()).toInt() * b.determinant(size - 1)
            p++
        }
        q++
    }
    return fac.trans(this, size, modular)
}

fun Array<IntArray>.trans(key: Array<IntArray>, r: Int, modular: Int = 26): Array<IntArray> {
    var j: Int
    val b: Array<IntArray> = Array(r) { IntArray(r) }
    val inv: Array<IntArray> = Array(r) { IntArray(r) }
    val d = key.determinant(r)
    var mi = d.modInverse(modular)
    var i = 0
    while (i < r) {
        j = 0
        while (j < r) {
            b[i][j] = this[j][i]
            j++
        }
        i++
    }
    i = 0
    while (i < r) {
        j = 0
        while (j < r) {
            inv[i][j] = b[i][j] % modular
            if (inv[i][j] < 0) inv[i][j] += modular
            inv[i][j] *= mi
            inv[i][j] %= modular
            j++
        }
        i++
    }
    return inv
}
