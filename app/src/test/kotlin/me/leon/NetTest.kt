package me.leon

import java.io.File
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
        l = null
        l.safeAs<HashSet<String>>().also { println(it) }
        l.cast<HashSet<String>>()
    }

    @Test
    fun fileRead() {
        val dir = "C:\\Users\\Leon\\Downloads\\Compressed\\m_zygsctf02\\m_zygsctf02"
        File(dir).listFiles().filter { it.readText().contains("key|flag|ctf".toRegex()) }.also {
            println(it)
        }
    }

    @Test
    fun fileType() {
        var fileDir = "C:/Users/Leon/Desktop"
        fileDir = "E:\\gitrepo\\Android-app"
        fileDir = "E:\\software\\Lily5\\soft\\dev\\cmder"
        //        fileDir = "D:\\BaiduNetdiskDownload\\雷电\\LDPlayer"

        //        File(fileDir).walk().filter { it.isFile }.forEach {
        //            println("$it  ${it.realExtension()}" )
        //        }
        File("C:\\Users\\Leon\\Desktop\\buldChm").listFiles()?.forEach {
            println(it.realExtension())
        }
    }
}
