package me.leon.ext.crypto

import me.leon.P1
import me.leon.P2
import me.leon.classical.*
import me.leon.ctf.*

enum class ClassicalCryptoType(val type: String) : IClassical {
    CAESAR("caesar") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.caesar25()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.caesar25()
    },
    ROT5("rot5") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.shift10(5)

        override fun decrypt(raw: String, params: Map<String, String>) = raw.shift10(5)

        override fun isIgnoreSpace() = false
    },
    ROT13("rot13") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.shift26(13)

        override fun decrypt(raw: String, params: Map<String, String>) = raw.shift26(13)

        override fun isIgnoreSpace() = false
    },
    ROT18("rot18") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.rot18()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.rot18()

        override fun isIgnoreSpace() = false
    },
    ROT47("rot47") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.shift94(47)

        override fun decrypt(raw: String, params: Map<String, String>) = raw.shift94(47)

        override fun isIgnoreSpace() = false
    },
    AFFINE("affine") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.affineEncrypt(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.affineDecrypt(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("factor a", "b")

        override fun isIgnoreSpace() = false
    },
    RAILFENCE("railFence") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.railFenceEncrypt(params[P1]!!.toInt())

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.railFenceDecrypt(params[P1]!!.toInt())

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("fence number", "")
    },
    RAILFENCEW("railFenceW") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.railFenceWEncrypt(params[P1]!!.toInt(), params[P2]?.toIntOrNull() ?: 0)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.railFenceWDecrypt(params[P1]!!.toInt(), params[P2]?.toIntOrNull() ?: 0)

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("fence number", "offset,default is 0")
    },
    VIRGENENE("virgenene") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.virgeneneEncode(params[P1]!!)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.virgeneneDecode(params[P1]!!)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key", "")

        override fun isIgnoreSpace() = false
    },
    ATBASH("atbash") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.atBash()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.atBash()

        override fun isIgnoreSpace() = false
    },
    MORSE("morse") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.morseEncrypt()
                .replace(".", params[P1].takeUnless { it.isNullOrEmpty() } ?: ".")
                .replace("-", params[P2].takeUnless { it.isNullOrEmpty() } ?: "-")

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.replace(params[P1].takeUnless { it.isNullOrEmpty() } ?: ".", ".")
                .replace(params[P2].takeUnless { it.isNullOrEmpty() } ?: "-", "-")
                .morseDecrypt()

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("default .", "default -")

        override fun isIgnoreSpace() = false
    },
    QWE("qwe") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.qweEncrypt()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.qweDecrypt()
    },
    POLYBIUS("polybius") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.polybius(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.polybiusDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun isIgnoreSpace() = false
        override fun paramsCount() = 2

        override fun paramsHints() =
            listOf("table, $TABLE_A_Z_WO_J as default", "encode map, 12345 as default")
    },
    NIHILIST("nihilist") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.nihilist(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.nihilistDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("keyword", "encodeMap 12345 is as default")

        override fun isIgnoreSpace() = false
    },
    ADFGX("ADFGX") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.adfgx(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.adfgxDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("table $TABLE_A_Z_WO_J", "keyword")
    },
    ADFGVX("ADFGVX") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.adfgvx(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.adfgvxDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("table $TABLE_A_Z_WO_J", "keyword")
    },
    PLAYFAIR("playFair") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.playFair(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.playFairDecrypt(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key", "")
    },
    AUTOKEY("autoKey") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.autoKey(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.autoKeyDecrypt(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("table $TABLE_A_Z_WO_J", "")

        override fun isIgnoreSpace() = false
    },
    BACON24("bacon24") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.baconEncrypt24()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.baconDecrypt24()

        override fun isIgnoreSpace() = false
    },
    BACON26("bacon26") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.baconEncrypt26()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.baconDecrypt26()

        override fun isIgnoreSpace() = false
    },
    OTP("oneTimePad") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.oneTimePad(params[P1]!!)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.oneTimePadDecrypt(params[P1]!!)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key data as long as data size", "")
    },
    SOCIALISM("socialistCoreValue") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.socialistCoreValues()

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.socialistCoreValuesDecrypt()
    },
    BRAINFUCK("brain fuck") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.brainFuckEncrypt()

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.brainFuckDecrypt()
    },
    Ook("Ook") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.ookEncrypt()

        override fun decrypt(raw: String, params: Map<String, String>): String = raw.ookDecrypt()

        override fun isIgnoreSpace() = false
    },
    TROLLSCRIPT("troll script") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.trollScriptEncrypt()

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.trollScriptDecrypt()
    },
    Braille("braille") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.blindEncode().also { println("Braille $raw $params") }

        override fun decrypt(raw: String, params: Map<String, String>): String = raw.blindDecode()
    },
    BauDot("baudot") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.baudot().also { println("baudot $raw $params") }

        override fun decrypt(raw: String, params: Map<String, String>): String = raw.baudotDecode()

        override fun isIgnoreSpace() = false
    },
    AlphabetIndex("a1z26") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.alphabetIndex(
                    params[P1]?.ifEmpty { TABLE_A_Z } ?: " ",
                    params[P2]?.ifEmpty { " " } ?: " "
                )
                .also { println("alphabetIndex $raw $params") }

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.alphabetIndexDecode()

        override fun paramsCount() = 2

        override fun paramsHints() =
            listOf(
                "table, '$TABLE_A_Z' as default",
                "delimiter(space as default)",
            )

        override fun isIgnoreSpace() = false
    },
    Zero1248("01248") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.zero1248().also { println("01248 $raw $params") }

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.zero1248Decode()
    },
    BubbleBabble("bubbleBabble") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.bubbleBabble()

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.bubbleBabbleDecode()

        override fun isIgnoreSpace() = false
    },
    ZWC("zeroWidthChar") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.zwc(params[P1]?.ifEmpty { "hide" } ?: "hide")

        override fun decrypt(raw: String, params: Map<String, String>): String = raw.zwcDecode()

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("show plain text", "")
    },
    PeriodicTable("periodicTable") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.elementPeriodEncode()

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.elementPeriodDecode()

        override fun isIgnoreSpace() = false
    },
    PawnShop("pawnShop") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.pawnshop()

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.pawnshopDecode()

        override fun isIgnoreSpace() = false
    },
    AsciiSum("asciiSum") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.asciiSum()

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.asciiSumDecode()

        override fun isIgnoreSpace() = false
    },
    CurveCipher("curveCipher") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.curveCipher(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.curveCipherDecode(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("row", "column")
    },
    EmojiSubstitute("emojiSubstitute") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.emojiReplace(params[P1].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 0)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.emojiReplaceDecode(params[P1].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 0)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("shift, default 0", "")
    },
    HandyCode("handyCode") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.tableEncode(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_HANDY_CODE)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.tableDecode(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_HANDY_CODE)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("default table '$TABLE_HANDY_CODE'", "")

        override fun isIgnoreSpace() = false
    },
    Porta("porta") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.porta(params[P1].takeUnless { it.isNullOrEmpty() } ?: "porta")

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.porta(params[P1].takeUnless { it.isNullOrEmpty() } ?: "porta")

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key,PORTA as default", "")

        override fun isIgnoreSpace() = false
    },
    Beaufort("beaufort") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.beaufort(params[P1].takeUnless { it.isNullOrEmpty() } ?: "beaufort")

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.beaufort(params[P1].takeUnless { it.isNullOrEmpty() } ?: "beaufort")

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key,beaufort as default", "")

        override fun isIgnoreSpace() = false
    },
    FourSquare("fourSquare") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.fourSquare(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J
            )

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.fourSquareDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("key1, length 25 ", "key2, length 25")

        override fun isIgnoreSpace() = false
    },
    Gronsfeld("gronsfeld") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.gronsfeld(params[P1].takeUnless { it.isNullOrEmpty() } ?: "123456")

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.gronsfeldDecrypt(params[P1].takeUnless { it.isNullOrEmpty() } ?: "123456")

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key, a sequence of numbers 0-9,default 123456 ", "")

        override fun isIgnoreSpace() = false
    },
    Trifid("trifid") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.trifid(params[P1]!!, params[P2].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 5)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.trifidDecrypt(
                params[P1]!!,
                params[P2].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 5
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("key,length 27 ", "period, default 5")
    },
    Bifid("bifid") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.bifid(params[P1]!!, params[P2].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 5)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.bifidDecrypt(
                params[P1]!!,
                params[P2].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 5
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf(" key,length 25 ", "period, default 5")
    },
    GrayCode("grayCode") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.grayEncode(
                params[P1]?.ifEmpty { "0" }?.toInt() ?: 0,
                params[P2]?.ifEmpty { " " } ?: " "
            )

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.grayDecode(
                params[P1]?.ifEmpty { "0" }?.toInt() ?: 0,
                params[P2]?.ifEmpty { " " } ?: " "
            )

        override fun paramsCount() = 2

        override fun paramsHints() =
            listOf("length, default is binary string length", "delimiter(space as default)")

        override fun isIgnoreSpace() = false
    },
    BuddhaSay("佛曰") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.buddhaSays()

        override fun decrypt(raw: String, params: Map<String, String>): String = raw.buddhaExplain()
    },
    BuddhaSay2("新佛曰(online)") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            PcMoeOnlineCipher.encrypt(PcMoeOnlineCipher.Buddha, raw)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            PcMoeOnlineCipher.decrypt(PcMoeOnlineCipher.Buddha, raw)
    },
    Roar("兽曰(online)") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            PcMoeOnlineCipher.encrypt(PcMoeOnlineCipher.Roar, raw)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            PcMoeOnlineCipher.decrypt(PcMoeOnlineCipher.Roar, raw)
    },
    Bear("熊曰(online)") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            PcMoeOnlineCipher.encrypt(PcMoeOnlineCipher.Bear, raw)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            PcMoeOnlineCipher.decrypt(PcMoeOnlineCipher.Bear, raw)
    },
    HILL("hill") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.hillEncrypt(params[P1] ?: "", fromZero = params[P2]?.isEmpty() ?: true)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.hillDecrypt(params[P1] ?: "", fromZero = params[P2]?.isEmpty() ?: true)

        override fun paramsCount() = 2

        override fun paramsHints() =
            listOf("key matrix,like 1 2 0 1 or bcab", "A = 0 as default,if has value A =1")
    },
    Rabbit("rabbit") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            JavascriptCipher.rabbitEncrypt(raw, params[P1] ?: "")

        override fun decrypt(raw: String, params: Map<String, String>): String =
            JavascriptCipher.rabbitDecrypt(raw, params[P1] ?: "")

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("password,default is empty string", "")
    },
    AAEncode("aaencode") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            JavascriptCipher.aaEncode(raw)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            JavascriptCipher.aaDecode(raw)

        override fun isIgnoreSpace() = false
    },
    JJEncode("jjencode") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            JavascriptCipher.jjEncode(raw, params[P1] ?: "$", params[P2]?.isNullOrEmpty() ?: true)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            JavascriptCipher.jjDecode(raw)

        override fun paramsCount() = 2

        override fun paramsHints() =
            listOf(
                "global variable name, default is '$'",
                "palindromic,false as default,if has value is true"
            )
    },
}
