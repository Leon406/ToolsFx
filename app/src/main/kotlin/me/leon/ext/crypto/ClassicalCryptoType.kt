package me.leon.ext.crypto

import me.leon.*
import me.leon.classical.*
import me.leon.ctf.*
import me.leon.ctf.rsa.RsaSolver
import me.leon.encode.base.BASE32_DICT
import me.leon.encode.base.BASE64_DICT
import me.leon.ext.*

enum class ClassicalCryptoType(val type: String) : IClassical {
    CAESAR("caesar") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.shift26(
                requireNotNull(params[P1]).toInt(),
                requireNotNull(params[P2]).ifEmpty { params[P1] }!!.toInt()
            )

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.shift26(
                26 - params[P1]!!.toInt(),
                26 - params[P2]!!.ifEmpty { params[P1] }!!.toInt()
            )

        override fun isIgnoreSpace() = false

        override fun hasCrack() = true

        override fun crack(raw: String, keyword: String): String {
            val lowerCount = raw.count { it.isLowerCase() }
            val upperCount = raw.count { it.isUpperCase() }
            val isOneCase = lowerCount == 0 || upperCount == 0
            val sb = StringBuilder()
            for (i in (1..25)) {
                for (j in (1..25)) {
                    if (isOneCase && j > 1) break
                    val biasLower = j.takeUnless { isOneCase } ?: i
                    val decrypted = raw.shift26(26 - i, 26 - biasLower)
                    if (decrypted.containsRegexIgnoreCase(keyword)) {
                        sb.append(
                                "shift: $i shift(lower): $biasLower${System.lineSeparator()}\t$decrypted"
                            )
                            .appendLine()
                    }
                }
            }
            return sb.toString()
        }
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

        override fun isIgnoreSpace() = false

        override fun hasCrack() = true

