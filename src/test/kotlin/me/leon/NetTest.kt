package me.leon

import me.leon.base.base64
import me.leon.ext.readBytesFromNet
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
}
