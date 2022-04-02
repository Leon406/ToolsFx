package me.leon.controller

import me.leon.*
import me.leon.ext.*
import me.leon.ext.crypto.*
import tornadofx.*

class DigestController : Controller() {
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
        else if (method.passwordHashingType() != null) {
            method.passwordHashingType()!!.hash(data.decodeToByteArray(inputEncode))
        } else data.decodeToByteArray(inputEncode).hash2String(method)

    fun digestFile(method: String, path: String): String =
        catch({ "digest file error: $it" }) {
            if (path.isEmpty()) ""
            else if (method.startsWith("CRC"))
                if (method.contains("32")) path.crc32File() else path.crc64File()
            if (method.passwordHashingType() != null) {
                kotlin.error("not support")
            } else path.fileHash(method)
        }
}
