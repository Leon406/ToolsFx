package me.leon

import kotlin.test.assertEquals
import me.leon.encode.base.base64
import me.leon.ext.*
import org.junit.Test

class NetTest {

    @Test
    fun fetch() {
        ("https://app.xiaoe-tech.com/get_video_key.php?edk=CiCmt6ItZK%2BbwGUya552EY7CvBHTuyarJXJrbisGFV%2FI" +
                "xhCO08TAChiaoOvUBCokYjRhNjFiNTgtMmVhNy00OWYxLTgwZGMtZTE0NTIyODc5YWIy&fileId=52858907848127" +
                "19098&keySource=VodBuildInKMS")
            .readBytesFromNet()
            .base64()
            .also {
                println(it)
                assertEquals("5VyPyIv3693VdklBeXVY3g==", it)
            }
    }

    @Test
    fun fetchHeaders() {
        ("https://app.xiaoe-tech.com/get_video_key.php?edk=CiCmt6ItZK%2BbwGUya552EY7CvBHTuyarJXJrbisGFV%2FI" +
                "xhCO08TAChiaoOvUBCokYjRhNjFiNTgtMmVhNy00OWYxLTgwZGMtZTE0NTIyODc5YWIy&fileId=52858907848127" +
                "19098&keySource=VodBuildInKMS")
            .readHeadersFromNet()
            .also { println(it) }
    }

    @Test
    fun fileRead() {
        TEST_PRJ_DIR
            .listFiles()
            ?.filter { it.isFile && it.readText().contains("key|flag|ctf".toRegex()) }
            .also { println(it) }
    }

    @Test
    fun fileType() {
        TEST_PRJ_DIR.listFiles()?.forEach { println(it.realExtension()) }
    }
}
