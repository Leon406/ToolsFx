package me.leon.ctf

import java.io.File
import kotlin.test.assertEquals
import me.leon.TEST_CTF_DIR
import me.leon.ext.*
import org.junit.Test

class CtfTest2 {
    @Test
    fun dnaDecode() {
        File(TEST_CTF_DIR, "dna.txt").readText().dnaDecode().also {
            println(it)
            println(it.dna())
        }
        println(dnaMap)
    }

    @Test
    fun caesarBox() {
        val height = 3
        val data = "Hello World!"
        val expected = "Hlodeor!lWl"

        assertEquals(expected, data.caesarBox(height))
        assertEquals("HelloWorld!", expected.caesarBoxDecrypt(height))
    }

    @Test
    fun rot8000() {
        val data = "The Quick Brown Fox Jumped Over The Lazy Dog."
        val expected = "籝籱籮 籚籾籲籬籴 籋类籸粀籷 籏籸粁 籓籾籶籹籮籭 籘籿籮类 籝籱籮 籕籪粃粂 籍籸籰簷"

        assertEquals(expected, data.rot8000())
        assertEquals(data, expected.rot8000())
    }

    @Test
    fun cetacean() {
        val data = "hi"
        val expected = "EEEEEEEEEeeEeEEEEEEEEEEEEeeEeEEe"

        assertEquals(expected, data.cetacean())
        assertEquals(data, expected.cetaceanDecrypt())
    }

    @Test
    fun yygqTest() {
        //        println("好sdf手动蝶阀".yygq()
        //            .also {
        //                println(it.yygqDecode())
        //            })

        println(File(TEST_CTF_DIR, "yygq.txt").readText().yygqDecode())
    }

    @Test
    fun manchester() {
        val data = "你好manchester encode data"
        //        println(data.manchester())
        //        println(data.manchester().manchesterDecode2String())
        // 10110010
        // 标准 1001101001011001
        //  0110010110100110
        // 差分 1010011010100101
        // https://upload-images.jianshu.io/upload_images/7648905-6c4a8341a9f08b3e.png?imageMogr2/auto-orient/strip|imageView2/2/w/1007/format/webp

        val testData = "B2" // 10110010
        assertEquals("0110010110100110", testData.hex2ByteArray().manchester())
        assertEquals("10110010", testData.hex2ByteArray().manchester().manchesterDecode())
        assertEquals("1001101001011001", testData.hex2ByteArray().manchester(true))
        assertEquals("10110010", testData.hex2ByteArray().manchester(true).manchesterDecode(true))
        assertEquals("1010011010100101", testData.hex2ByteArray().manchesterDiff())

        assertEquals("10110010", testData.hex2ByteArray().manchesterDiff().manchesterDiffDecode())
        println("10010011011000100001000101101010111111001101".manchesterDecode())
    }
}
