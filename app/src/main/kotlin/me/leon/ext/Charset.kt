package me.leon.ext

import java.nio.charset.Charset

val GBK = Charset.forName("GBK")
val BIG5 = Charset.forName("Big5")
val SHIFT_JIS = Charset.forName("Shift_Jis")

val charsets = arrayOf(Charsets.UTF_8, Charsets.ISO_8859_1, SHIFT_JIS, BIG5, GBK)

fun String.recoverEncoding() =
    charsets
        .flatMap { src ->
            charsets.filterNot { it == src }.map { "$src-->$it" to toByteArray(src).toString(it) }
        }
        .filterNot {
            it.second.contains(
                ("\\?\\?|�|\u0080|\u0081|\u0082|\u0083|\u0084|\u0085" +
                        "|\u0086|\u0087|\u0088|\u0089" +
                        "|\u008A|\u008B|\u008C|\u008D|\u008E" +
                        "|\u008F|\u0090|\u0091|\u0092" +
                        "|\u0093|\u0094|\u0095|\u0096|\u0097" +
                        "|\u0098|\u0099|\u009A|\u009B|\u009C" +
                        "|\u009D|\u009E|\u009F| |¡|¢|£|¤|¥|¦|§|¨|©|ª|«|¬|\u00AD" +
                        "|®|¯|°|±|²|³|´|µ|¶|·|¸|¹|º|»|¼|½|¾|¿|À|Á|Â|Ã|Ä|Å|Æ|Ç|È|É|Ê|Ë|Ì|Í|Î|Ï|Ð" +
                        "|Ò|Ó|Ô|Õ|Ö|×|Ø|Ù|Ú|Û|Ü|Ý|Þ|ß|à|á|â|ã|ä|å|æ|ç|è|é|ê|ë|ì|í|î|ï|ð|ñ|ò|ó|ô" +
                        "|ソ|ぉ" +
                        "|õ|ö|÷|ø|ù|ú|û|ü|ý|þ|ÿ|Ā")
                    .toRegex()
            )
        }
        .fold(StringBuilder()) { acc, (src, dst) -> acc.append("$src: $dst \n") }
        .toString()

fun String.toCharset(): Charset = Charset.forName(this)
