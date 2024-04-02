package me.leon.hash.murmur

import java.nio.ByteBuffer
import java.nio.ByteOrder
import me.leon.hash.murmur.Constant.Companion.UINT_MASK
import me.leon.hash.murmur.Constant.Companion.UNSIGNED_MASK

/**
 * A pure Kotlin implementation of the Murmur 3 hashing algorithm as presented at
 * [Murmur Project](https://sites.google.com/site/murmurhash/)
 *
 * Code is ported from original Java source at https://github.com/sangupta/murmur
 *
 * @author chan
 * @since 1.0
 */
@Suppress("All")
object Three : Constant {

    private const val X86_32_C1 = -0x3361d2af
    private const val X86_32_C2 = 0x1b873593
    private const val X64_128_C1 = -0x783c846eeebdac2bL
    private const val X64_128_C2 = 0x4cf5ad432745937fL

    /**
     * Compute the Murmur3 hash as described in the original source code.
     *
     * @param data the data that needs to be hashed
     * @param length the length of the data that needs to be hashed
     * @param seed the seed to use to compute the hash
     * @return the computed hash value
     */
    fun hash86b32(data: ByteArray, length: Int, seed: Long): Long {
        val nblocks = length shr 2
        var hash = seed

        // ----------
        // body
        for (i in 0 until nblocks) {
            val i4 = i shl 2

            var k1 = data[i4].toLong() and UNSIGNED_MASK
            k1 = k1 or (data[i4 + 1].toLong() and UNSIGNED_MASK shl 8)
            k1 = k1 or (data[i4 + 2].toLong() and UNSIGNED_MASK shl 16)
            k1 = k1 or (data[i4 + 3].toLong() and UNSIGNED_MASK shl 24)

            //			int k1 = (data[i4] & 0xff) + ((data[i4 + 1] & 0xff) << 8) + ((data[i4 + 2] & 0xff)
            // << 16) + ((data[i4 + 3] & 0xff) << 24);

            k1 = k1 * X86_32_C1 and UINT_MASK
            k1 = rotl32(k1, 15)
            k1 = k1 * X86_32_C2 and UINT_MASK

            hash = hash xor k1
            hash = rotl32(hash, 13)
            hash = (hash * 5 and UINT_MASK) + 0xe6546b64L and UINT_MASK
        }

        // ----------
        // tail

        // Advance offset to the unprocessed tail of the data.
        val offset = nblocks shl 2 // nblocks * 2;
        var k1: Long = 0

        when (length and 3) {
            3 -> {
                k1 = k1 xor (data[offset + 2].toLong() shl 16 and UINT_MASK)
                k1 = k1 xor (data[offset + 1].toLong() shl 8 and UINT_MASK)
                k1 = k1 xor data[offset].toLong()
                k1 = k1 * X86_32_C1 and UINT_MASK
                k1 = rotl32(k1, 15)
                k1 = k1 * X86_32_C2 and UINT_MASK
                hash = hash xor k1
            }
            2 -> {
                k1 = k1 xor (data[offset + 1].toLong() shl 8 and UINT_MASK)
                k1 = k1 xor data[offset].toLong()
                k1 = k1 * X86_32_C1 and UINT_MASK
                k1 = rotl32(k1, 15)
                k1 = k1 * X86_32_C2 and UINT_MASK
                hash = hash xor k1
            }
            1 -> {
                k1 = k1 xor data[offset].toLong()
                k1 = k1 * X86_32_C1 and UINT_MASK
                k1 = rotl32(k1, 15)
                k1 = k1 * X86_32_C2 and UINT_MASK
                hash = hash xor k1
            }
        }

        // ----------
        // finalization

        hash = hash xor length.toLong()
        hash = fmix32(hash)

        return hash
    }

