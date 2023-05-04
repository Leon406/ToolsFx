package me.leon.crack.douyin

import kotlinx.coroutines.delay
import me.leon.ext.readBytesFromNet
import me.leon.selenium.Selenium

/**
 * @author Leon
 * @since 2023-01-12 9:36
 * @email deadogone@gmail.com
 */
private const val COUNT = 30

private const val UID = "MS4wLjABAAAAHBzaYq41eZhmDn9cOTQya8X3-YxoAYTOLm1BM947R_A"

private const val QUERY_FORMAT =
    "device_platform=webapp&aid=6383&screen_width=1920&screen_height=1080" +
        "&os_name=Windows&os_version=10&cpu_core_num=12&device_memory=8" +
        "&platform=PC&downlink=10&effective_type=4g&round_trip_time=100" +
        "&sec_user_id=$UID" +
        "&max_cursor=%s" +
        "&locate_query=False&count=$COUNT"

private const val COOKIE =
    "ttwid=1%7CfAd-xtIAXDq0GGfzA6AkbXNJ11-dEzxQspZbqsmrN0w%7C1673262048%7" +
        "Ca2150a159edd95de9da46d5efced2d6e0e2414859e4c4d3c896a4544f66dc003; "

private const val UA =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/95.0.4638.69 Safari/537.36"
private const val REFERER = "https://www.douyin.com/user"

suspend fun main() {

    val maxCursor = 1_667_207_880_000L
    var query = QUERY_FORMAT.format(maxCursor)
    val bogus = Selenium.makeXBogus(query, UA)
    println(bogus)
    query = "$query&X-Bogus=$bogus"
    println(query)
    var ok = false
    while (!ok) {
        "https://www.douyin.com/aweme/v1/web/aweme/post/?$query"
            .also { println(it) }
            .readBytesFromNet(
                headers = mapOf("referer" to REFERER, "user-agent" to UA, "cookie" to COOKIE)
            )
            .also {
                val body = it.decodeToString()
                println("______$body")
                ok = body.isNotEmpty()
                if (ok.not()) {
                    delay(3000)
                }
            }
    }
}
