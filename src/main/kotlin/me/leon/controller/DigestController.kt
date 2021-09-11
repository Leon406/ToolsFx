package me.leon.controller

import me.leon.Digests
import me.leon.ext.*
import tornadofx.*

class DigestController : Controller() {
    fun digest(method: String, data: String) =
        catch({ "digest error: $it" }) {
            if (data.isEmpty()) ""
            else if (method.startsWith("CRC")) data.crc32() else Digests.hash(method, data)
        }

    fun digestFile(method: String, path: String) =
        catch({ "digest file error: $it" }) {
            if (path.isEmpty()) ""
            else if (method.startsWith("CRC")) path.crc32File()
            else Digests.hashByFile(method, path)
        }
}
