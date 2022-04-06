package me.leon.toolsfx.plugin.compress

interface ICompress {

    fun compress(bytes: ByteArray): ByteArray

    fun decompress(bytes: ByteArray): ByteArray
}
