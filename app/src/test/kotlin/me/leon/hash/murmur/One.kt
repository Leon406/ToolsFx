package me.leon.hash.murmur

import me.leon.hash.murmur.Constant.Companion.UINT_MASK
import me.leon.hash.murmur.Constant.Companion.UNSIGNED_MASK

/**
 * A pure Kotlin implementation of the Murmur 1 hashing algorithm as presented at
 * [Murmur Project](https://sites.google.com/site/murmurhash/)
 *
 * Code is ported from original Java source at https://github.com/sangupta/murmur
 *
 * @author chan
 * @since 1.0
 */
internal object One : Constant {

    /**
     * Compute the Murmur1 hash as described in the original source code.
     *
     * @param data the data that needs to be hashed
     * @param length the length of the data that needs to be hashed
     * @param seed the seed to use to compute the hash
     * @return the computed hash value
     */
    fun hash(data: ByteArray, length: Int, seed: Long): Long {
        val m = 0xc6a4a793L
        val r = 16

        var h = seed xor length * m

        // Mix 4 bytes at a time into the hash
        val length4 = length shr 2

        for (i in 0 until length4) {
            val i4 = i shl 2

            var k = data[i4].toLong() and UNSIGNED_MASK
            k = k or ((data[i4 + 1].toLong() and UNSIGNED_MASK) shl 8)
            k = k or (((data[i4 + 2].toLong() and UNSIGNED_MASK)) shl 16)
            k = k or ((data[i4 + 3].toLong() and UNSIGNED_MASK) shl 24)

            h = h + k and UINT_MASK
            h = h * m and UINT_MASK
            h = h xor (h shr 16 and UINT_MASK)
        }

        // remaining bytes
        val offset = length4 shl 2
        when (length and 3) {
            3 -> {
                h += data[offset + 2].toLong() shl 16 and UINT_MASK
                h += data[offset + 1].toLong() shl 8 and UINT_MASK
                h += data[offset].toLong() and UINT_MASK
                h = h * m and UINT_MASK
                h = h xor (h shr r and UINT_MASK)
            }
            2 -> {
                h += data[offset + 1].toLong() shl 8 and UINT_MASK
                h += data[offset].toLong() and UINT_MASK
                h = h * m and UINT_MASK
                h = h xor (h shr r and UINT_MASK)
            }
            1 -> {
                h += data[offset].toLong() and UINT_MASK
                h = h * m and UINT_MASK
                h = h xor (h shr r and UINT_MASK)
            }
        }

        // final operations
        h = h * m and UINT_MASK
        h = h xor (h shr 10 and UINT_MASK)
        h = h * m and UINT_MASK
        h = h xor (h shr 17 and UINT_MASK)

        // return the hash
        return h
    }

    /**
     * Compute the Murmur1 hash (aligned version) as described in the original source code.
     *
     * Code ported from original C++ version at
     * [MurmurHashAligned.cpp](https://sites.google.com/site/murmurhash/MurmurHashAligned.cpp?attredirects=0)
     *
     * @param data the data that needs to be hashed
     * @param length the length of the data that needs to be hashed
     * @param seed the seed to use to compute the hash
     * @return the computed hash value
     */
    fun hashAligned(data: ByteArray, length: Int, seed: Long): Long {
        return hash(data, length, seed)
    }
}
