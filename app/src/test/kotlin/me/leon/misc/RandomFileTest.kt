package me.leon.misc

import java.io.File
import java.io.RandomAccessFile
import me.leon.TEST_DATA_DIR
import org.junit.Test

class RandomFileTest {
    @Test
    fun random() {
        val file = File(TEST_DATA_DIR, "random")
        RandomAccessFile(file, "rw").run {
            seek(2)
            println(readLine())
            writeBytes("abcdf")
            writeChars("chccc")
            writeInt(97)
        }
    }
}