    /**
     * Compute the Murmur3 hash (128-bit version) as described in the original source code.
     *
     * @param data the data that needs to be hashed
     * @param length the length of the data that needs to be hashed
     * @param seed the seed to use to compute the hash
     * @return the computed hash value
     */
    fun hash64b128(data: ByteArray, length: Int, seed: Long): LongArray {
        var h1 = seed
        var h2 = seed

        val buffer = ByteBuffer.wrap(data)
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        while (buffer.remaining() >= 16) {
            val k1 = buffer.long
            val k2 = buffer.long

            h1 = h1 xor mixK1(k1)

            h1 = java.lang.Long.rotateLeft(h1, 27)
            h1 += h2
            h1 = h1 * 5 + 0x52dce729

            h2 = h2 xor mixK2(k2)

            h2 = java.lang.Long.rotateLeft(h2, 31)
            h2 += h1
            h2 = h2 * 5 + 0x38495ab5
        }

        // ----------
        // tail

        // Advance offset to the unprocessed tail of the data.
        //		offset += (nblocks << 4); // nblocks * 16;

        buffer.compact()
        buffer.flip()

        val remaining = buffer.remaining()
        if (remaining > 0) {
            var k1: Long = 0
            var k2: Long = 0
            when (buffer.remaining()) {
                15 -> {
                    k2 = k2 xor ((buffer.getAsLong(14) and UNSIGNED_MASK) shl 48)
                    k2 = k2 xor ((buffer.getAsLong(13) and UNSIGNED_MASK) shl 40)
                    k2 = k2 xor ((buffer.getAsLong(12) and UNSIGNED_MASK) shl 32)
                    k2 = k2 xor ((buffer.getAsLong(11) and UNSIGNED_MASK) shl 24)
                    k2 = k2 xor ((buffer.getAsLong(10) and UNSIGNED_MASK) shl 16)
                    k2 = k2 xor ((buffer.getAsLong(9) and UNSIGNED_MASK) shl 8)
                    k2 = k2 xor (buffer.getAsLong(8) and UNSIGNED_MASK)
                    k1 = k1 xor buffer.long
                }
                14 -> {
                    k2 = k2 xor ((buffer.getAsLong(13) and UNSIGNED_MASK) shl 40)
                    k2 = k2 xor ((buffer.getAsLong(12) and UNSIGNED_MASK) shl 32)
                    k2 = k2 xor ((buffer.getAsLong(11) and UNSIGNED_MASK) shl 24)
                    k2 = k2 xor ((buffer.getAsLong(10) and UNSIGNED_MASK) shl 16)
                    k2 = k2 xor ((buffer.getAsLong(9) and UNSIGNED_MASK) shl 8)
                    k2 = k2 xor (buffer.getAsLong(8) and UNSIGNED_MASK)
                    k1 = k1 xor buffer.long
                }
                13 -> {
                    k2 = k2 xor ((buffer.getAsLong(12) and UNSIGNED_MASK) shl 32)
                    k2 = k2 xor ((buffer.getAsLong(11) and UNSIGNED_MASK) shl 24)
                    k2 = k2 xor ((buffer.getAsLong(10) and UNSIGNED_MASK) shl 16)
                    k2 = k2 xor ((buffer.getAsLong(9) and UNSIGNED_MASK) shl 8)
                    k2 = k2 xor (buffer.getAsLong(8) and UNSIGNED_MASK)
                    k1 = k1 xor buffer.long
                }
                12 -> {
                    k2 = k2 xor ((buffer.getAsLong(11) and UNSIGNED_MASK) shl 24)
                    k2 = k2 xor ((buffer.getAsLong(10) and UNSIGNED_MASK) shl 16)
                    k2 = k2 xor ((buffer.getAsLong(9) and UNSIGNED_MASK) shl 8)
                    k2 = k2 xor (buffer.getAsLong(8) and UNSIGNED_MASK)
                    k1 = k1 xor buffer.long
                }
                11 -> {
                    k2 = k2 xor ((buffer.getAsLong(10) and UNSIGNED_MASK) shl 16)
                    k2 = k2 xor ((buffer.getAsLong(9) and UNSIGNED_MASK) shl 8)
                    k2 = k2 xor (buffer.getAsLong(8) and UNSIGNED_MASK)
                    k1 = k1 xor buffer.long
                }
                10 -> {
                    k2 = k2 xor ((buffer.getAsLong(9) and UNSIGNED_MASK) shl 8)
                    k2 = k2 xor (buffer.getAsLong(8) and UNSIGNED_MASK)
                    k1 = k1 xor buffer.long
                }
                9 -> {
                    k2 = k2 xor (buffer.getAsLong(8) and UNSIGNED_MASK)
                    k1 = k1 xor buffer.long
                }
                8 -> k1 = k1 xor buffer.long
                7 -> {
                    k1 = k1 xor ((buffer.getAsLong(6) and UNSIGNED_MASK) shl 48)
                    k1 = k1 xor ((buffer.getAsLong(5) and UNSIGNED_MASK) shl 40)
                    k1 = k1 xor ((buffer.getAsLong(4) and UNSIGNED_MASK) shl 32)
                    k1 = k1 xor ((buffer.getAsLong(3) and UNSIGNED_MASK) shl 24)
                    k1 = k1 xor ((buffer.getAsLong(2) and UNSIGNED_MASK) shl 16)
                    k1 = k1 xor ((buffer.getAsLong(1) and UNSIGNED_MASK) shl 8)
                    k1 = k1 xor (buffer.getAsLong(0) and UNSIGNED_MASK)
                }
                6 -> {
                    k1 = k1 xor ((buffer.getAsLong(5) and UNSIGNED_MASK) shl 40)
                    k1 = k1 xor ((buffer.getAsLong(4) and UNSIGNED_MASK) shl 32)
                    k1 = k1 xor ((buffer.getAsLong(3) and UNSIGNED_MASK) shl 24)
                    k1 = k1 xor ((buffer.getAsLong(2) and UNSIGNED_MASK) shl 16)
                    k1 = k1 xor ((buffer.getAsLong(1) and UNSIGNED_MASK) shl 8)
                    k1 = k1 xor (buffer.getAsLong(0) and UNSIGNED_MASK)
                }
                5 -> {
                    k1 = k1 xor ((buffer.getAsLong(4) and UNSIGNED_MASK) shl 32)
                    k1 = k1 xor ((buffer.getAsLong(3) and UNSIGNED_MASK) shl 24)
                    k1 = k1 xor ((buffer.getAsLong(2) and UNSIGNED_MASK) shl 16)
                    k1 = k1 xor ((buffer.getAsLong(1) and UNSIGNED_MASK) shl 8)
                    k1 = k1 xor (buffer.getAsLong(0) and UNSIGNED_MASK)
                }
                4 -> {
                    k1 = k1 xor ((buffer.getAsLong(3) and UNSIGNED_MASK) shl 24)
                    k1 = k1 xor ((buffer.getAsLong(2) and UNSIGNED_MASK) shl 16)
                    k1 = k1 xor ((buffer.getAsLong(1) and UNSIGNED_MASK) shl 8)
                    k1 = k1 xor (buffer.getAsLong(0) and UNSIGNED_MASK)
                }
                3 -> {
                    k1 = k1 xor ((buffer.getAsLong(2) and UNSIGNED_MASK) shl 16)
                    k1 = k1 xor ((buffer.getAsLong(1) and UNSIGNED_MASK) shl 8)
                    k1 = k1 xor (buffer.getAsLong(0) and UNSIGNED_MASK)
                }
                2 -> {
                    k1 = k1 xor ((buffer.getAsLong(1) and UNSIGNED_MASK) shl 8)
                    k1 = k1 xor (buffer.getAsLong(0) and UNSIGNED_MASK)
                }
                1 -> k1 = k1 xor (buffer.getAsLong(0) and UNSIGNED_MASK)
                else -> throw AssertionError("Code should not reach here!")
            }

            // mix
            h1 = h1 xor mixK1(k1)
            h2 = h2 xor mixK2(k2)
        }

        // ----------
        // finalization

        h1 = h1 xor length.toLong()
        h2 = h2 xor length.toLong()

        h1 += h2
        h2 += h1

        h1 = fmix64(h1)
        h2 = fmix64(h2)

        h1 += h2
        h2 += h1

        return longArrayOf(h1, h2)
    }

