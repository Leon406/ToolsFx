package me.leon.toolsfx.plugin.compress

interface ICompress {

    fun compress(bytes: ByteArray): ByteArray

    fun decompress(bytes: ByteArray): ByteArray

    fun compress(
        raw: String,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ): String = ""

    fun decompress(
        raw: String,
        inputEncode: String = "raw",
        outputEncode: String = "base64",
    ): String = ""
}
