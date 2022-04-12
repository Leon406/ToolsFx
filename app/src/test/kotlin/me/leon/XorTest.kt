package me.leon

import java.io.File
import kotlin.experimental.xor
import me.leon.classical.xor
import me.leon.ext.*
import org.junit.Test

class XorTest {

    @Test
    fun xorCrack() {
        val raw = "dsdfdsf你好"
        val encoded = listOf(7, 66, 86, 85, 7, 66, 84, -41, -34, -111, -41, -106, -34)
        val key = "c123"
        raw
            .toByteArray()
            .mapIndexed { index, c -> c.toInt() xor key[index % key.length].code }
            .also { println(it) }

        encoded
            .mapIndexed { index, c -> (c xor key[index % key.length].code).toByte() }
            .toByteArray()
            .decodeToString()
            .also { println(it) }
        println(
            raw.toByteArray()
                .mapIndexed { index, c -> (c.toInt() xor encoded[index % encoded.size]).toByte() }
                .toByteArray()
                .decodeToString()
        )

        val ints = arrayOf(71, 65, 77, 69)
        "E:/download/360/xor (1).txt"
            .toFile()
            .readBytes()
            .mapIndexed { index, byte -> byte xor (ints[index % 4]).toByte() }
            .toByteArray()
            .decodeToString()
            .also { println(it) }
        println("!-,\"<".toByteArray().xor("flag{").joinToString(" "))
    }

    @Test
    fun readWechat() {
        val dir = "Your wechat image directory"

        dir.toFile().walk().filter { it.extension == "dat" }.forEach {
            println(it.absolutePath)
            decryptFile(it)
        }
    }

    private fun decryptFile(it: File) {
        weChatXorKey(it).run {
            it.inputStream().use { input ->
                File(it.parentFile.absolutePath, it.nameWithoutExtension + ".${this.second}")
                    .outputStream()
                    .use { output ->
                        output.write(
                            input
                                .readBytes()
                                .map { (it.toInt() xor this.first.toInt()).toByte() }
                                .toByteArray()
                        )
                    }
            }
        }
    }

    private val imgMagicNumbers = arrayOf("ffd8", "8950", "4749", "5249", "4949", "504b")
    private val fileExtension = arrayOf("jpg", "png", "gif", "webp", "tif", "zip")
    private fun weChatXorKey(file: File): Pair<Byte, String> {
        file.inputStream().use {
            it.readNBytes(2).toHex().let { hex ->
                for (bytes in imgMagicNumbers.map { it.hex2ByteArray() }) {
                    val keys =
                        hex
                            .hex2ByteArray()
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
        }
        throw UnknownError()
    }
}
