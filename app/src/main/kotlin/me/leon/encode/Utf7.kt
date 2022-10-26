package me.leon.encode

import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode

/** https://github.com/kkaefer/utf7/blob/master/utf7.js */
const val UTF7_RFC3501_BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890+,"

// val SET_D = "A-Za-z0-9'\\(\\),-./:?".toRegex()
// val SET_O = "!\\\\\"#\$%&*;<=>@_'^\\{\\|}".toRegex()
// val SET_W = " \r\n\t".toRegex()
val SET_ALL = "[^A-Za-z0-9'\\(\\),-./:\\?!\\\\\"#\$%&*;<=>@_'^\\{\\|} \r\n\t]+".toRegex()
val DECODE = "\\+([A-Za-z0-9/+]*)-".toRegex()
val DECODE_EXT = "&([^-]*)-".toRegex()
val SET_EXT = "[^\\x20-\\x7e]+".toRegex()

/** RFC 2152 */
fun String.utf7(isAll: Boolean = false) =
    if (isAll) {
        "+${toByteArray(Charsets.UTF_16BE).base64(needPadding = false)}-"
    } else {
        StringBuilder(this).replace(SET_ALL) {
            val base64 =
                if (it.value == "+") {
                    ""
                } else {
                    it.value.toByteArray(Charsets.UTF_16BE).base64(needPadding = false)
                }
            "+$base64-"
        }
    }

/** RFC 3501 */
fun String.utf7Ext() =
    StringBuilder(this.replace("&", "&-")).replace(SET_EXT) {
        val base64 =
            if (it.value == "&") {
                ""
            } else {
                it.value.toByteArray(Charsets.UTF_16BE).base64(UTF7_RFC3501_BASE64, false)
            }
        "&$base64-"
    }

/** RFC 2152 */
fun String.uft7Decode() =
    StringBuilder(this).replace(DECODE) {
        val s = it.groupValues[1]
        if (s == "") {
            "+"
        } else {
            s.base64Decode().toString(Charsets.UTF_16BE)
        }
    }

/** RFC 3501 */
fun String.uft7ExtDecode() =
    StringBuilder(this).replace(DECODE_EXT) {
        val s = it.groupValues[1]
        println(s)
        if (s == "") {
            "&"
        } else {
            s.replace("/", ",").base64Decode(UTF7_RFC3501_BASE64).toString(Charsets.UTF_16BE)
        }
    }
