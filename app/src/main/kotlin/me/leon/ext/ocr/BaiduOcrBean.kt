package me.leon.ext.ocr

import com.google.gson.annotations.SerializedName

data class BaiduOcrBean(@SerializedName("words_result") val results: List<BaiduOcrResult>?)

data class BaiduOcrResult(val words: String)
