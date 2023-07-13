package me.leon.misc

import me.leon.ext.fromJson
import me.leon.ext.readFromNet
import me.leon.misc.net.linkCheck
import java.net.URLEncoder

object Translator {
    val SUPPORT_LANGUAGE =
        arrayOf(
            "zh-CN",
            "en",
            "ja",
            "ko",
            "fr",
            "de",
            "ru",
            "ar",
            "es",
            "it",
            "hi",
            "th",
            "vi",
            "pl",
            "ro",
            "id",
        )
    private const val URL_GOOGLE = "%s/api/translate/?engine=google" + "&from=%s&to=%s&text=%s"
    private const val URL_LINGVA = "https://lingva.ml/api/v1/%s/%s/%s"

    private val mirrors =
        listOf(
            "https://translate.bus-hit.me",
            "https://simplytranslate.pussthecat.org",
            "https://translate.tiekoetter.com",
            "https://translate.slipfox.xyz",
            "https://translate.catvibers.me",
            "https://t.opnxng.com",
            "https://st.alefvanoon.xyz",
            "https://simplytranslate.leemoon.network"
        )

    private val okServer = mutableSetOf<String>()

    fun init() {
        mirrors
            .linkCheck(3000)
            .filter { it.second }
            .map { it.first }
            .also {
                okServer.addAll(it)
                println(it)
            }
    }

    fun google(text: String, src: String = "auto", target: String = "zh-CN"): String =
        URL_GOOGLE.format(okServer.first(), src, target, URLEncoder.encode(text, "utf-8"))
            .readFromNet()
            .fromJson(Map::class.java)["translated-text"]
            .toString()

    fun lingva(text: String, src: String = "auto", target: String = "zh"): String =
        URL_LINGVA.format(
            src.replace("-CN", ""),
            target.replace("-CN", ""),
            URLEncoder.encode(text, "utf-8")
        )
            .readFromNet()
            .fromJson(Map::class.java)["translation"]
            .toString()
}
