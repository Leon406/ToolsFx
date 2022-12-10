package me.leon.ext

import java.math.BigInteger

fun String.parseRsaParams() =
    replace("\"|'", "")
        .split("\n|\r\n".toRegex())
        .filter { it.contains("[=:：]".toRegex()) }
        .fold(mutableMapOf<String, BigInteger>()) { acc, s ->
            acc.apply {
                with(s.split("\\s*[=:：]\\s*".toRegex())) {
                    val value = this[1].trim()
                    acc[this[0].replace("\\W".toRegex(), "").lowercase()] =
                        when {
                            value.startsWith("0x", true) -> value.substring(2).toBigInteger(16)
                            value.startsWith("0", true) -> value.substring(1).toBigInteger(8)
                            else -> value.toBigInteger()
                        }
                }
            }
        }
