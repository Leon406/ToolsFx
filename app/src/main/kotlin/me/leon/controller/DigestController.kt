package me.leon.controller

import me.leon.Digests
import me.leon.ext.*
import tornadofx.*

class DigestController : Controller() {
    fun digest(method: String, data: String, isSingleLine: Boolean = false) =
        if (isSingleLine) data.lineAction2String { digest(method, it) } else digest(method, data)

    private fun digest(method: String, data: String) =
        catch({ "digest error: $it" }) {
            if (data.isEmpty()) ""
            else if (method.startsWith("CRC"))
                if (method.contains("32")) data.crc32() else data.crc64()
            else Digests.hash(method, data)
        }

    fun digestFile(method: String, path: String) =
        catch({ "digest file error: $it" }) {
            if (path.isEmpty()) ""
            else if (method.startsWith("CRC"))
                if (method.contains("32")) path.crc32File() else path.crc64File()
            else Digests.hashByFile(method, path)
        }
}
