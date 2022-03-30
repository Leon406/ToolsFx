package me.leon.controller

import me.leon.Digests
import me.leon.ext.*
import me.leon.ext.crypto.*
import tornadofx.*

class DigestController : Controller() {
    fun digest(method: String, data: String, inputEncode: String, isSingleLine: Boolean = false) =
        if (isSingleLine) data.lineAction2String { digest(method, it, inputEncode) }
        else digest(method, data, inputEncode)

    private fun digest(method: String, data: String, inputEncode: String) =
        catch({ "digest error: $it" }) {
            if (method.startsWith("CRC"))
                if (method.contains("32")) data.decodeToByteArray(inputEncode).crc32()
                else data.decodeToByteArray(inputEncode).crc64()
            else Digests.hashHexString(method, data.decodeToByteArray(inputEncode))
        }

    fun digestFile(method: String, path: String) =
        catch({ "digest file error: $it" }) {
            if (path.isEmpty()) ""
            else if (method.startsWith("CRC"))
                if (method.contains("32")) path.crc32File() else path.crc64File()
            else Digests.hashByFile(method, path)
        }
}
