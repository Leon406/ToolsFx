/**
 * ********************************************************\ | | | XXTEA.java | | | | XXTEA
 * encryption algorithm library for Java. | | | | Encryption Algorithm Authors: | | David J. Wheeler
 * | | Roger M. Needham | | | | Code Authors: Ma Bingyao <mabingyao></mabingyao>@gmail.com> | |
 * LastModified: Mar 10, 2015 | | | \
 */
package me.leon.ext.crypto

object XXTEA {
    private const val DELTA = -0x61c88647

    private fun mx(sum: Int, y: Int, z: Int, p: Int, e: Int, k: IntArray): Int {
        return (z ushr 5 xor (y shl 2)) + (y ushr 3 xor (z shl 4)) xor
            (sum xor y) + (k[p and 3 xor e] xor z)
    }

    fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        return if (data.isEmpty()) {
            data
        } else {
            toByteArray(encrypt(toIntArray(data, true), toIntArray(fixKey(key), false)), false)
        }
    }

    @JvmStatic
    fun encrypt(data: String, key: String): ByteArray =
        encrypt(data.toByteArray(charset("UTF-8")), key.toByteArray(charset("UTF-8")))

    fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        return if (data.isEmpty()) {
            data
        } else {
            toByteArray(decrypt(toIntArray(data, false), toIntArray(fixKey(key), false)), true)
        }
    }

    fun decrypt(data: ByteArray, key: String): ByteArray = decrypt(data, key.toByteArray())

    @JvmStatic
    fun decryptToString(data: ByteArray, key: String) = String(decrypt(data, key), charset("UTF-8"))

    private fun encrypt(v: IntArray, k: IntArray): IntArray {
        val n = v.size - 1
        if (n < 1) {
            return v
        }
        var p: Int
        var q = 6 + 52 / (n + 1)
        var z = v[n]
        var y: Int
        var sum = 0L
        var e: Int
        while (q-- > 0) {
            sum += DELTA
            e = (sum ushr 2 and 3).toInt()
            p = 0
            while (p < n) {
                y = v[p + 1]
                v[p] += mx(sum.toInt(), y, z, p, e, k)
                z = v[p]
                p++
            }
            y = v[0]
            v[n] += mx(sum.toInt(), y, z, p, e, k)
            z = v[n]
        }
        return v
    }

    private fun decrypt(v: IntArray, k: IntArray): IntArray {
        val n = v.size - 1
        if (n < 1) {
            return v
        }
        var p: Int
        val q = 6 + 52 / (n + 1)
        var z: Int
        var y = v[0]
        var sum = q * DELTA
        var e: Int

        while (sum != 0) {
            e = sum ushr 2 and 3
            p = n
            while (p > 0) {
                z = v[p - 1]
                v[p] -= mx(sum, y, z, p, e, k)
                y = v[p]
                p--
            }
            z = v[n]
            v[0] -= mx(sum, y, z, p, e, k)
            y = v[0]
            sum -= DELTA
        }
        return v
    }

    private fun fixKey(key: ByteArray): ByteArray {
        if (key.size == 16) return key
        val fixedKey = ByteArray(16)
        System.arraycopy(key, 0, fixedKey, 0, Math.min(key.size, 16))
        return fixedKey
    }

    private fun toIntArray(data: ByteArray, includeLength: Boolean): IntArray {
        var n = if (data.size and 3 == 0) data.size ushr 2 else (data.size ushr 2) + 1
        val result: IntArray
        if (includeLength) {
            result = IntArray(n + 1)
            result[n] = data.size
        } else {
            result = IntArray(n)
        }
        n = data.size
        for (i in 0 until n) {
            result[i ushr 2] =
                result[i ushr 2] or (0x000000ff and data[i].toInt() shl (i and 3 shl 3))
        }
        return result
    }

    private fun toByteArray(data: IntArray, includeLength: Boolean): ByteArray {
        var n = data.size shl 2
        if (includeLength) {
            val m = data[data.size - 1]
            n -= 4
            if (m < n - 3 || m > n) {
                return byteArrayOf()
            }
            n = m
        }
        val result = ByteArray(n)
        for (i in 0 until n) {
            result[i] = (data[i ushr 2] ushr (i and 3 shl 3)).toByte()
        }
        return result
    }
}
