package me.leon.ext.crypto

import me.leon.ctf.ZWC_UNICODE_DICT

/**
 * @author Leon
 * @since 2022-10-08 15:23
 */
const val HINT = "hint"
const val CHECK = "check"
val CLASSIC_CONFIG =
    mapOf(
        ClassicalCryptoType.CAESAR to
            mapOf(
                HINT to arrayOf("shift", "shift lower(default is same to shift)"),
            ),
        ClassicalCryptoType.AFFINE to
            mapOf(
                HINT to arrayOf("factor a", "b"),
            ),
        ClassicalCryptoType.RAILFENCE to
            mapOf(
                HINT to arrayOf("fence number", "offset,default is 0 (w-type)"),
                CHECK to arrayOf("W-type")
            ),
        ClassicalCryptoType.VIRGENENE to
            mapOf(
                HINT to arrayOf("key"),
            ),
        ClassicalCryptoType.MORSE to
            mapOf(
                HINT to arrayOf("default .", "default -"),
            ),
        ClassicalCryptoType.POLYBIUS to
            mapOf(
                HINT to
                    arrayOf("table, $TABLE_A_Z_WO_J as default", "encode map, 12345 as default"),
            ),
        ClassicalCryptoType.NIHILIST to
            mapOf(
                HINT to arrayOf("keyword", "encodeMap 12345 is as default"),
            ),
        ClassicalCryptoType.ADFGX to
            mapOf(HINT to arrayOf("table $TABLE_A_Z_WO_J", "keyword"), CHECK to arrayOf("ADFGVX")),
        ClassicalCryptoType.PLAYFAIR to
            mapOf(
                HINT to arrayOf("key"),
            ),
        ClassicalCryptoType.AUTOKEY to
            mapOf(
                HINT to arrayOf("key"),
            ),
        ClassicalCryptoType.OTP to
            mapOf(
                HINT to arrayOf("key data as long as data size"),
            ),
        ClassicalCryptoType.AlphabetIndex to
            mapOf(
                HINT to
                    arrayOf(
                        "table, '$TABLE_A_Z' as default",
                        "delimiter(space as default)",
                    ),
            ),
        ClassicalCryptoType.ZWC to
            mapOf(HINT to arrayOf("show plain text(optional)"), CHECK to arrayOf("morse")),
        ClassicalCryptoType.ZWC_UNICODE to
            mapOf(
                HINT to arrayOf("show plain text", "encode dict,default $ZWC_UNICODE_DICT"),
                CHECK to arrayOf("binary")
            ),
        ClassicalCryptoType.CurveCipher to
            mapOf(
                HINT to arrayOf("row", "column"),
            ),
        ClassicalCryptoType.EmojiSubstitute to
            mapOf(
                HINT to arrayOf("shift, default 0"),
            ),
        ClassicalCryptoType.HandyCode to
            mapOf(
                HINT to arrayOf("default table '$TABLE_HANDY_CODE'"),
            ),
        ClassicalCryptoType.Porta to
            mapOf(
                HINT to arrayOf("key,PORTA as default"),
            ),
        ClassicalCryptoType.Beaufort to
            mapOf(
                HINT to arrayOf("key,beaufort as default"),
            ),
        ClassicalCryptoType.FourSquare to
            mapOf(
                HINT to arrayOf("key1, length 25 ", "key2, length 25"),
            ),
        ClassicalCryptoType.Gronsfeld to
            mapOf(
                HINT to arrayOf("key, a sequence of numbers 0-9,default 123456 "),
            ),
        ClassicalCryptoType.Trifid to
            mapOf(
                HINT to arrayOf("key,length 27 ", "period, default 5"),
            ),
        ClassicalCryptoType.Bifid to
            mapOf(
                HINT to arrayOf(" key,length 25,default A-Z(w/o J) ", "period, default 5"),
            ),
        ClassicalCryptoType.GrayCode to
            mapOf(
                HINT to
                    arrayOf(
                        "length, default is binary string length",
                        "delimiter(space as default)"
                    ),
            ),
        ClassicalCryptoType.HILL to
            mapOf(
                HINT to
                    arrayOf(
                        "key matrix,like 1 2 0 1 or bcab",
                        "A = 0 as default,if has value A =1"
                    ),
            ),
        ClassicalCryptoType.Rabbit to
            mapOf(
                HINT to arrayOf("password,default is empty string"),
            ),
        ClassicalCryptoType.JJEncode to
            mapOf(
                HINT to
                    arrayOf(
                        "global variable name, default is '$'",
                        "palindromic,false as default,if has value is true"
                    ),
            ),
        ClassicalCryptoType.CAESAR_BOX to mapOf(HINT to arrayOf("height")),
        ClassicalCryptoType.STEG_BASE64 to
            mapOf(HINT to arrayOf("show data file path(encrypt)"), CHECK to arrayOf("base32")),
        ClassicalCryptoType.FENHAM to mapOf(HINT to arrayOf("key,as long as raw data")),
        ClassicalCryptoType.FRAC_MORSE to mapOf(HINT to arrayOf("key,length must be 26")),
        ClassicalCryptoType.EIGHT_DIAGRAM to mapOf(HINT to arrayOf("delimiter(none as default)")),
        ClassicalCryptoType.MANCHESTER to mapOf(CHECK to arrayOf("standard", "reverse 8bit")),
        ClassicalCryptoType.MANCHESTER_DIFF to mapOf(CHECK to arrayOf("reverse 8bit")),
        ClassicalCryptoType.BACON24 to mapOf(CHECK to arrayOf("bacon26")),
        ClassicalCryptoType.TYPE7 to mapOf(HINT to arrayOf("seed(need in encrypt), from 0 to 52")),
        ClassicalCryptoType.BASE64_CASE to
            mapOf(HINT to arrayOf("addition words, separate by non-letter")),
        ClassicalCryptoType.BuddhaSay to
            mapOf(
                HINT to arrayOf("custom password for encrypt version, default is TakuronDotTop"),
                CHECK to arrayOf("encrypt version"),
            ),
    )
