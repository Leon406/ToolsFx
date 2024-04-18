package me.leon.misc.net

import me.leon.ext.fromJson
import me.leon.ext.readFromNet

/**
 * @author Leon
 * @since 2023-07-28 14:48
 * @email deadogone@gmail.com
 */
object ShortUrl {
    fun baka(url: String): String =
        ("https://baka.link/shorten?url=$url")
            .readFromNet()
            .fromJson(Map::class.java)["s_url"]
            .toString()

    fun tinyUrl(url: String) = ("https://tinyurl.com/api-create.php?url=$url").readFromNet()

    fun aadTw(url: String) =
        "https://aad.tw/index.php?m=Index&a=create"
            .readFromNet(
                "POST",
                headers = mapOf("Content-Type" to "application/x-www-form-urlencoded"),
                data = "url=$url"
            )
            .fromJson(Map::class.java)["tinyurl"]
            .toString()

    fun ecx(url: String) =
        "https://www.ecx.cx/api/url/add"
            .readFromNet(
                "POST",
                headers = mapOf("Authorization" to "Token cH4lpSuC6LgqoDidiqB5"),
                data = "{\"url\":\"$url\"}"
            )
            .fromJson(Map::class.java)["short"]
            .toString()

    fun dwz(url: String) =
        "https://www.dwz.lc/api/url/add"
            .readFromNet(
                "POST",
                headers = mapOf("Authorization" to "Token xxHQfao69Ra9G7EI87mC"),
                data = "{\"url\":\"$url\"}"
            )
            .fromJson(Map::class.java)["short"]
            .toString()
}

interface Shorten {
    fun short(url: String): String
}

enum class ShortUrlEnum : Shorten {

    AAD_TW {
        override fun short(url: String) = ShortUrl.aadTw(url)
    },
    ECX {
        override fun short(url: String) = ShortUrl.ecx(url)
    },
    DWZ {
        override fun short(url: String) = ShortUrl.dwz(url)
    },
    BAKA {
        override fun short(url: String) = ShortUrl.baka(url)
    },
    TINY_URL {
        override fun short(url: String) = ShortUrl.tinyUrl(url)
    },
}

fun String.shortUrl(type: String) = ShortUrlEnum.valueOf(type).short(this)
