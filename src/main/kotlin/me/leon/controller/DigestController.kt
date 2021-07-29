package me.leon.controller

import me.leon.Digests
import tornadofx.*

class DigestController :Controller() {
    fun digest(method: String, data: String) =
        try {
            if (data.isEmpty()) "" else Digests.hash(method, data)
        } catch (e: Exception) {
            e.printStackTrace()
            "digest error: ${e.message}"
        }

    fun digestFile(method: String, path: String) =
        try {
            if (path.isEmpty()) "" else Digests.hashByFile(method, path)
        } catch (e: Exception) {
            e.printStackTrace()
            "digest file error: ${e.message}"
        }
}