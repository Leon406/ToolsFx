package me.leon.compress

interface ICompress {

    fun compress(bytes: ByteArray): ByteArray

    fun decompress(bytes: ByteArray): ByteArray
}
