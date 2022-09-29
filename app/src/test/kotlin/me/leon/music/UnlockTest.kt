package me.leon.music

import java.io.File
import kotlin.test.Test
import me.leon.ext.toFile
import me.leon.music.ncm.NcmDecoder
import me.leon.music.qmc.qmcDecode

/**
 *
 * @author Leon
 * @since 2022-09-28 14:17
 * @email: deadogone@gmail.com
 */
class UnlockTest {
    @Test
    fun qmcTest() {
        "E:\\download\\360\\张靓颖 - 春夜喜雨 (Live).qmcflac".toFile().qmcDecode()
    }

    @Test
    fun ncmTest() {
        val file = File("C:\\Users\\Leon\\Downloads\\麻雀.ncm")
        NcmDecoder.ncmDecrypt(file)
    }
}
