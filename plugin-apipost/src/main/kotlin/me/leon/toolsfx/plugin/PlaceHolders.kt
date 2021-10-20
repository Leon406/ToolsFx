package me.leon.toolsfx.plugin

import me.leon.Digests
import me.leon.encode.base.base64

const val TIMESTAMP = "{{timestamp}}"
const val UUID = "{{uuid}}"
const val UUID2 = "{{uuid2}}"
val METHOD = """\{\{(\w+)\((.*)\)}}""".toRegex()

fun timeStamp() = System.currentTimeMillis()

fun uuid() = java.util.UUID.randomUUID().toString()

fun uuid2() = java.util.UUID.randomUUID().toString().replace("-", "")

fun String.replacePlaceHolders() =
    replace(UUID, uuid()).replace(UUID2, uuid2()).replace(TIMESTAMP, timeStamp().toString())

fun main() {
    val d1 = "123123{{md5(23123)}}"
    METHOD.findAll(d1).forEach {
        println(it.groupValues[1])
        println(it.groupValues[2])
        d1.replace(it.groupValues[0], it.groupValues[1].methodCall(it.groupValues[2])).also {
            println(it)
        }
    }
}

fun String.methodCall(vararg args: String) =
    when (this) {
        "md5" -> Digests.hash(this, args[0])
        "base64" -> args[0].base64()
        else -> ""
    }
