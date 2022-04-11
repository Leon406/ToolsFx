package me.leon.asymmetric

import java.math.BigInteger
import me.leon.*

object RsaSolver {
    /** 1.e大小判断 2.NC互素判断 N C 不互素 gcm(n,c) = p 3.factor Db分解,自动过滤合数 */
    fun solveNEC(n: BigInteger, e: BigInteger, c: BigInteger): String {
        if (e < 6.toBigInteger()) {
            for (k in 1..1000) {
                val m = (k.toBigInteger() * n + c).root(e.toInt())
                if (m[1] == BigInteger.ZERO) {
                    return m.first().n2s()
                }
            }
            return "no solution"
        } else if (n.gcd(c) != BigInteger.ONE) {
            val p = n.gcd(c)
            val q = n / p
            val phi = p.phi(q)
            (c.modPow(e.invert(phi), n) / p).also {
                return it.n2s()
            }
        } else {
            n.factorDb().also {
                println(it)
                if (it.size >= 2) {
                    val phi = it.phi()
                    val d = e.invert(phi).also { println(it) }
                    val propN = it.propN(n)
                    return c.decrypt(d, propN).also { println(it) }
                }
                return "no solution"
            }
        }
    }

    /** 知p,q, 即只知 n d e phi互素判断 */
    fun solvePQEC(p: BigInteger, q: BigInteger, e: BigInteger, c: BigInteger): String {
        val n = p * q
        val phi = p.phi(q)

        return if (e.gcd(phi) == BigInteger.ONE) {
            val d = e.invert(p.phi(q))
            c.decrypt(d, p * q)
        } else {
            val t = e.gcd(phi)
            val t1 = e / t
            val dt1 = t1.invert(phi)
            c.modPow(dt1, n).root(t.toInt()).first().n2s()
        }
    }
}