    private fun mixK1(k1: Long): Long {
        var k1 = k1
        k1 *= X64_128_C1
        k1 = java.lang.Long.rotateLeft(k1, 31)
        k1 *= X64_128_C2

        return k1
    }

    private fun mixK2(k2: Long): Long {
        var k2 = k2
        k2 *= X64_128_C2
        k2 = java.lang.Long.rotateLeft(k2, 33)
        k2 *= X64_128_C1

        return k2
    }

    /**
     * Rotate left for 32 bits.
     *
     * @param original
     * @param shift
     * @return
     */
    private fun rotl32(original: Long, shift: Int): Long {
        return original shl shift and UINT_MASK or (original.ushr(32 - shift) and UINT_MASK)
    }

    /**
     * Rotate left for 64 bits.
     *
     * @param original
     * @param shift
     * @return
     */
    /**
     * fmix function for 32 bits.
     *
     * @param h
     * @return
     */
    private fun fmix32(h: Long): Long {
        var h = h
        h = h xor (h shr 16 and UINT_MASK)
        h = h * 0x85ebca6bL and UINT_MASK
        h = h xor (h shr 13 and UINT_MASK)
        h = h * -0x3d4d51cb and UINT_MASK
        h = h xor (h shr 16 and UINT_MASK)

        return h
    }

    /**
     * fmix function for 64 bits.
     *
     * @param k
     * @return
     */
    private fun fmix64(k: Long): Long {
        var k = k
        k = k xor k.ushr(33)
        k *= -0xae502812aa7333L
        k = k xor k.ushr(33)
        k *= -0x3b314601e57a13adL
        k = k xor k.ushr(33)

        return k
    }
}

private fun ByteBuffer.getAsLong(index: Int): Long {
    return get(index).toLong()
}
