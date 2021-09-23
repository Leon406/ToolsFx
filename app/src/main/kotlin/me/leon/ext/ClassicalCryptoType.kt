package me.leon.ext

import me.leon.P1
import me.leon.P2
import me.leon.classical.DEFAULT_POLYBIUS_ENCODE_MAP
import me.leon.classical.DEFAULT_POLYBIUS_TABLE
import me.leon.classical.affineDecrypt
import me.leon.classical.affineEncrypt
import me.leon.classical.atBash
import me.leon.classical.baconDecrypt24
import me.leon.classical.baconDecrypt26
import me.leon.classical.baconEncrypt24
import me.leon.classical.baconEncrypt26
import me.leon.classical.caesar25
import me.leon.classical.morseDecrypt
import me.leon.classical.morseEncrypt
import me.leon.classical.oneTimePad
import me.leon.classical.oneTimePadDecrypt
import me.leon.classical.polybius
import me.leon.classical.polybiusDecrypt
import me.leon.classical.qweDecrypt
import me.leon.classical.qweEncrypt
import me.leon.classical.railFenceDecrypt
import me.leon.classical.railFenceEncrypt
import me.leon.classical.rot18
import me.leon.classical.shift10
import me.leon.classical.shift26
import me.leon.classical.shift94
import me.leon.classical.virgeneneDecode
import me.leon.classical.virgeneneEncode
import me.leon.ctf.brainFuckDecrypt
import me.leon.ctf.brainFuckEncrypt
import me.leon.ctf.ookDecrypt
import me.leon.ctf.ookEncrypt
import me.leon.ctf.socialistCoreValues
import me.leon.ctf.socialistCoreValuesDecrypt
import me.leon.ctf.trollScriptDecrypt
import me.leon.ctf.trollScriptEncrypt

enum class ClassicalCryptoType(val type: String) {
    CAESAR("caesar") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.caesar25()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.caesar25()
    },
    ROT5("rot5") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.shift10(5)

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.shift10(5)
    },
    ROT13("rot13") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.shift26(13)

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.shift26(13)
    },
    ROT18("rot18") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.rot18()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.rot18()
    },
    ROT47("rot47") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.shift94(47)

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.shift94(47)
    },
    AFFINE("affine") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.affineEncrypt(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.affineDecrypt(params[P1]!!.toInt(), params[P2]!!.toInt())
    },
    RAILFENCE("railFence") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.railFenceEncrypt(params[P1]!!.toInt())

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.railFenceDecrypt(params[P1]!!.toInt())
    },
    VIRGENENE("virgenene") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.virgeneneEncode(params[P1]!!)

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.virgeneneDecode(params[P1]!!)
    },
    ATBASH("atbash") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.atBash()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.atBash()
    },
    MORSE("morse") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.morseEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.morseDecrypt()
    },
    QWE("qwe") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.qweEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.qweDecrypt()
    },
    POLYBIUS("polybius") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.polybius(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.polybiusDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )
    },
    BACON24("bacon24") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.baconEncrypt24()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.baconDecrypt24()
    },
    BACON26("bacon26") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.baconEncrypt26()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.baconDecrypt26()
    },
    OTP("oneTimePad") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.oneTimePad(params[P1]!!)

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.oneTimePadDecrypt(params[P1]!!)
    },
    SOCIALISM("SocialistCoreValue") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.socialistCoreValues()

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.socialistCoreValuesDecrypt()
    },
    BRAINFUCK("brain fuck") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.brainFuckEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.brainFuckDecrypt() ?: ""
    },
    Ook("Ook") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.ookEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.ookDecrypt() ?: ""
    },
    TROLLSCRIPT("troll script") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.trollScriptEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.trollScriptDecrypt() ?: ""
    };

    abstract fun encrypt(raw: String, params: MutableMap<String, String>): String
    abstract fun decrypt(raw: String, params: MutableMap<String, String>): String
}
