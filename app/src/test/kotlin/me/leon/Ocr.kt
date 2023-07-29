package me.leon

import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import kotlin.test.*
import me.leon.ext.ocr.BaiduOcr
import me.leon.ext.toBase64
import org.junit.Test

@Ignore
class Ocr {

    @Test
    fun urlOcr() {
        fixSsl()
        val data = "https://wx1.sinaimg.cn/mw2000/7736d59fly1gzpm3yc7m6j20j80ip755.jpg"
        assertTrue(BaiduOcr.ocr(data).contains("你们别看我"))
    }

    private fun fixSsl() {
        val trustManagers =
            arrayOf(
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {
                        // nop
                    }

                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {
                        // nop
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate>? {
                        return null
                    }
                }
            )
        val sc = SSLContext.getInstance("TLSv1.2")
        sc.init(null, trustManagers, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    }

    @Test
    fun base64Ocr() {
        assertTrue(
            BaiduOcr.ocrBase64(File(TEST_OCR_DIR, "ocrbase64.txt").readText())
                .contains("fromJson(BaiduOcrBean class.java)BaiduOcrBean")
        )
    }

    @Test
    fun fileOcr() {
        println(BaiduOcr.ocrBase64(File(TEST_OCR_DIR, "ja.jpg").toBase64()))
        assertTrue(BaiduOcr.ocrBase64(File(TEST_OCR_DIR, "ocr.jpg").toBase64()).contains("你们别看我平时"))
    }

    companion object {
        init {
            BaiduOcr.accessToken()
        }
    }
}
