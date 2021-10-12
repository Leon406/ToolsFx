package me.leon.plugin

import me.leon.toolsfx.plugin.net.NetHelper.parseCurl
import org.junit.Test

class CurlParse {
    @Test
    fun parse() {

        val raw =
            """
  curl "http://47.98.112.234:9101/sys/login" ^
  -H "Connection: keep-alive" ^
  -H "Accept: application/json, text/javascript, */*; q=0.01" ^
  -H "DNT: 1" ^
  -H "X-Requested-With: XMLHttpRequest" ^
  -H "Content-Type: application/x-www-form-urlencoded; charset=UTF-8" ^
  -H "Origin: http://47.98.112.234:9101" ^
  -H "Referer: http://47.98.112.234:9101/login.html;jsessionid=4C97420C4B8AF8FB680581FF233398E0" ^
  -H "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8" ^
  -H "Cookie: JSESSIONID=4C97420C4B8AF8FB680581FF233398E0" ^
  --compressed ^
  --insecure
        """.trimIndent()

        println(raw.parseCurl())
        println("http://www.baidu.com".parseCurl())
    }
}
