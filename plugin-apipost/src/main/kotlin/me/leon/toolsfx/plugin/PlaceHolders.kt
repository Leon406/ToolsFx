package me.leon.toolsfx.plugin

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import me.leon.encode.base.base64
import me.leon.ext.toBinaryString
import me.leon.ext.toFile
import me.leon.hash

const val TIMESTAMP = "{{timestamp}}"
const val TIMESTAMP2 = "{{timestamp2}}"
const val UUID = "{{uuid}}"
const val UUID2 = "{{uuid2}}"
val METHOD = """\{\{(\w+)\((.*)\)}}""".toRegex()

fun timeStamp() = System.currentTimeMillis()

fun uuid() = java.util.UUID.randomUUID().toString()

fun uuid2() = uuid().replace("-", "")

fun String.addHttp() =
    takeIf { it.startsWith("https:") || it.startsWith("http:") } ?: "http://$this"

fun String.replacePlaceHolders() =
    replace(UUID, uuid())
        .replace(UUID2, uuid2())
        .replace(TIMESTAMP2, (timeStamp() / 1000).toString())
        .replace(TIMESTAMP, timeStamp().toString())
        .methodParse()

fun String.methodCall(args: String): String {

    println("methodCall $this $args")
    return when (this) {
        "md5" -> args.hash(this)
        "digest" -> args.substringAfter(",").hash(args.substringBefore(","))
        "base64" -> args.base64()
        "base64File" -> args.toFile().readBytes().base64()
        "binary" -> args.toBinaryString()
        "uppercase" -> args.uppercase()
        "lowercase" -> args.lowercase()
        "date2Mills" ->
            LocalDateTime.parse("$args 00:00:00", timeFormatter)
                .toInstant(ZoneOffset.of("+8"))
                .toEpochMilli()
                .toString()
        "datetime2Mills" ->
            LocalDateTime.parse(args, timeFormatter)
                .toInstant(ZoneOffset.of("+8"))
                .toEpochMilli()
                .toString()
        else -> this
    }
}

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

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
