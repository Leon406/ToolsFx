package me.leon.controller

import me.leon.*
import me.leon.ext.*
import me.leon.ext.crypto.*
import tornadofx.*

class DigestController : Controller() {
    private val dicts by lazy {
        DICT_DIR.toFile().listFiles()?.flatMap { it.readLines() }?.distinct() ?: listOf()
    }

    fun digest(
        method: String,
        data: String,
        inputEncode: String = "raw",
        isSingleLine: Boolean = false
    ) =
        catch({ "digest error: $it" }) {
            if (isSingleLine) data.lineAction2String { digest(method, it, inputEncode) }
            else digest(method, data, inputEncode)
        }

    private fun digest(method: String, data: String, inputEncode: String) =
        if (method.startsWith("CRC"))
            if (method.contains("32")) data.decodeToByteArray(inputEncode).crc32()
            else data.decodeToByteArray(inputEncode).crc64()
        else if (method == "Adler32") {
            data.decodeToByteArray(inputEncode).adler32()
        } else if (method.passwordHashingType() != null) {
            method.passwordHashingType()!!.hash(data.decodeToByteArray(inputEncode))
        } else data.decodeToByteArray(inputEncode).hash2String(method)

    fun digestFile(method: String, path: String): String =
        catch({ "digest file error: $it" }) {
            if (path.isEmpty()) ""
            else if (method.startsWith("CRC"))
                if (method.contains("32")) path.crc32File() else path.crc64File()
            else if (method == "Adler32") path.adler32File()
            else if (method.passwordHashingType() != null) kotlin.error("not support")
            else path.fileHash(method)
        }

    // 首次加载1400W, 8s,  100w md5 1s  21c40fc4ddd462df2509b232fef4ec6c
    // 1400w 14s md5  dd2978f9ae7014cd2d1884c5a1bbbca2
    fun crack(method: String, data: String) =
        catch({ "digest crack error: $it" }) {
            dicts.find { pw -> digest(method, pw, "raw") == data } ?: ""
        }
}
