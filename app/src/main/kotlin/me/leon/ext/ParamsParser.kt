package me.leon.ext

import java.math.BigInteger

fun String.parseRsaParams() =
    replace("\"|'", "")
        .split("\n|\r\n".toRegex())
        .also { println(it) }
        .filterNot { it.isBlank() }
        .fold(mutableMapOf<String, BigInteger>()) { acc, s ->
            acc.apply {
                with(s.split("\\s*[=:：]\\s*".toRegex())) {
                    acc[this[0]] =
                        this[1].takeUnless { it.startsWith("0x", true) }?.toBigInteger()
                            ?: this[1].substring(2).toBigInteger(16)
                }
            }
        }
