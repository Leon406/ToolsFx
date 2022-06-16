package me.leon

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

inline fun <reified T> T.toJson() = Json.encodeToString(this)

inline fun <reified T> String.fromJson() =
    Json { ignoreUnknownKeys = true }.decodeFromString<T>(this)

@Serializable data class Data(val a: Int, val b: String = "null")

fun main() {

    println(Data(42, "str").toJson())
    println("""{"a":42, "b": "str"}""".fromJson<Data>())
    println("""{"a":42,"c":"dd"}""".fromJson<Data>())
    println("""["a","42","43"]""".fromJson<Array<String>>().contentToString())
    println("""["a","42","43"]""".fromJson<MutableList<String>>().joinToString(" "))
}
