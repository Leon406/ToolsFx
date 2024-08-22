package me.leon

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator

inline fun <reified T> T.toJson() = Json.encodeToString(this)

inline fun <reified T> String.fromJson() =
    Json { ignoreUnknownKeys = true }.decodeFromString<T>(this)

@Serializable data class Data(val a: Int, val b: String = "null")

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("type")
@Serializable
sealed class State {
    @Serializable @SerialName("success") data class Success(val data: String) : State()

    @Serializable @SerialName("fail") data class Fail(val code: Int) : State()
}

fun main() {
    State.Success.serializer()
    println(Data(42, "str").toJson())
    println("""{"a":42, "b": "str"}""".fromJson<Data>())
    println("""{"a":42,"c":"dd"}""".fromJson<Data>())
    println("""{"data":"42","type":"success"}""".fromJson<State>())
    println("""{"code":42,"type":"fail"}""".fromJson<State>())
    println(State.Fail(100).toJson())
    println(State.Success("Ok").toJson())
    println("""["a","42","43"]""".fromJson<Array<String>>().contentToString())
    println("""["a","42","43"]""".fromJson<MutableList<String>>().joinToString(" "))
}
