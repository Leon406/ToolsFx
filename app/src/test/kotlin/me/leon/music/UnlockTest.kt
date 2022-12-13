package me.leon.music

import java.io.File
import kotlin.test.Ignore
import kotlin.test.Test
import me.leon.ext.toFile
import me.leon.music.ncm.NcmDecoder
import me.leon.music.qmc.qmcDecode

/**
 * @author Leon
 * @since 2022-09-28 14:17
 */
@Ignore
class UnlockTest {
    @Test
    fun qmcTest() {

        "C:\\Users\\Leon\\Downloads\\Telegram Desktop\\慕容晓晓 - 爱情买卖.qmcogg".toFile().qmcDecode()
        //        "E:\\download\\360\\张靓颖 - 春夜喜雨 (Live).qmcflac".toFile().qmcDecode()
    }

    @Test
    fun ncmTest() {
        val file = File("C:\\Users\\Leon\\Downloads\\麻雀.ncm")
        NcmDecoder.ncmDecrypt(file)
    }
}
