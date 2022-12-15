package me.leon.toolsfx.plugin.sample2

import me.leon.ext.stacktrace
import tornadofx.*

class Sample2Controller : Controller() {

    fun process(type: SampleServiceType, input: String): String {
        return runCatching { type.process(input, mutableMapOf()) }.getOrElse { it.stacktrace() }
    }
}
