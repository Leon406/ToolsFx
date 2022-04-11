package me.leon.classical

fun ByteArray.xor(key: String) =
    mapIndexed { index, c -> (c.toInt() xor key[index % key.length].code).toByte() }.toByteArray()

fun ByteArray.xor(key: ByteArray) =
    mapIndexed { index, c -> (c.toInt() xor key[index % key.size].toInt()).toByte() }.toByteArray()
