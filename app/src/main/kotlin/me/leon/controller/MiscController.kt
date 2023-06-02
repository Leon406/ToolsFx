package me.leon.controller

import me.leon.ext.stacktrace
import me.leon.misc.MiscServiceType
import tornadofx.*

class MiscController : Controller() {

    fun process(
        type: MiscServiceType,
        input: String,
        params: Map<String, String> = emptyMap()
    ): String {
        return runCatching { type.process(input, params) }.getOrElse { it.stacktrace() }
    }
}
