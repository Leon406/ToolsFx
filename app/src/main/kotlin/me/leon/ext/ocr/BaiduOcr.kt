package me.leon.ext.ocr

import me.leon.ext.fromJson
import me.leon.ext.fx.Prefs
import me.leon.ext.readBytesFromNet
import me.leon.ext.simpleReadFromNet
import tornadofx.urlEncoded

object BaiduOcr {
    private var token: String? = null

    fun accessToken(ak: String = "", sk: String = "") =
        ("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials" +
                "&client_id=$ak" +
                "&client_secret=$sk")
            .simpleReadFromNet()
            .fromJson(MutableMap::class.java)["access_token"]
            ?.also { token = it as String }

    fun ocr(url: String): String {
        return ocrData("url=$url&detect_language=true")
    }

    fun ocrBase64(base64: String): String {
        return ocrData("image=${base64.urlEncoded}&detect_language=true")
    }

    private fun ocrData(data: String): String {
        require(Prefs.ocrKey.isNotEmpty()) { "请先配置百度OCR" }
        require(Prefs.ocrSecret.isNotEmpty()) { "请先配置百度OCR" }
        token ?: accessToken(Prefs.ocrKey, Prefs.ocrSecret)
        return "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token=$token"
            .readBytesFromNet(
                "POST",
                headers = mutableMapOf("Content-Type" to "application/x-www-form-urlencoded"),
                data = data
            )
            .decodeToString()
            .also { println(it) }
            .fromJson(BaiduOcrBean::class.java)
            .results
            ?.joinToString(System.lineSeparator()) { it.words }
            ?: error("request failed")
    }
}
