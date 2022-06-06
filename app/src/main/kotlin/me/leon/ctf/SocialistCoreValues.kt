package me.leon.ctf

import kotlin.random.Random
import me.leon.encode.base.BASE16_DICT
import me.leon.ext.hex2String
import me.leon.ext.toHex

const val SOCIALISM = "富强民主文明和谐自由平等公正法治爱国敬业诚信友善"

fun String.socialistCoreValues() =
    toHex()
        .uppercase()
        .map {
            with(BASE16_DICT.indexOf(it)) {
                when (this) {
                    in 0..9 -> {
                        SOCIALISM[2 * this] + SOCIALISM[2 * this + 1].toString()
                    }
                    else -> {
                        if (Random.nextBoolean()) {
                            SOCIALISM[2 * 10] +
                                SOCIALISM[2 * 10 + 1].toString() +
                                SOCIALISM[2 * (this - 10)] +
                                SOCIALISM[2 * (this - 10) + 1].toString()
                        } else {
                            SOCIALISM[2 * 11] +
                                SOCIALISM[2 * 11 + 1].toString() +
                                SOCIALISM[2 * (this - 6)] +
                                SOCIALISM[2 * (this - 6) + 1].toString()
                        }
                    }
                }
            }
        }
        .joinToString("")

fun String.socialistCoreValuesDecrypt(): String {
    var tmp = 0
    return chunked(2)
        .map { SOCIALISM.indexOf(it) / 2 }
        .fold(StringBuilder()) { acc, i ->
            acc.apply {
                tmp =
                    if (i < 10) {
                        when (tmp) {
                            10 -> append(BASE16_DICT[i + 10])
                            11 -> append(BASE16_DICT[i + 6])
                            else -> append(i)
                        }
                        0
                    } else {
                        i
                    }
            }
        }
        .toString()
        .hex2String()
}
