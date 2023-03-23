package me.leon.crack

import java.io.File
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.crypto.decryptFile
import me.leon.toolsfx.plugin.net.HttpUrlUtil

/**
 * 解密参考文章: https://www.52pojie.cn/thread-1689801-1-1.html
 *
 * @author Leon
 * @since 2023-03-16 13:57
 * @email deadogone@gmail.com
 */
const val APPID = "appSTozGuyf6388"
const val UID = "u_anonymous_64128d857cd86_Epc0i5SdCo"
const val server2 = "https://pc.mijialaw.com"

const val server = "https://$APPID.h5.xiaoeknow.com"

const val API_VIDEO_INFO = "$server2/xe.course.business.video.detail_info.get/2.0.0/"

val REG_M3U8_KEY = "#EXT-X-KEY:METHOD=AES-128,URI=\"([^\"]+)\"".toRegex()
val REG_M3U8_TS = """(\w+\.\w+\.ts)\?start=\d+&end=\d+""".toRegex()

fun video(resId: String, prodId: String): XiaoeVideoInfoResponse {
    val headers = mutableMapOf<String, Any>("Content-Type" to "application/x-www-form-urlencoded")
    val params =
        mutableMapOf<String, Any>(
            "resource_id" to resId,
            "product_id" to prodId,
            "opr_sys" to "Win32"
        )
    return HttpUrlUtil.post(API_VIDEO_INFO, params, headers).data.fromJson()
}

fun videoInfo(resId: String, prodId: String): String? {
    return video(resId, prodId).data?.decryptVideoUrl()?.let {
        println(it)
        it.fromJsonArray(DecryptedVideoItem::class.java).firstOrNull()?.run {
            println(url)
            val m3u8 = HttpUrlUtil.get(url)
            println(m3u8)

            val tsName = REG_M3U8_TS.find(m3u8.data)?.groupValues?.get(1)
            println(REG_M3U8_KEY.find(m3u8.data)?.groupValues?.get(1))
            println(tsName)
            val downloadTs = path("$tsName?type=mpegts")
            println("~~~~~~~ download ts: $downloadTs")
            HttpUrlUtil.get(downloadTs, isDownload = true)
            val keyUrl = REG_M3U8_KEY.find(m3u8.data)?.groupValues?.get(1)
            getBase64Key(keyUrl!!)
        }
    }
}

fun getBase64Key(url: String): String {
    val url = "$url&uid=$UID"
    println("req: $url")
    val key = url.readBytesFromNet()
    println(key.decodeToString())
    println("key= ${key.base64()}")

    val uidBytes = UID.toByteArray()
    require(key.size == 16) { "响应key长度异常" }
    return key.mapIndexed { index, byte -> byte xor uidBytes[index] }.toByteArray().base64()
}

fun main() {
    val key = videoInfo("v_61275811e4b065461cbf4126", "p_610a413ce4b0a27d0e393ff8")
    // https://encrypt-k-vod.xet.tech/529d8d60vodtransbj1252524126/a695d22c387702295310109806/drm/v.f421220.m3u8?sign=68a5bb6fc0a61c4b7fc06f96bc22dd54&t=6413995a&us=QMBfMbOCPG&exper=180&time=1678962840428&time=1678962840433
    requireNotNull(key)
    HttpUrlUtil.downloadFolder
        .listFiles { _, name -> name.endsWith("ts") }
        ?.forEach {
            it.absolutePath.decryptFile(
                key.base64Decode(),
                ByteArray(16) { 0 },
                "AES/CBC/PKCS5Padding",
                File(HttpUrlUtil.downloadFolder, it.nameWithoutExtension + ".mp4").absolutePath
            )
        }
}
