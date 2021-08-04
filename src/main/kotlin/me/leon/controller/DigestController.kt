package me.leon.controller

import me.leon.Digests
import me.leon.ext.stacktrace
import tornadofx.*

class DigestController : Controller() {
    fun digest(method: String, data: String) =
        try {
            if (data.isEmpty()) "" else Digests.hash(method, data)
        } catch (e: Exception) {
            "digest error: ${e.stacktrace()}"
        }

    fun digestFile(method: String, path: String) =
        try {
            if (path.isEmpty()) "" else Digests.hashByFile(method, path)
        } catch (e: Exception) {
            "digest file error: ${e.stacktrace()}"
        }
}
