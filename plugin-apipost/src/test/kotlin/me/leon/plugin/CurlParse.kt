package me.leon.plugin

import me.leon.toolsfx.plugin.net.*
import org.junit.Test

class CurlParse {

    @Test
    fun parse2() {
        println("https://www.baidu.com".parseCurl())
        val raw =
            """
  curl "http://xx.xx.xx.xx:9101/sys/login" ^
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
        """
                .trimIndent()

        println(raw.parseCurl())
    }

    @Test
    fun parse() {
        val raw2 =
            """
  curl "https://xluser-ssl.xunlei.com/v1/shield/captcha/init" ^
  -H "authority: xluser-ssl.xunlei.com" ^
   -H "dnt: 1" ^
  -H "content-type: text/plain;charset=UTF-8" ^
  -H "accept: */*" ^
  -H "origin: https://pan.xunlei.com" ^
  -H "sec-fetch-site: same-site" ^
  -H "sec-fetch-mode: cors" ^
  -H "sec-fetch-dest: empty" ^
  -H "referer: https://pan.xunlei.com/s/VMw5q-6uE83SOZJuuadfAspbA1?path=^%^2F" ^
  -H "accept-language: zh-CN,zh;q=0.9,en;q=0.8" ^
  --data-binary "^{^\^"client_id^\^":^\^"Xqp0kJBXWhwaTpB6^\^"}" ^
  --compressed
        """
                .trimIndent()
        println(raw2.parseCurl())
        val r =
            """
            curl https://api.kinh.cc/Picture/ImgBB.php
            -X POST
            -H "Content-Type: application/json"
            -d "{\"FilePicture\":\"@file\"}" 
        """
                .trimIndent()

        println(r.also { println(it) }.parseCurl())
    }

    @Test
    fun removeQuote() {
        "'Content-Type: application/json'".removeFirstAndEndQuotes().also { println(it) }
        "\"Content-Type: application/json\"".removeFirstAndEndQuotes().also { println(it) }
        "'Content-Type: application/json".removeFirstAndEndQuotes().also { println(it) }
        "\"Content-Type: application/json".removeFirstAndEndQuotes().also { println(it) }
    }

    @Test
    fun parseFile() {
        "apiType=bilibili&token=5c483f653d928ef0c83d3547efb12792&image=@file".paramsParse().also {
            println(it)
        }
        //        val raw ="""
        //            curl https://www.hualigs.cn/api/upload
        //            -X POST
        //            -H "accept:application/json, text/javascript, */*; q=0.01"
        //            -H "user-agent:Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML,
        // like Gecko) Chrome/86.0.4240.198 Safari/537.36"
        //            -d "apiType=bilibili&token=5c483f653d928ef0c83d3547efb12792&image=@file"
        //        """.trimIndent()
        //
        //        println(raw.parseCurl())
    }
}
