package me.leon.ext.ocr

import me.leon.ext.*
import tornadofx.*

object BaiduOcr {
    private const val apiKey = "FfBOUz203SVQGP1j3YYun6yp"
    private const val secretKey = "nvkBxgmsQo72sN6G2BR91lk1nhGAkEhU"
    private var token: String? = null

    fun accessToken(ak: String = apiKey, sk: String = secretKey) =
        ("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials" +
                "&client_id=$ak" +
                "&client_secret=$sk")
            .simpleReadFromNet()
            .fromJson(MutableMap::class.java)["access_token"]
            ?.also { token = it as String }

    fun ocr(url: String): String {
        return ocrData("url=$url")
    }

    fun ocrBase64(base64: String): String {
        return ocrData("image=${base64.urlEncoded}")
    }

    private fun ocrData(data: String): String {
        token ?: accessToken()
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
            ?: kotlin.error("request failed")
    }
}
