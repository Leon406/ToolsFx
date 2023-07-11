package me.leon.encode.base

import kotlin.math.floor
import me.leon.ext.math.toBigInteger
import me.leon.ext.toUnicodeChar

/**
 * ported from https://github.com/qntm/base32768/blob/main/src/index.js
 *
 * @author Leon
 * @since 2022-10-31 15:10
 */
private const val BITS_PER_CHAR = 15
private const val BITS_PER_BYTE = 8
private val lookupE = mutableMapOf<Int, List<String>>()
private val lookupD = mutableMapOf<String, List<Int>>()
@SuppressWarnings("UnusedPrivateMember")
private val points =
    arrayOf(
            "ҠҿԀԟڀڿݠޟ߀ߟကဟႠႿᄀᅟᆀᆟᇠሿበቿዠዿጠጿᎠᏟᐠᙟᚠᛟកសᠠᡟᣀᣟᦀᦟ᧠᧿ᨠᨿᯀᯟᰀᰟᴀᴟ⇠⇿⋀⋟⍀⏟␀␟─❟➀➿⠀⥿⦠⦿⨠⩟⪀⪿⫠⭟ⰀⰟⲀⳟⴀⴟⵀⵟ⺠⻟㇀㇟㐀䶟䷀龿ꀀꑿ꒠꒿ꔀꗿꙀꙟꚠꛟ꜀ꝟꞀꞟꡀꡟ",
            "ƀƟɀʟ"
        )
        .also {
            it.mapIndexed { index, s ->
                val encodeRepertoire = mutableListOf<String>()
                s.toByteArray(Charsets.UTF_32BE).asIterable().chunked(8).map {
                    val start = it.take(4).toByteArray().toBigInteger().toInt()
                    val end = it.takeLast(4).toByteArray().toBigInteger().toInt()
                    for (i in start..end) {
                        encodeRepertoire.add(i.toUnicodeChar())
                    }
                }
                val numZBits = BITS_PER_CHAR - BITS_PER_BYTE * index
                lookupE[numZBits] = encodeRepertoire
                encodeRepertoire.forEachIndexed { i, char -> lookupD[char] = listOf(numZBits, i) }
            }
        }

fun String.base32768(): String = toByteArray().base32768()

fun ByteArray.base32768(): String {
    val sb = StringBuilder()
    var z = 0
    var numZBits = 0

    for (i in indices) {
        for (j in (BITS_PER_BYTE - 1) downTo 0) {
            val bit = this[i].toInt() shr j and 1
            z = (z shl 1) + bit
            numZBits++
            if (numZBits == BITS_PER_CHAR) {
                sb.append(lookupE[numZBits]!![z])
                z = 0
                numZBits = 0
            }
        }
    }
    if (numZBits != 0) {
        while (!lookupE.contains(numZBits)) {
            z = (z shl 1) + 1
            numZBits++
        }
        sb.append(lookupE[numZBits]!![z])
    }

    return sb.toString()
}

fun ByteArray.base32768Decode(): ByteArray = decodeToString().base32768Decode()

fun String.base32768Decode(): ByteArray {
    var numUint8s = 0
    var uint8 = 0
    var numUint8Bits = 0

    val uint8Array = ByteArray(floor(length * BITS_PER_CHAR.toDouble() / BITS_PER_BYTE).toInt())
    for (c in this) {
        val (numZBits, z) = lookupD[c.toString()]!!
        for (j in (numZBits - 1) downTo 0) {
            val bit = z shr j and 1
            uint8 = (uint8 shl 1) + bit
            numUint8Bits++
            if (numUint8Bits == BITS_PER_BYTE) {
                uint8Array[numUint8s] = uint8.toByte()
                numUint8s++
                uint8 = 0
                numUint8Bits = 0
            }
        }
    }
    return uint8Array.take(numUint8s).toByteArray()
}
