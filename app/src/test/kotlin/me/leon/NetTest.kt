package me.leon

import me.leon.encode.base.base64
import me.leon.ext.cast
import me.leon.ext.readBytesFromNet
import me.leon.ext.readHeadersFromNet
import me.leon.ext.safeAs
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Arrays
import java.util.Collections

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

    @Test
    fun fileRead() {
        val dir = "C:\\Users\\Leon\\Downloads\\Compressed\\m_zygsctf02\\m_zygsctf02"
        File(dir).listFiles().filter { it.readText().contains("key|flag|ctf".toRegex()) }.also {
            println(it)
        }
    }

    @Test
    fun fileType() {
        val fileDir = "C:/Users/Leon/Desktop"

        File(fileDir).listFiles().filter { it.isFile }.map {
            it.inputStream().readNBytes(5)
        }.also {
            println(it.joinToString(" ") { it.contentToString() + String(it) })
        }
    }

    val magics = mapOf(
        "ffd8ff" to "jpg",
        "89504e47" to "png",
        "4749463837" to "gif",
        "4749463839" to "gif",
        "49492a00227105008037" to "tif",
        "424d228c010000000000" to "bmp",
        "424d8240090000000000" to "bmp",
        "424d8e1b030000000000" to "bmp",
        "41433130313500000000" to "dwg",
        "7b5c727466315c616e73" to "rtf",
        "38425053000100000000" to "psd",
        "46726f6d3a203d3f6762" to "eml",
        "5374616E64617264204A" to "mdb",
        "252150532D41646F6265" to "ps",
        "255044462d312e" to "pdf",
        "2e524d46000000120001" to "rmvb",
        "464c5601050000000900" to "flv",
        "0000001C66747970" to "mp4",
        "00000020667479706" to "mp4",
        "00000018667479706D70" to "mp4",
        "49443303000000002176" to "mp3",
        "000001ba210001000180" to "mpg",
        "3026b2758e66cf11a6d9" to "wmv",
        "52494646e27807005741" to "wav",
        "52494646d07d60074156" to "avi",
        "4d546864000000060001" to "mid",
        "526172211a0700cf9073" to "rar",
        "235468697320636f6e66" to "ini",
        "504B03040a0000000000" to "jar",
        "504B0304140008000800" to "jar",
        "d0cf11e0a1b11ae10" to "xls",
        "504B0304" to "zip",
        "4d5a9000030000000400" to "exe",
        "3c25402070616765206c" to "jsp",
        "4d616e69666573742d56" to "mf",
        "7061636b616765207765" to "java",
        "406563686f206f66660d" to "bat",
        "1f8b0800000000000000" to "gz",
        "cafebabe0000002e0041" to "class",
        "49545346030000006000" to "chm",
        "04000000010000001300" to "mxp",
        "6431303a637265617465" to "torrent",
        "6D6F6F76" to "mov",
        "FF575043" to "wpd",
        "CFAD12FEC5FD746F" to "dbx",
        "2142444E" to "pst",
        "AC9EBD8F" to "qdf",
        "E3828596" to "pwl",
        "2E7261FD" to "ram",
    )

}
