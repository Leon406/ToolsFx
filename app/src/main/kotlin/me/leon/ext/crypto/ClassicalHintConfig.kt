package me.leon.ext.crypto

/**
 *
 * @author Leon
 * @since 2022-10-08 15:23
 * @email: deadogone@gmail.com
 */
const val HINT = "hint"
const val CHECK = "check"
val CLASSIC_CONFIG =
    mapOf(
        ClassicalCryptoType.CAESAR.type to
            mapOf(
                HINT to arrayOf("shift", "shift lower(default is same to shift)"),
            ),
        ClassicalCryptoType.AFFINE.type to
            mapOf(
                HINT to arrayOf("factor a", "b"),
            ),
        ClassicalCryptoType.RAILFENCE.type to
            mapOf(
                HINT to arrayOf("fence number"),
            ),
        ClassicalCryptoType.RAILFENCEW.type to
            mapOf(
                HINT to arrayOf("fence number", "offset,default is 0"),
            ),
        ClassicalCryptoType.VIRGENENE.type to
            mapOf(
                HINT to arrayOf("key"),
            ),
        ClassicalCryptoType.MORSE.type to
            mapOf(
                HINT to arrayOf("default .", "default -"),
            ),
        ClassicalCryptoType.POLYBIUS.type to
            mapOf(
                HINT to
                    arrayOf("table, $TABLE_A_Z_WO_J as default", "encode map, 12345 as default"),
            ),
        ClassicalCryptoType.NIHILIST.type to
            mapOf(
                HINT to arrayOf("keyword", "encodeMap 12345 is as default"),
            ),
        ClassicalCryptoType.ADFGX.type to
            mapOf(
                HINT to arrayOf("table $TABLE_A_Z_WO_J", "keyword"),
            ),
        ClassicalCryptoType.ADFGVX.type to
            mapOf(
                HINT to arrayOf("table $TABLE_A_Z_WO_J", "keyword"),
            ),
        ClassicalCryptoType.PLAYFAIR.type to
            mapOf(
                HINT to arrayOf("key"),
            ),
        ClassicalCryptoType.AUTOKEY.type to
            mapOf(
                HINT to arrayOf("key"),
            ),
        ClassicalCryptoType.OTP.type to
            mapOf(
                HINT to arrayOf("key data as long as data size"),
            ),
        ClassicalCryptoType.AlphabetIndex.type to
            mapOf(
                HINT to
                    arrayOf(
                        "table, '$TABLE_A_Z' as default",
                        "delimiter(space as default)",
                    ),
            ),
        ClassicalCryptoType.ZWC.type to
            mapOf(
                HINT to arrayOf("show plain text"),
            ),
        ClassicalCryptoType.ZWC_MORSE.type to
            mapOf(
                HINT to arrayOf("show plain text"),
            ),
        ClassicalCryptoType.CurveCipher.type to
            mapOf(
                HINT to arrayOf("row", "column"),
            ),
        ClassicalCryptoType.EmojiSubstitute.type to
            mapOf(
                HINT to arrayOf("shift, default 0"),
            ),
        ClassicalCryptoType.HandyCode.type to
            mapOf(
                HINT to arrayOf("default table '$TABLE_HANDY_CODE'"),
            ),
        ClassicalCryptoType.Porta.type to
            mapOf(
                HINT to arrayOf("key,PORTA as default"),
            ),
        ClassicalCryptoType.Beaufort.type to
            mapOf(
                HINT to arrayOf("key,beaufort as default"),
            ),
        ClassicalCryptoType.FourSquare.type to
            mapOf(
                HINT to arrayOf("key1, length 25 ", "key2, length 25"),
            ),
        ClassicalCryptoType.Gronsfeld.type to
            mapOf(
                HINT to arrayOf("key, a sequence of numbers 0-9,default 123456 "),
            ),
        ClassicalCryptoType.Trifid.type to
            mapOf(
                HINT to arrayOf("key,length 27 ", "period, default 5"),
            ),
        ClassicalCryptoType.Bifid.type to
            mapOf(
                HINT to arrayOf(" key,length 25,default A-Z(w/o J) ", "period, default 5"),
            ),
        ClassicalCryptoType.GrayCode.type to
            mapOf(
                HINT to
                    arrayOf(
                        "length, default is binary string length",
                        "delimiter(space as default)"
                    ),
            ),
        ClassicalCryptoType.HILL.type to
            mapOf(
                HINT to
                    arrayOf(
                        "key matrix,like 1 2 0 1 or bcab",
                        "A = 0 as default,if has value A =1"
                    ),
            ),
        ClassicalCryptoType.Rabbit.type to
            mapOf(
                HINT to arrayOf("password,default is empty string"),
            ),
        ClassicalCryptoType.JJEncode.type to
            mapOf(
                HINT to
                    arrayOf(
                        "global variable name, default is '$'",
                        "palindromic,false as default,if has value is true"
                    ),
            ),
        ClassicalCryptoType.CAESAR_BOX.type to mapOf(HINT to arrayOf("height")),
        ClassicalCryptoType.FENHAM.type to mapOf(HINT to arrayOf("key,as long as raw data")),
        ClassicalCryptoType.FRAC_MORSE.type to mapOf(HINT to arrayOf("key,length must be 26")),
        ClassicalCryptoType.EIGHT_DIAGRAM.type to
            mapOf(HINT to arrayOf("delimiter(none as default)")),
        ClassicalCryptoType.MANCHESTER.type to mapOf(CHECK to arrayOf("standard", "reverse 8bit")),
        ClassicalCryptoType.MANCHESTER_DIFF.type to mapOf(CHECK to arrayOf("reverse 8bit"))
    )
