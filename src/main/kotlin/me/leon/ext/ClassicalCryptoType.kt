package me.leon.ext

import me.leon.*
import me.leon.classical.*

enum class ClassicalCryptoType(val type: String) {
    CAESAR("caesar") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) = raw.caesar25()

        override fun decrypt(raw: String, prams: MutableMap<String, String>) = raw.caesar25()
    },
    ROT5("rot5") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) = raw.shift10(5)

        override fun decrypt(raw: String, prams: MutableMap<String, String>) = raw.shift10(5)
    },
    ROT13("rot13") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) = raw.shift26(13)

        override fun decrypt(raw: String, prams: MutableMap<String, String>) = raw.shift26(13)
    },
    ROT18("rot18") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) = raw.rot18()

        override fun decrypt(raw: String, prams: MutableMap<String, String>) = raw.rot18()
    },
    ROT47("rot47") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) = raw.shift94(47)

        override fun decrypt(raw: String, prams: MutableMap<String, String>) = raw.shift94(47)
    },
    AFFINE("affine") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) =
            raw.affineEncrypt(prams[P1]!!.toInt(), prams[P2]!!.toInt())

        override fun decrypt(raw: String, prams: MutableMap<String, String>) =
            raw.affineDecrypt(prams[P1]!!.toInt(), prams[P2]!!.toInt())
    },
    VIRGENENE("virgenene") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) =
            raw.virgeneneEncode(prams[P1]!!)

        override fun decrypt(raw: String, prams: MutableMap<String, String>) =
            raw.virgeneneDecode(prams[P1]!!)
    },
    ATBASH("atbash") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) = raw.atBash()

        override fun decrypt(raw: String, prams: MutableMap<String, String>) = raw.atBash()
    },
    Morse("morse") {
        override fun encrypt(raw: String, prams: MutableMap<String, String>) = raw.morseEncrypt()

        override fun decrypt(raw: String, prams: MutableMap<String, String>) = raw.morseDecrypt()
    };

    abstract fun encrypt(raw: String, prams: MutableMap<String, String>): String
    abstract fun decrypt(raw: String, prams: MutableMap<String, String>): String
}
