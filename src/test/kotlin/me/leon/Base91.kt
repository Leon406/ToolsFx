package me.leon

import java.io.ByteArrayOutputStream
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Modified version of Jochaim Henke's original code from http://base91.sourceforge.net/
 *
 * basE91 encoding/decoding routines
 *
 * Copyright (c) 2000-2006 Joachim Henke All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of Joachim Henke nor the names of
 * his contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Joachim Henke (Original version)
 * @author Benedikt Waldvogel (Modifications)
 */
object Base91 {

    private var DECODING_TABLE: ByteArray = ByteArray(256).apply { fill(-1) }
    private val ENCODING_TABLE =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"".toByteArray()
    private val BASE = ENCODING_TABLE.size
    private const val AVERAGE_ENCODING_RATIO = 1.2297f
    fun encode(data: ByteArray): ByteArray {
        val estimatedSize = ceil((data.size * AVERAGE_ENCODING_RATIO).toDouble()).toInt()
        val output = ByteArrayOutputStream(estimatedSize)
        var ebq = 0
        var en = 0
        for (i in data.indices) {
            ebq = ebq or (data[i].toInt() and 255 shl en)
            en += 8
            if (en > 13) {
                var ev = ebq and 8191
                if (ev > 88) {
                    ebq = ebq shr 13
                    en -= 13
                } else {
                    ev = ebq and 16383
                    ebq = ebq shr 14
                    en -= 14
                }
                output.write(ENCODING_TABLE[ev % BASE].toInt())
                output.write(ENCODING_TABLE[ev / BASE].toInt())
            }
        }
        if (en > 0) {
            output.write(ENCODING_TABLE[ebq % BASE].toInt())
            if (en > 7 || ebq > 90) {
                output.write(ENCODING_TABLE[ebq / BASE].toInt())
            }
        }
        return output.toByteArray()
    }

    fun decode(data: ByteArray): ByteArray {

        // if (data.length == 0)
        // return new byte[] {};
        var dbq = 0
        var dn = 0
        var dv = -1
        val estimatedSize = (data.size / AVERAGE_ENCODING_RATIO).roundToInt()
        val output = ByteArrayOutputStream(estimatedSize)
        for (i in data.indices) {
            if (DECODING_TABLE[data[i].toInt()].toInt() == -1) continue
            if (dv == -1) dv = DECODING_TABLE[data[i].toInt()].toInt()
            else {
                dv += DECODING_TABLE[data[i].toInt()] * BASE
                dbq = dbq or (dv shl dn)
                dn += if (dv and 8191 > 88) 13 else 14
                do {
                    output.write(dbq)
                    dbq = dbq shr 8
                    dn -= 8
                } while (dn > 7)
                dv = -1
            }
        }
        if (dv != -1) {
            output.write(((dbq or dv shl dn).toByte()).toInt())
        }
        return output.toByteArray()
    }

    init {
        for (i in 0 until BASE) DECODING_TABLE[ENCODING_TABLE[i].toInt()] = i.toByte()
    }
}
