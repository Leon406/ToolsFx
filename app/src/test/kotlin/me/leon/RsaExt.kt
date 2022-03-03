package me.leon

import java.math.BigInteger
import me.leon.ext.readFromNet

// this = p
fun BigInteger.phi(q: BigInteger) = (this - BigInteger.ONE) * (q - BigInteger.ONE)

// this = p
fun BigInteger.phi(q: String) = phi(BigInteger(q))

// this = e
fun BigInteger.d(phi: BigInteger) = modInverse(phi)

// this = e
fun BigInteger.d(phi: String) = d(BigInteger(phi))

// this = m   * c = m^e mod n m = c^d mod n
fun BigInteger.encrypt(e: BigInteger, n: BigInteger) = modPow(e, n).toString()

// this = c
fun BigInteger.decrypt(d: BigInteger, n: BigInteger) = modPow(d, n).toByteArray().decodeToString()
// this = c
fun BigInteger.decrypt(d: String, n: String) = decrypt(BigInteger(d), BigInteger(n))

// this = n
fun BigInteger.factorDb() = getPrimeFromFactorDb(this)

fun getPrimeFromFactorDb(digit: BigInteger) = getPrimeFromFactorDb(digit.toString())

fun getPrimeFromFactorDb(digit: String): List<BigInteger> {
    "http://www.factordb.com/index.php?query=$digit".readFromNet().also {
        "<td>(\\w+)</td>".toRegex().find(it)?.let {
            when (it.groupValues[1]) {
                "P" -> return listOf(digit.toBigInteger())
                "FF" -> "Composite, fully factored"
                "C" ->
                    return listOf(digit.toBigInteger()).also {
                        println("Composite, no factors known")
                    }
                "CF" -> "Composite, factors known"
                else -> return listOf(digit.toBigInteger()).also { println("Unknown") }
            }
        }

        "index\\.php\\?id=\\d+".toRegex().findAll(it).toList().map { it.value }.also {
            if (it.size >= 3) {
                val map = it.filterIndexed { i, _ -> i != 0 }.map { getPrimeFromFactorDbPath(it) }
                println("$digit = \n\t ${map.joinToString("\n\t*")}")
                return map
            } else {
                println("无法分解")
                return listOf(digit.toBigInteger())
            }
        }
    }
}

private fun getPrimeFromFactorDbPath(path: String) =
    "http://www.factordb.com/$path".readFromNet().run {
        "value=\"(\\d+)\"".toRegex().find(this)!!.groupValues[1].toBigInteger()
    }
