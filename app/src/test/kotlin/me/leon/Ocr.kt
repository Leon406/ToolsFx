package me.leon

import java.io.File
import me.leon.ext.ocr.BaiduOcr
import me.leon.ext.toBase64
import org.junit.Before
import org.junit.Test

class Ocr {

    @Before
    fun setUp() {
        BaiduOcr.accessToken()
    }

    @Test
    fun urlOcr() {
        val data = "https://wx1.sinaimg.cn/mw2000/7736d59fly1gzpm3yc7m6j20j80ip755.jpg"
        println(BaiduOcr.ocr(data))
    }

    @Test
    fun base64Ocr() {
        println(BaiduOcr.ocrBase64(File(TEST_OCR_DIR, "ocrbase64.txt").readText()))
    }

    @Test
    fun fileOcr() {
        println(BaiduOcr.ocrBase64(File(TEST_OCR_DIR, "ocr.jpg").toBase64()))
    }
}
