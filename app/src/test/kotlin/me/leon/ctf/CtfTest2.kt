package me.leon.ctf

import java.io.File
import kotlin.test.assertEquals
import me.leon.TEST_CTF_DIR
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
}
