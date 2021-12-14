package me.leon.toolsfx.plugin

import me.leon.Digests
import me.leon.encode.base.base64
import me.leon.ext.toBinaryString

const val TIMESTAMP = "{{timestamp}}"
const val TIMESTAMP2 = "{{timestamp2}}"
const val UUID = "{{uuid}}"
const val UUID2 = "{{uuid2}}"
val METHOD = """\{\{(\w+)\((.*)\)}}""".toRegex()

fun timeStamp() = System.currentTimeMillis()

fun uuid() = java.util.UUID.randomUUID().toString()

fun uuid2() = uuid().replace("-", "")

fun String.replacePlaceHolders() =
    replace(UUID, uuid())
        .replace(UUID2, uuid2())
        .replace(TIMESTAMP2, (timeStamp() / 1000).toString())
        .replace(TIMESTAMP, timeStamp().toString())
        .methodParse()

fun main() {
    val d1 = "Adbsd{{base64({{md5({{md5(23123-1231)}})}})}}"
    println(d1.methodParse())
    val d2 = "Adbsd{{base64({{digest(SHA3-256,6666dd)}})}}"
    println(d2.methodParse())
    val d3 = " Adbsd{{uuid}}  {{lowercase(dsaf123DSDf)}}"
    println(d3.replacePlaceHolders().methodParse())
    val d4 = " {{base64(21321)}}"
    println(d4.replacePlaceHolders().methodParse())
}

fun String.methodCall(args: String): String {

    println("methodCall $this $args")
    return when (this) {
        "md5" -> Digests.hash(this, args)
        "digest" ->
            Digests.hash(args.substringBefore(",").also { println(this) }, args.substringAfter(","))
        "base64" -> args.base64()
        "binary" -> args.toBinaryString()
        "uppercase" -> args.uppercase()
        "lowercase" -> args.lowercase()
        else -> this
    }
}

fun String.methodParse(): String {
    return METHOD.find(this)?.run {
        println(this.groupValues)
        var tmp = this.groupValues[2]
        while (METHOD.matches(tmp)) {
            tmp = tmp.methodParse()
        }
        replace(this.groupValues.first(), this.groupValues[1].methodCall(tmp))
    }
        ?: this
}
