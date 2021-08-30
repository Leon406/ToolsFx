package me.leon

import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.Throws

/* Inspired from https://github.com/adamcaudill/Base58Check/blob/master/src/Base58Check/Base58CheckEncoding.cs */
object Base58Check {
    private const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    private val ALPHABET_ARRAY = ALPHABET.toCharArray()
    private val BASE_SIZE = BigInteger.valueOf(ALPHABET_ARRAY.size.toLong())
    private const val CHECKSUM_SIZE = 4
    @Throws(NoSuchAlgorithmException::class)
    fun encode(data: ByteArray): String {
        return encodePlain(addChecksum(data))
    }

    fun encodePlain(data: ByteArray): String {
        var intData: BigInteger
        intData =
            try {
                BigInteger(1, data)
            } catch (e: NumberFormatException) {
                return ""
            }
        var result = ""
        while (intData.compareTo(BigInteger.ZERO) == 1) {
            val quotientAndRemainder = intData.divideAndRemainder(BASE_SIZE)
            val quotient = quotientAndRemainder[0]
            val remainder = quotientAndRemainder[1]
            intData = quotient
            result = ALPHABET_ARRAY[remainder.toInt()].toString() + result
        }
        var i = 0
        while (i < data.size && data[i].toInt() == 0) {
            result = "1$result"
            i++
        }
        return result
    }

    @Throws(NoSuchAlgorithmException::class)
    fun decode(encoded: String): ByteArray {
        val valueWithChecksum = decodePlain(encoded)
        return verifyAndRemoveChecksum(valueWithChecksum)
            ?: throw IllegalArgumentException("Base58 checksum is invalid")
    }

    fun decodePlain(encoded: String): ByteArray {
        if (encoded.isEmpty()) {
            return ByteArray(0)
        }
        var intData = BigInteger.ZERO
        var leadingZeros = 0
        for (i in encoded.indices) {
            val current = encoded[i]
            val digit = ALPHABET.indexOf(current)
            require(digit != -1) {
                String.format("Invalid Base58 character `%c` at position %d", current, i)
            }
            intData = intData.multiply(BASE_SIZE).add(BigInteger.valueOf(digit.toLong()))
        }
        for (element in encoded) {
            if (element == '1') {
                leadingZeros++
            } else {
                break
            }
        }
        val bytesData: ByteArray =
            if (intData == BigInteger.ZERO) {
                ByteArray(0)
            } else {
                intData.toByteArray()
            }

        // Should we cut the sign byte ? -
        // https://bitcoinj.googlecode.com/git-history/216deb2d35d1a128a7f617b91f2ca35438aae546/lib/src/com/google/bitcoin/core/Base58.java
        val stripSignByte = bytesData.size > 1 && bytesData[0].toInt() == 0 && bytesData[1] < 0
        val decoded = ByteArray(bytesData.size - (if (stripSignByte) 1 else 0) + leadingZeros)
        System.arraycopy(
            bytesData,
            if (stripSignByte) 1 else 0,
            decoded,
            leadingZeros,
            decoded.size - leadingZeros
        )
        return decoded
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun verifyAndRemoveChecksum(data: ByteArray): ByteArray? {
        val value = data.copyOfRange(0, data.size - CHECKSUM_SIZE)
        val checksum = data.copyOfRange(data.size - CHECKSUM_SIZE, data.size)
        val expectedChecksum = getChecksum(value)
        return if (checksum.contentEquals(expectedChecksum)) value else null
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun addChecksum(data: ByteArray): ByteArray {
        val checksum = getChecksum(data)
        val result = ByteArray(data.size + checksum.size)
        System.arraycopy(data, 0, result, 0, data.size)
        System.arraycopy(checksum, 0, result, data.size, checksum.size)
        return result
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getChecksum(data: ByteArray): ByteArray {
        var hash = hash256(data)
        hash = hash256(hash)
        println(String(hash))
        return hash.copyOfRange(0, CHECKSUM_SIZE)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun hash256(data: ByteArray?): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(data)
        return md.digest()
    }
}
