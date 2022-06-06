/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package me.leon.ext.crypto

import java.util.zip.Checksum

/**
 * CRC-64 implementation with ability to combine checksums calculated over different blocks of data.
 * Standard ECMA-182, http://www.ecma-international.org/publications/standards/Ecma-182.htm
 */
class CRC64 @JvmOverloads constructor(private var value: Long = 0L) : Checksum {

    init {
        for (n in 0..255) {
            var crc = n.toLong()
            repeat(8) { crc = if (crc and 1 == 1L) crc ushr 1 xor POLY else crc ushr 1 }
            table[n] = crc
        }
    }

    /** Get 8 byte representation of current CRC64 value. */
    val bytes: ByteArray
        get() {
            val b = ByteArray(8)
            for (i in 0..7) {
                b[7 - i] = (value ushr i * 8).toByte()
            }
            return b
        }
    constructor(b: ByteArray, len: Int) : this(0) {
        update(b, len)
    }

    /** Update CRC64 with new byte block. */
    fun update(b: ByteArray, len: Int) {
        var lenTmp = len
        var idx = 0
        value = value.inv()
        while (lenTmp > 0) {
            value = table[(value xor b[idx].toLong()).toInt() and 0xff] xor (value ushr 8)
            idx++
            lenTmp--
        }
        value = value.inv()
    }

    /** Update CRC64 with new byte. */
    fun update(b: Byte) {
        value = value.inv()
        value = table[(value xor b.toLong()).toInt() and 0xff] xor (value ushr 8)
        value = value.inv()
    }

    override fun update(b: Int) {
        update((b and 0xFF).toByte())
    }

    override fun update(b: ByteArray, off: Int, len: Int) {
        var tmpLen = len
        var i = off
        while (tmpLen > 0) {
            update(b[i++])
            tmpLen--
        }
    }

    override fun getValue(): Long {
        return value
    }

    override fun reset() {
        value = 0
    }

    fun crcHex() = value.toULong().toString(16)

    fun crcDecimal() = value.toULong().toString()

    companion object {
        // c96c5795d7870f42 kotlin signed long is  -0x3693a86a2878f0be
        private const val POLY = -0x3693a86a2878f0beL // ECMA-182

        // 0xD800000000000000L kotlin signed long is  0x972777519512027136
        //        private const val POLY = 0x972777519512027136 // ISO
        /* CRC64 calculation table. */
        private val table: LongArray = LongArray(256)

        private const val GF2_DIM = 64

        /** Construct new CRC64 instance from byte array. */
        fun fromBytes(b: ByteArray): CRC64 {
            var l: Long = 0
            for (i in 0..3) {
                l = l shl 8
                l = l xor (b[i].toLong() and 0xFF)
            }
            return CRC64(l)
        }

        /*
         * dimension of GF(2) vectors (length
         * of CRC)
         */
        private fun gf2MatrixTimes(mat: LongArray, vec: Long): Long {
            var vecTmp = vec
            var sum: Long = 0
            var idx = 0
            while (vecTmp != 0L) {
                if (vecTmp and 1 == 1L) sum = sum xor mat[idx]
                vecTmp = vecTmp ushr 1
                idx++
            }
            return sum
        }

        private fun gf2MatrixSquare(square: LongArray, mat: LongArray) {
            for (n in 0 until GF2_DIM) square[n] = gf2MatrixTimes(mat, mat[n])
        }

        /*
         * Return the CRC-64 of two sequential blocks, where sum1 is the CRC-64 of
         * the first block, sum2 is the CRC-64 of the second block, and len2 is the
         * length of the second block.
         */
        fun combine(sum1: CRC64, sum2: CRC64, len2: Long): CRC64 {
            // degenerate case.
            var tmpLen = len2
            if (tmpLen == 0L) return CRC64(sum1.getValue())
            val (even, odd) = makeOddEvenArray()

            // apply len2 zeros to crc1 (first square will put the operator for one
            // zero byte, eight zero bits, in even)
            var crc1 = sum1.getValue()
            val crc2 = sum2.getValue()
            do {
                // apply zeros operator for this bit of len2
                gf2MatrixSquare(even, odd)
                if (tmpLen and 1 == 1L) crc1 = gf2MatrixTimes(even, crc1)
                tmpLen = tmpLen ushr 1

                // if no more bits set, then done
                if (tmpLen == 0L) break

                // another iteration of the loop with odd and even swapped
                gf2MatrixSquare(odd, even)
                if (tmpLen and 1 == 1L) crc1 = gf2MatrixTimes(odd, crc1)
                tmpLen = tmpLen ushr 1

                // if no more bits set, then done
            } while (tmpLen != 0L)

            // return combined crc.
            crc1 = crc1 xor crc2
            return CRC64(crc1)
        }

        private fun makeOddEvenArray(): Pair<LongArray, LongArray> {
            var row: Long
            val even = LongArray(GF2_DIM) // even-power-of-two zeros operator
            val odd = LongArray(GF2_DIM) // odd-power-of-two zeros operator

            // put operator for one zero bit in odd
            odd[0] = POLY // CRC-64 polynomial
            row = 1
            var n = 1
            while (n < GF2_DIM) {
                odd[n] = row
                row = row shl 1
                n++
            }

            // put operator for two zero bits in even
            gf2MatrixSquare(even, odd)

            // put operator for four zero bits in odd
            gf2MatrixSquare(odd, even)
            return Pair(even, odd)
        }

        /*
         * Return the CRC-64 of two sequential blocks, where sum1 is the CRC-64 of
         * the first block, sum2 is the CRC-64 of the second block, and len2 is the
         * length of the second block.
         */
        fun combine(crc1: Long, crc2: Long, len2: Long): Long {
            // degenerate case.
            var lenTmp = len2
            if (lenTmp == 0L) return crc1
            var crc1Tmp = crc1

            val (even, odd) = makeOddEvenArray()
            // apply len2 zeros to crc1 (first square will put the operator for one
            // zero byte, eight zero bits, in even)
            do {
                // apply zeros operator for this bit of len2
                gf2MatrixSquare(even, odd)
                if (lenTmp and 1 == 1L) crc1Tmp = gf2MatrixTimes(even, crc1Tmp)
                lenTmp = lenTmp ushr 1

                // if no more bits set, then done
                if (lenTmp == 0L) break

                // another iteration of the loop with odd and even swapped
                gf2MatrixSquare(odd, even)
                if (lenTmp and 1 == 1L) crc1Tmp = gf2MatrixTimes(odd, crc1Tmp)
                lenTmp = lenTmp ushr 1

                // if no more bits set, then done
            } while (lenTmp != 0L)

            // return combined crc.
            crc1Tmp = crc1Tmp xor crc2
            return crc1Tmp
        }
    }
}
