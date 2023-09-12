package me.leon.controller

import me.leon.config.DICT_DIR
import me.leon.ext.*
import me.leon.ext.crypto.*
import me.leon.fileHash
import me.leon.hash.*
import me.leon.hash2String
import tornadofx.*

class DigestController : Controller() {
    private val dicts
        get() =
            DICT_DIR.toFile()
                .listFiles()
                ?.filter { it.name.endsWith(".txt") }
                ?.flatMap { it.readLines() }
                ?.distinct()
                .orEmpty()

    fun digest(
        method: String,
        data: String,
        inputEncode: String = "raw",
        singleLine: Boolean = false
    ) =
        catch({ "digest error: $it" }) {
            if (singleLine) {
                data.lineAction2String { digest(method, it, inputEncode) }
            } else {
                digest(method, data, inputEncode)
            }
        }

    private fun digest(method: String, data: String, inputEncode: String) =
        if (method.startsWith("CRC")) {
            CRC_MAPPING[method.substringAfter("CRC")]!!.crc()
                .digest(data.decodeToByteArray(inputEncode))
        } else if (method == "Adler32") {
            data.decodeToByteArray(inputEncode).adler32()
        } else if (method == "NTLM") {
            data.decodeToByteArray(inputEncode).ntlmHash()
        } else if (method == "MD5_MIDDLE") {
            data.decodeToByteArray(inputEncode).hash2String().substring(8, 24)
        } else if (method == "LM") {
            data.decodeToByteArray(inputEncode).lmHash()
        } else if (method.passwordHashingType() != null) {
            method.passwordHashingType()!!.hash(data.decodeToByteArray(inputEncode))
        } else {
            data.decodeToByteArray(inputEncode).hash2String(method)
        }

    fun passwordHashingCrack(method: String, hashed: String) =
        catch({ "digest crack error: $it" }) {
            require(method.passwordHashingType()!!.hash(byteArrayOf()).length == hashed.length) {
                "Wrong Method!!! "
            }
            dicts.find { pw -> method.passwordHashingType()!!.check(pw, hashed) }.orEmpty()
        }

    fun digestFile(method: String, path: String): String =
        catch({ "digest file error: $it" }) {
            if (path.isEmpty()) {
                ""
            } else if (method.startsWith("CRC")) {
                path.toFile().crc(CRC_MAPPING[method.substringAfter("CRC")]!!)
            } else if (method == "Adler32") {
                path.adler32File()
            } else if (method.passwordHashingType() != null) {
                kotlin.error("not support")
            } else if (method == "MD5_MIDDLE") {
                path.fileHash().substring(8, 24)
            } else {
                path.fileHash(method)
            }
        }

    // 首次加载1400W, 8s,  100w md5 1s  21c40fc4ddd462df2509b232fef4ec6c
    // 1400w 单线程 14s md5  dd2978f9ae7014cd2d1884c5a1bbbca2
    // 1400w parallelStream 3s-6s md5  dd2978f9ae7014cd2d1884c5a1bbbca2
    fun crack(method: String, data: String): String =
        catch({ "digest crack error: $it" }) {
            val lower = data.lowercase()
            require(digest(method, "", "raw").length == data.length) { "Wrong Method!!! " }
            println("crack $method $data")
            dicts.findParallel("") { digest(method, it, "raw") == lower }
        }

    fun maskCrack(method: String, hashed: String, mask: String, dict: String): String =
        catch({ "digest crack error: $it" }) {
            val lower = hashed.lowercase()
            require(digest(method, "", "raw").length == hashed.length) { "Wrong Method!!! " }
            val cond =
                if (method.startsWith("SpringSecurity")) {
                    { pw: String -> method.passwordHashingType()!!.check(pw, lower) }
                } else {
                    { pw: String -> digest(method, pw, "raw") == lower }
                }
            mask.maskParallel(dict, cond).orEmpty()
        }
}
