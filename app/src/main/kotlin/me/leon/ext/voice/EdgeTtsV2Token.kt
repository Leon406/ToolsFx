package me.leon.ext.voice

import java.nio.ByteOrder
import java.security.MessageDigest
import java.nio.ByteBuffer

object EdgeTtsV2Token {
    private const val WIN_EPOCH = 11644473600L
    private const val S_TO_NS = 1000000000
    private const val TRUSTED_CLIENT_TOKEN = "6A5AA1D4EAFF4E9FB37E23D68491D6F4"
    private const val DEFAULT_CHROMIUM_FULL_VERSION = "143.0.3650.75"

    private val PATENT_HASH_PREFIX =
        "Copyright (C) Microsoft Corporation. All rights reserved.".toByteArray(Charsets.US_ASCII)

    private const val U32_MASK = 0xFFFFFFFFL
    private const val PATCH_MARKER = "_hass_edge_tts_sec_ms_gec_v2"

    private fun u32(value: Long): Long = value and U32_MASK

    private fun rol16(value: Long): Long {
        val v = u32(value)
        return ((v shl 16) or (v shr 16)) and U32_MASK
    }

    private fun wordsLe4Even(data: ByteArray): List<Long> {
        val padLen = 4 - (data.size % 4)
        val padded = data + ByteArray(padLen)
        if (padded.isEmpty()) return emptyList()

        val buf = ByteBuffer.wrap(padded).order(ByteOrder.LITTLE_ENDIAN)
        val words = mutableListOf<Long>()
        while (buf.hasRemaining()) {
            words.add(u32(buf.getInt().toLong()))
        }
        if (words.size % 2 != 0) {
            words.add(0L)
        }
        return words
    }

    private fun cs64WordSwap(words: List<Long>, keyWords: List<Long>): Pair<Long, Long> {
        require(words.size >= 2 && words.size % 2 == 0 && keyWords.size >= 2) {
            "CS64 requires nonempty even-sized data and 2 key words"
        }
        val a = u32(keyWords[0] or 1L)
        val b = u32(keyWords[1] or 1L)
        var acc = 0L
        var last = 0L

        for (i in words.indices step 2) {
            val x = words[i]
            val y = words[i + 1]

            var t = u32(rol16(x) * 0xEF0569FB + x * a)
            t = rol16(t)
            var q = u32(t * 0x76F50F34)
            q = u32(rol16(q) * 0xC3EFEA97 + q)
            acc = u32(acc + q)

            t = u32(rol16(y) * 0xC31713DB + y * b)
            t = rol16(t)
            q = u32(t * 0x1881CE3C)
            last = u32(rol16(q) * 0x35BD1EC9 + q)
            acc = u32(acc + last)
        }
        return Pair(last, acc)
    }

    private fun cs64Reversible(words: List<Long>, keyWords: List<Long>): Pair<Long, Long> {
        require(words.size >= 2 && words.size % 2 == 0 && keyWords.size >= 2) {
            "CS64 requires nonempty even-sized data and 2 key words"
        }
        val a = u32(keyWords[0] or 1L)
        val b = u32(keyWords[1] or 1L)
        var state = 0L
        var acc = 0L

        val consts = listOf(0xCF98B111L, 0x87085B9FL, 0x12CEB96DL, 0x257E1D83L)
        for (i in words.indices step 2) {
            val x = words[i]
            val y = words[i + 1]

            state = u32((state + x) * a)
            for (c in consts) {
                state = u32(rol16(state) * c)
            }
            var t = u32((y + state) * b)
            t = u32(rol16(t) * 0xA27416F5)
            t = u32(rol16(t) * 0xD38396FF)
            t = rol16(t)
            acc = u32(acc + state)
            state = u32(rol16(u32(t * 0x7C932B89)) * 0xBFA49F69)
            acc = u32(acc + state)
        }
        return Pair(state, acc)
    }

    private fun patentHash(data: ByteArray): Pair<String, String> {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val digest = sha256.digest(data)
        val dataWords = wordsLe4Even(data)
        val digestWords = wordsLe4Even(digest)

        val a = cs64WordSwap(dataWords, digestWords)
        val b = cs64Reversible(dataWords, digestWords)

        val w0 = u32(a.first xor b.first)
        val w1 = u32(a.second xor b.second)

        val buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
        buf.putInt(w0.toInt())
        buf.putInt(w1.toInt())
        val cs64Hex = buf.array().joinToString("") { "%02X".format(it) }
        val shaHex = digest.joinToString("") { "%02X".format(it) }
        return Pair(cs64Hex, shaHex)
    }

    fun generateSecMsGecV2(unixTimestamp: Long): String {
        var ticks = unixTimestamp
        ticks += WIN_EPOCH
        ticks -= ticks % 300
        ticks *= S_TO_NS / 100

        val strToHash =
            ticks.toString().toByteArray(Charsets.US_ASCII) + TRUSTED_CLIENT_TOKEN.toByteArray(Charsets.US_ASCII)
        val payload = PATENT_HASH_PREFIX + strToHash
        val (cs64, sha256) = patentHash(payload)
        return "$cs64-$sha256"
    }
}
