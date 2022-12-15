package me.leon.toolsfx.plugin

import me.leon.ext.stacktrace
import me.leon.toolsfx.plugin.ext.ImageServiceType
import tornadofx.*

class ImageController : Controller() {

    fun process(
        type: ImageServiceType,
        input: String,
        isFile: Boolean = false,
        params: Map<String, String> = emptyMap()
    ): Any {
        return runCatching { type.process(input, isFile, params) }.getOrElse { it.stacktrace() }
    }
}
