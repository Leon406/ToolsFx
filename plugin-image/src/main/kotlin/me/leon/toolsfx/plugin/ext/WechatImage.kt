package me.leon.toolsfx.plugin.ext

import java.io.File
import me.leon.ext.hex2ByteArray
import me.leon.ext.toHex

private val imgMagicNumbers = arrayOf("ffd8", "8950", "4749", "5249", "4949", "504b")
private val fileExtension = arrayOf("jpg", "png", "gif", "webp", "tif", "zip")

fun File.weChatDecrypt(): ByteArray =
    weChatXorKey(this).run {
        inputStream().use { input ->
            input.readBytes().map { (it.toInt() xor this.first.toInt()).toByte() }.toByteArray()
        }
    }

fun weChatXorKey(file: File): Pair<Byte, String> {
    file.inputStream().use {
        val hex = it.readNBytes(2).toHex()
        for (bytes in imgMagicNumbers.map { it.hex2ByteArray() }) {
            val keys =
                hex.hex2ByteArray()
                    .mapIndexed { index, c ->
                        (c.toInt() xor bytes[index % bytes.size].toInt()).toByte()
                    }
                    .toByteArray()
                    .also { println(it.contentToString()) }
            if (keys.first() == keys.last()) {
                println(imgMagicNumbers.indexOf(bytes.toHex()))
                return keys.first() to fileExtension[imgMagicNumbers.indexOf(bytes.toHex())]
            }
        }
    }
    error("unMatch")
}
