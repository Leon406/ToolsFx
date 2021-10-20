package me.leon.ext

interface IEncode {

    fun decode(encoded: String, dict: String, charset: String): ByteArray

    fun encode2String(bytes: ByteArray, dict: String, charset: String): String
}
