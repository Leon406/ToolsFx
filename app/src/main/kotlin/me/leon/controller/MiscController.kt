package me.leon.controller

import me.leon.ext.stacktrace
import me.leon.view.MiscServiceType
import tornadofx.*

class MiscController : Controller() {

    fun process(type: MiscServiceType, input: String): String {
        return runCatching { type.process(input, mutableMapOf()) }.getOrElse { it.stacktrace() }
    }
}
