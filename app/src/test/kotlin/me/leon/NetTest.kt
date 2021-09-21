package me.leon

import me.leon.encode.base.base64
import me.leon.ext.cast
import me.leon.ext.readBytesFromNet
import me.leon.ext.readHeadersFromNet
import me.leon.ext.safeAs
import org.junit.Test

class NetTest {

    @Test
    fun fetch() {
        ("https://app.xiaoe-tech.com/get_video_key.php?edk=CiCmt6ItZK%2BbwGUya552EY7CvBHTuyarJXJrbisGFV%2FI" +
                "xhCO08TAChiaoOvUBCokYjRhNjFiNTgtMmVhNy00OWYxLTgwZGMtZTE0NTIyODc5YWIy&fileId=52858907848127" +
                "19098&keySource=VodBuildInKMS")
            .readBytesFromNet()
            .base64()
            .also { println(it) }
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
    fun fetchJson() {

        var l: MutableList<String>? = mutableListOf("", "")
        l.safeAs<HashSet<String>>().also { println(it) }
        //        l?.cast<HashSet<String>>()
        l = null
        l.safeAs<HashSet<String>>().also { println(it) }
        l.cast<HashSet<String>>()
    }
}
