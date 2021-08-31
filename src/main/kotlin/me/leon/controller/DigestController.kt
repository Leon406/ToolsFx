package me.leon.controller

import me.leon.Digests
import me.leon.ext.catch
import tornadofx.Controller

class DigestController : Controller() {
    fun digest(method: String, data: String) =
        catch({ "digest error: $it" }) { if (data.isEmpty()) "" else Digests.hash(method, data) }

    fun digestFile(method: String, path: String) =
        catch({ "digest file error: $it" }) {
            if (path.isEmpty()) "" else Digests.hashByFile(method, path)
        }
}