        override fun crack(raw: String, keyword: String): String {
            val sb = StringBuilder()
            for (a in 1..26) for (b in 1..26) {
                runCatching {
                    val decrypted = raw.affineDecrypt(a, b)
                    if (decrypted.containsRegexIgnoreCase(keyword)) {
                        sb.append("$a*x+$b: ${System.lineSeparator()}\t$decrypted").appendLine()
                    }
                }
            }
            return sb.toString()
        }
    },
    RAILFENCE("railFence") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.railFenceWEncrypt(params[P1]!!.toInt(), params[P2]?.toIntOrNull() ?: 0)
            } else {
                raw.railFenceEncrypt(params[P1]!!.toInt())
            }

        override fun decrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.railFenceWDecrypt(params[P1]!!.toInt(), params[P2]?.toIntOrNull() ?: 0)
            } else {
                raw.railFenceDecrypt(params[P1]!!.toInt())
            }

        override fun hasCrack() = true

        override fun crack(raw: String, keyword: String, params: Map<String, String>): String {
            val sb = StringBuilder()
            val func = { s: String, i: Int ->
                if (requireNotNull(params[C1]).toBoolean()) {
                    s.railFenceWDecrypt(i)
                } else {
                    s.railFenceDecrypt(i)
                }
            }
            for (i in 2 until raw.length) {
                runCatching {
                    val decrypted = func(raw, i)
                    if (decrypted.containsRegexIgnoreCase(keyword)) {
                        sb.append("railFence $i: ${System.lineSeparator()}\t$decrypted")
                            .appendLine()
                    }
                }
            }
            return sb.toString()
        }
    },
    VIRGENENE("virgenene") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.virgeneneEncode(params[P1]!!)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.virgeneneDecode(params[P1]!!)

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

        override fun isIgnoreSpace() = false
    },
    ADFGX("ADFGX") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.adfgvx(
                    params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                    params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
                )
            } else {
                raw.adfgx(
                    params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                    params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
                )
            }

        override fun decrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.adfgvxDecrypt(
                    params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                    params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
                )
            } else {
                raw.adfgxDecrypt(
                    params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J,
                    params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
                )
            }
    },
    PLAYFAIR("playFair") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.playFair(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.playFairDecrypt(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J)
    },
    AUTOKEY("autoKey") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.autoKey(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.autoKeyDecrypt(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_A_Z_WO_J)

        override fun isIgnoreSpace() = false
    },
    BACON24("bacon24") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.baconEncrypt26()
            } else {
                raw.baconEncrypt24()
            }

        override fun decrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.baconDecrypt26()
            } else {
                raw.baconDecrypt24()
            }

        override fun isIgnoreSpace() = false
    },
    OTP("oneTimePad") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.oneTimePad(params[P1]!!)

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.oneTimePadDecrypt(params[P1]!!)
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
    Braille("braille(盲文)") {
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
    ZWC("zwBinary") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.zwcMorse(params[P1]?.ifEmpty { "show" } ?: "show")
            } else {
                raw.zwcBinary(params[P1]?.ifEmpty { "show" } ?: "show")
            }

        override fun decrypt(raw: String, params: Map<String, String>): String = buildString {
            append(raw.filterNot { it in zeroWidthDict })
            appendLine()
            append(
                if (requireNotNull(params[C1]).toBoolean()) {
                    raw.zwcMorseDecode()
                } else {
                    raw.zwcBinaryDecode()
                }
            )
        }
    },
    ZWC_UNICODE("zwUnicode") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.zwcUnicodeBinary(
                    params[P1]?.ifEmpty { "show" } ?: "show",
                    params[P2]?.ifEmpty { ZWC_UNICODE_DICT } ?: ZWC_UNICODE_DICT
                )
            } else {
                raw.zwcUnicode(
                    params[P1]?.ifEmpty { "show" } ?: "show",
                    params[P2]?.ifEmpty { ZWC_UNICODE_DICT } ?: ZWC_UNICODE_DICT
                )
            }

        override fun decrypt(raw: String, params: Map<String, String>): String = buildString {
            append(raw.filterNot { it in zeroWidthDict })
            appendLine()
            append(
                if (requireNotNull(params[C1]).toBoolean()) {
                    raw.zwcUnicodeDecodeBinary(
                        params[P2]?.ifEmpty { ZWC_UNICODE_DICT } ?: ZWC_UNICODE_DICT
                    )
                } else {
                    raw.zwcUnicodeDecode(
                        params[P2]?.ifEmpty { ZWC_UNICODE_DICT } ?: ZWC_UNICODE_DICT
                    )
                }
            )
        }
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
    },
    EmojiSubstitute("emojiSubstitute") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.emojiReplace(params[P1].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 0)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.emojiReplaceDecode(params[P1].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 0)
    },
    HandyCode("handyCode") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.tableEncode(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_HANDY_CODE)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.tableDecode(params[P1].takeUnless { it.isNullOrEmpty() } ?: TABLE_HANDY_CODE)

        override fun isIgnoreSpace() = false
    },
    Vowel("vowel") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.tableEncode(TABLE_VOWEL)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.tableDecode(TABLE_VOWEL).lowercase()

        override fun isIgnoreSpace() = false
    },
    Porta("porta") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.porta(params[P1].takeUnless { it.isNullOrEmpty() } ?: "porta")

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.porta(params[P1].takeUnless { it.isNullOrEmpty() } ?: "porta")

        override fun isIgnoreSpace() = false
    },
    Beaufort("beaufort") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.beaufort(params[P1].takeUnless { it.isNullOrEmpty() } ?: "beaufort")

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.beaufort(params[P1].takeUnless { it.isNullOrEmpty() } ?: "beaufort")

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

        override fun isIgnoreSpace() = false
    },
    Gronsfeld("gronsfeld") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.gronsfeld(params[P1].takeUnless { it.isNullOrEmpty() } ?: "123456")

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.gronsfeldDecrypt(params[P1].takeUnless { it.isNullOrEmpty() } ?: "123456")

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
    },
    Bifid("bifid") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.bifid(
                params[P1]!!.ifEmpty { TABLE_A_Z_WO_J },
                params[P2].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 5
            )

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.bifidDecrypt(
                params[P1]!!.ifEmpty { TABLE_A_Z_WO_J },
                params[P2].takeUnless { it.isNullOrEmpty() }?.toInt() ?: 5
            )
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

        override fun isIgnoreSpace() = false
    },
    BuddhaSay("与佛论禅") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.buddhaPbe(requireNotNull(params[P1]))
            } else {
                raw.buddhaSays()
            }

        override fun decrypt(raw: String, params: Map<String, String>): String =
            if (requireNotNull(params[C1]).toBoolean()) {
                raw.buddhaPbeDecrypt(requireNotNull(params[P1]))
            } else {
                raw.buddhaExplain()
            }
    },
    BuddhaSay2("新佛曰(online)") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            PcMoeOnlineCipher.encrypt(PcMoeOnlineCipher.Buddha, raw)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            PcMoeOnlineCipher.decrypt(PcMoeOnlineCipher.Buddha, raw)
    },
    Roar("兽音(online)") {
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
            raw.hillEncrypt(params[P1].orEmpty(), fromZero = params[P2]?.isEmpty() ?: true)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.hillDecrypt(params[P1].orEmpty(), fromZero = params[P2]?.isEmpty() ?: true)

        override fun hasCrack() = true

        override fun crack(raw: String, keyword: String): String {
            val range = 0..25
            val results = mutableListOf<String>()
            for (k1 in range) {
                for (k2 in range) for (k3 in range) for (k4 in range) {
                    runCatching {
                        val decrypted = raw.hillDecrypt("$k1 $k2 $k3 $k4", fromZero = false)
                        if (decrypted.containsRegexIgnoreCase(keyword)) {
                            results.add(
                                "hill $k1 $k2 $k3 $k4: ${System.lineSeparator()}\t$decrypted"
                            )
                        }
                    }
                }
            }
            return results.joinToString(System.lineSeparator())
        }
    },
    Rabbit("rabbit") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            JavascriptCipher.rabbitEncrypt(raw, params[P1].orEmpty())

        override fun decrypt(raw: String, params: Map<String, String>): String =
            JavascriptCipher.rabbitDecrypt(raw, params[P1].orEmpty())
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
    },
    RSA_CRACK("RSA-crack") {
        override fun encrypt(raw: String, params: Map<String, String>) = decrypt(raw, params)

        override fun decrypt(raw: String, params: Map<String, String>): String =
            with(raw.parseRsaParams()) { RsaSolver.solve(this) }

        override fun isIgnoreSpace() = false
    },
    DNA("DNA") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.dna()

        override fun decrypt(raw: String, params: Map<String, String>): String = raw.dnaDecode()

        override fun isIgnoreSpace() = false
    },
    TAP_CODE("tapCode") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.tapCode()

        override fun decrypt(raw: String, params: Map<String, String>): String =
            raw.tapCodeDecrypt()

        override fun isIgnoreSpace() = false
    },
    CAESAR_BOX("caesar box") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.caesarBox(params[P1]!!.toInt())

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.caesarBoxDecrypt(params[P1]!!.toInt())

        override fun isIgnoreSpace() = false
    },
    ROT8000("rot8000") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.rot8000()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.rot8000()

        override fun isIgnoreSpace() = false
    },
    CETACEAN("cetacean") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.cetacean()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.cetaceanDecrypt()

        override fun isIgnoreSpace() = false
    },
    YYGQ("阴阳怪气") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.yygq()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.yygqDecode()

        override fun isIgnoreSpace() = false
    },
    MANCHESTER("manchester") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.manchester(
                requireNotNull(params[C1]).toBoolean(),
                requireNotNull(params[C2]).toBoolean()
            )

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.manchesterDecode(
                requireNotNull(params[C1]).toBoolean(),
                requireNotNull(params[C2]).toBoolean()
            )
    },
    MANCHESTER_DIFF("manchester-diff") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.manchesterDiff(requireNotNull(params[C1]).toBoolean())

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.manchesterDiffDecode(requireNotNull(params[C1]).toBoolean())
    },
    EIGHT_DIAGRAM("六十四卦") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.eightDiagram(requireNotNull(params[P1].also { println(it) }))

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.eightDiagramDecode(requireNotNull(params[P1]))
    },
    STEM_BRANCH("天干地支(base60)") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.stemBranch()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.stemBranchDecode()
    },
    FRAC_MORSE("fracMorse") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.fracMorse(requireNotNull(params[P1]))

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.fracMorseDecrypt(requireNotNull(params[P1]))
    },
    FENHAM("Fenham") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.fenham(requireNotNull(params[P1]))

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.fenhamDecrypt(requireNotNull(params[P1]))
    },
    TWIN_HEX("twin-hex") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.twinHex()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.twinHexDecrypt()
    },
    BAI_JIA_XING("百家姓") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.baiJiaXing()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.baiJiaXingDecode()
    },
    TYPE7("type7") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.type7(params[P1]?.takeIf { it.isNotEmpty() }?.toInt() ?: 0)

        override fun decrypt(raw: String, params: Map<String, String>) = raw.type7Decode()
    },
    CITRIX_CTX1("Citrix CTX1") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.citrixCtx1()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.citrixCtx1Decode()
    },
    STEG_BASE64("steg base64") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.baseStegEncrypt(
                requireNotNull(params[P1]).toFile().readText(),
                if (requireNotNull(params[C1]).toBoolean()) {
                    BASE32_DICT
                } else {
                    BASE64_DICT
                }
            )

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.baseStegDecrypt(
                if (requireNotNull(params[C1]).toBoolean()) {
                    BASE32_DICT
                } else {
                    BASE64_DICT
                }
            )

        override fun isIgnoreSpace() = false
    },
    BASE64_CASE("base64CaseCrack") {
        override fun encrypt(raw: String, params: Map<String, String>) =
            raw.lineAction2String { it.base64UpperCase() }

        override fun decrypt(raw: String, params: Map<String, String>) =
            raw.base64CaseCrack(params[P1].orEmpty())

        override fun isIgnoreSpace() = false
    },
    HACKER("hacker words") {
        override fun encrypt(raw: String, params: Map<String, String>) = raw.hackWord()

        override fun decrypt(raw: String, params: Map<String, String>) = raw.hackWordDecode()

        override fun isIgnoreSpace() = false
    };

    override fun paramsHints(): Array<out String> {
        return CLASSIC_CONFIG[this]?.get(HINT).orEmpty()
    }

    override fun checkboxHints(): Array<out String> {
        return CLASSIC_CONFIG[this]?.get(CHECK).orEmpty()
    }
}
