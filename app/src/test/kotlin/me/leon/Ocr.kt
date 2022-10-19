package me.leon

import java.io.File
import kotlin.test.*
import me.leon.ext.ocr.BaiduOcr
import me.leon.ext.toBase64
import org.junit.Test

@Ignore
class Ocr {

    @Test
    fun urlOcr() {
        val data = "https://wx1.sinaimg.cn/mw2000/7736d59fly1gzpm3yc7m6j20j80ip755.jpg"
        assertTrue(BaiduOcr.ocr(data).contains("你们别看我"))
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
        assertTrue(BaiduOcr.ocrBase64(File(TEST_OCR_DIR, "ocr.jpg").toBase64()).contains("你们别看我平时"))
    }

    companion object {
        init {
            BaiduOcr.accessToken()
        }
    }
}
