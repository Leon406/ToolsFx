package me.leon.music

import java.io.File
import java.io.RandomAccessFile

/**
 * 随机读取流
 *
 * @author Leon
 * @since 2022-09-29 9:54
 */
class RandomAccessStream(val file: File, mode: String = "r") {

    private val randomAccessFile = RandomAccessFile(file, mode)

    fun readNBytes(length: Int, reset: Boolean = false): ByteArray {
        val byteArray = ByteArray(length)
        randomAccessFile.read(byteArray)
        if (reset) {
            randomAccessFile.seek(0)
        }
        return byteArray
    }

    fun write(byteArray: ByteArray) {
        randomAccessFile.write(byteArray)
    }

    fun read(byteArray: ByteArray): Int {
        return randomAccessFile.read(byteArray)
    }

    fun readValidate(length: Int): ByteArray {
        val byteArray = ByteArray(length)
        val len = read(byteArray)
        return byteArray.copyOf(len)
    }

    fun readValidate(byteArray: ByteArray): ByteArray {
        val length = read(byteArray)
        return byteArray.copyOf(length)
    }

    fun readRemain(): ByteArray {
        return readValidate(randomAccessFile.length().toInt())
    }

    fun skip(length: Int): Int {
        return randomAccessFile.skipBytes(length)
    }
}
