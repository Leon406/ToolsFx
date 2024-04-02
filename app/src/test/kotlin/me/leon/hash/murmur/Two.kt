package me.leon.hash.murmur

import me.leon.hash.murmur.Constant.Companion.UINT_MASK
import me.leon.hash.murmur.Constant.Companion.UNSIGNED_MASK

/**
 * A pure Kotlin implementation of the Murmur 2 hashing algorithm as presented at
 * [Murmur Project].toLong()(https://sites.google.com/site/murmurhash/)
 *
 * Code is ported from original Java source at https://github.com/sangupta/murmur
 *
 * @author chan
 * @since 1.0
 */
@Suppress("All")
object Two : Constant {

    /**
     * Compute the Murmur2 hash as described in the original source code.
     *
     * @param data the data that needs to be hashed
     * @param length the length of the data that needs to be hashed
     * @param seed the seed to use to compute the hash
     * @return the computed hash value
     */
    fun hash(data: ByteArray, length: Int, seed: Long): Long {
        val m = 0x5bd1e995L
        val r = 24

        // Initialize the hash to a 'random' value
        var hash = seed xor length.toLong() and UINT_MASK

        // Mix 4 bytes at a time into the hash
        val length4 = length.ushr(2)

        for (i in 0 until length4) {
            val i4 = i shl 2

            var k = data[i4].toLong() and UNSIGNED_MASK
            k = k or ((data[i4 + 1].toLong() and UNSIGNED_MASK) shl 8)
            k = k or ((data[i4 + 2].toLong() and UNSIGNED_MASK) shl 16)
            k = k or ((data[i4 + 3].toLong() and UNSIGNED_MASK) shl 24)

            k = k * m and UINT_MASK
            k = k xor (k.ushr(r) and UINT_MASK)
            k = k * m and UINT_MASK

            hash = hash * m and UINT_MASK
            hash = hash xor k and UINT_MASK
        }

        // Handle the last few bytes of the input array
        val offset = length4 shl 2
        when (length and 3) {
            3 -> {
                hash = hash xor (data[offset + 2].toLong() shl 16 and UINT_MASK)
                hash = hash xor (data[offset + 1].toLong() shl 8 and UINT_MASK)
                hash = hash xor (data[offset].toLong() and UINT_MASK)
                hash = hash * m and UINT_MASK
            }
            2 -> {
                hash = hash xor (data[offset + 1].toLong() shl 8 and UINT_MASK)
                hash = hash xor (data[offset].toLong() and UINT_MASK)
                hash = hash * m and UINT_MASK
            }
            1 -> {
                hash = hash xor (data[offset].toLong() and UINT_MASK)
                hash = hash * m and UINT_MASK
            }
        }

        hash = hash xor (hash.ushr(13) and UINT_MASK)
        hash = hash * m and UINT_MASK
        hash = hash xor hash.ushr(15)

        return hash
    }

    /**
     * Compute the Murmur2 hash (64-bit version) as described in the original source code.
     *
     * @param data the data that needs to be hashed
     * @param length the length of the data that needs to be hashed
     * @param seed the seed to use to compute the hash
     * @return the computed hash value
     */
    fun hash64(data: ByteArray, length: Int, seed: Long): Long {
        val m = -0x395b586ca42e166bL
        val r = 47

        var h = seed and UINT_MASK xor length * m

        val length8 = length shr 3

        for (i in 0 until length8) {
            val i8 = i shl 3

            var k =
                ((data[i8].toLong() and 0xff) +
                    (data[i8 + 1].toLong() and 0xff shl 8) +
                    (data[i8 + 2].toLong() and 0xff shl 16) +
                    (data[i8 + 3].toLong() and 0xff shl 24) +
                    (data[i8 + 4].toLong() and 0xff shl 32) +
                    (data[i8 + 5].toLong() and 0xff shl 40) +
                    (data[i8 + 6].toLong() and 0xff shl 48) +
                    (data[i8 + 7].toLong() and 0xff shl 56))

            k *= m
            k = k xor k.ushr(r)
            k *= m

            h = h xor k
            h *= m
        }

        when (length and 7) {
            7 -> {
                h = h xor ((data[(length and 7.inv()) + 6].toLong() and 0xff) shl 48)
                h = h xor ((data[(length and 7.inv()) + 5].toLong() and 0xff) shl 40)
                h = h xor ((data[(length and 7.inv()) + 4].toLong() and 0xff) shl 32)
                h = h xor ((data[(length and 7.inv()) + 3].toLong() and 0xff) shl 24)
                h = h xor ((data[(length and 7.inv()) + 2].toLong() and 0xff) shl 16)
                h = h xor ((data[(length and 7.inv()) + 1].toLong() and 0xff) shl 8)
                h = h xor (data[length and 7.inv()].toLong() and 0xff)
                h *= m
            }
            6 -> {
                h = h xor ((data[(length and 7.inv()) + 5].toLong() and 0xff) shl 40)
                h = h xor ((data[(length and 7.inv()) + 4].toLong() and 0xff) shl 32)
                h = h xor ((data[(length and 7.inv()) + 3].toLong() and 0xff) shl 24)
                h = h xor ((data[(length and 7.inv()) + 2].toLong() and 0xff) shl 16)
                h = h xor ((data[(length and 7.inv()) + 1].toLong() and 0xff) shl 8)
                h = h xor (data[length and 7.inv()].toLong() and 0xff)
                h *= m
            }
            5 -> {
                h = h xor ((data[(length and 7.inv()) + 4].toLong() and 0xff) shl 32)
                h = h xor ((data[(length and 7.inv()) + 3].toLong() and 0xff) shl 24)
                h = h xor ((data[(length and 7.inv()) + 2].toLong() and 0xff) shl 16)
                h = h xor ((data[(length and 7.inv()) + 1].toLong() and 0xff) shl 8)
                h = h xor (data[length and 7.inv()].toLong() and 0xff)
                h *= m
            }
            4 -> {
                h = h xor ((data[(length and 7.inv()) + 3].toLong() and 0xff) shl 24)
                h = h xor ((data[(length and 7.inv()) + 2].toLong() and 0xff) shl 16)
                h = h xor ((data[(length and 7.inv()) + 1].toLong() and 0xff) shl 8)
                h = h xor (data[length and 7.inv()].toLong() and 0xff)
                h *= m
            }
            3 -> {
                h = h xor ((data[(length and 7.inv()) + 2].toLong() and 0xff) shl 16)
                h = h xor ((data[(length and 7.inv()) + 1].toLong() and 0xff) shl 8)
                h = h xor (data[length and 7.inv()].toLong() and 0xff)
                h *= m
            }
            2 -> {
                h = h xor ((data[(length and 7.inv()) + 1].toLong() and 0xff) shl 8)
                h = h xor (data[length and 7.inv()].toLong() and 0xff)
                h *= m
            }
            1 -> {
                h = h xor (data[length and 7.inv()].toLong() and 0xff)
                h *= m
            }
        }

        h = h xor h.ushr(r)
        h *= m
        h = h xor h.ushr(r)

        return h
    }
}
