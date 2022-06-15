package me.leon.ctf.rsa

import java.math.BigInteger
import me.leon.*

object RsaSolver {

    private val modeNEC = listOf("n", "e", "c")
    private val modeN2E2C2 = listOf("n1", "e1", "c1", "n2", "e2", "c2")
    private val modeN2EC2 = listOf("n1", "c1", "n2", "e", "c2")
    private val modeEC = listOf("e", "c")
    private val modeNECPhi = listOf("n", "e", "c", "phi")
    private val modePQEC = listOf("p", "q", "e", "c")
    private val modeDpDq = listOf("dp", "dq", "p", "q", "c")
    private val modeDp = listOf("dp", "e", "c", "n")

    /**
     * 1. dp泄露
     * 2. e = 1
     * 3. e大小判断,小于6直接开方
     * 4. n为素数
     * 5. NC互素判断 N C 不互素 gcm(n,c) = p
     * 6. factor Db分解,自动过滤合数
     */
    fun solve(params: MutableMap<String, BigInteger>): String =
        when {
            params.containKeys(modeNECPhi) ->
                params["c"]!!.decrypt(params["e"]!!.invert(params["phi"]!!), params["n"]!!)
            params.containKeys(modeDp) && params["dq"] == null -> dpLeak(params)
            params.containKeys(modeDpDq) -> solveDpDq(params)
            params.containKeys(modeN2E2C2) -> solveN2E2C2(params)
            params.containKeys(modeN2EC2) -> solveN2EC2(params)
            params.containKeys(modePQEC) -> solvePQEC(params)
            params.containKeys(modeEC) && params["e"] == BigInteger.ONE -> params["c"]!!.n2s()
            params.containKeys(modeNEC) && params["e"]!! < 6.toBigInteger() ->
                smallE(params["n"]!!, params["c"]!!, params["e"]!!)
            params.containKeys(modeNEC) && params["n"]!!.isProbablePrime(100) -> {
                println("prime")
                val c = requireNotNull(params["c"])
                val n = requireNotNull(params["n"])
                val e = requireNotNull(params["e"])
                val phi = n - BigInteger.ONE
                (c.modPow(e.invert(phi), n)).also { println(it) }.n2s()
            }
            params.containKeys(modeNEC) && params["n"]!!.gcd(params["c"]) != BigInteger.ONE -> {
                println("nc")
                val c = requireNotNull(params["c"])
                val n = requireNotNull(params["n"])
                val e = requireNotNull(params["e"])
                val p = n.gcd(c)
                val q = n / p
                val phi = p.phi(q)
                (c.modPow(e.invert(phi), n) / p).n2s()
            }
            else -> factorDbSolve(params["n"]!!, params["e"]!!, params["c"]!!)
        }

    private fun solveN2EC2(params: MutableMap<String, BigInteger>): String {
        val e = requireNotNull(params["e"])
        val n1 = requireNotNull(params["n1"])
        val c1 = requireNotNull(params["c1"])
        val n2 = requireNotNull(params["n2"])
        val p = n1.gcd(n2)
        val q1 = n1 / p
        val d1 = e.invert(p.phi(q1))
        return c1.decrypt(d1, n1)
    }

    fun solveN2E2C2(params: MutableMap<String, BigInteger>): String {
        val n1 = requireNotNull(params["n1"])
        val n2 = requireNotNull(params["n2"])
        val e1 = requireNotNull(params["e1"])
        val e2 = requireNotNull(params["e2"])
        val c1 = requireNotNull(params["c1"])
        val c2 = requireNotNull(params["c2"])
        val (_, s1, s2) = e1.gcdExt(e2)
        return (c1.modPow(s1, n1) * c2.modPow(s2, n2) % n1).n2s()
    }

    private fun factorDbSolve(n: BigInteger, e: BigInteger, c: BigInteger): String {
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

    private fun smallE(n: BigInteger, c: BigInteger, e: BigInteger): String {
        for (k in 1..1000) {
            val m = (k.toBigInteger() * n + c).root(e.toInt())
            if (m[1] == BigInteger.ZERO) {
                return m.first().n2s()
            }
        }
        return "no solution"
    }

    private fun dpLeak(params: MutableMap<String, BigInteger>): String {
        println("dp leak")
        val n = requireNotNull(params["n"])
        val e = requireNotNull(params["e"])
        val c = requireNotNull(params["c"])
        val dp = requireNotNull(params["dp"])
        var p = BigInteger.ONE
        var q = BigInteger.ONE
        for (k in 1..65_537) {
            p = (e * dp - BigInteger.ONE) / k.toBigInteger() + BigInteger.ONE
            if (n.gcd(p) != BigInteger.ONE) {
                q = n / p
                break
            }
        }
        return c.decrypt(e.invert(p.phi(q)), n)
    }

    private fun solveDpDq(params: MutableMap<String, BigInteger>): String {
        val p = requireNotNull(params["p"])
        val q = requireNotNull(params["q"])
        val c = requireNotNull(params["c"])
        val dp = requireNotNull(params["dp"])
        val dq = requireNotNull(params["dq"])

        val invQ = q.invert(p)
        val mp = c.modPow(dp, p)
        val mq = c.modPow(dq, q)

        val m = (((mp - mq) * invQ) % p) * q + mq

        return m.n2s()
    }

    /** 知p,q, 即只知 n d e phi互素判断 */
    fun solvePQEC(params: MutableMap<String, BigInteger>): String {
        val p = requireNotNull(params["p"])
        val q = requireNotNull(params["q"])
        val e = requireNotNull(params["e"])
        val c = requireNotNull(params["c"])

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

fun <T : Any> MutableMap<String, T>.containKeys(keys: List<String>) =
    keys.map { this[it] }.all { it != null }
