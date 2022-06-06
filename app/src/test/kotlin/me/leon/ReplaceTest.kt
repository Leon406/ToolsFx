package me.leon

import java.util.regex.Pattern
import kotlin.system.measureTimeMillis
import lianzhang.Translator
import org.junit.Test

class ReplaceTest {
    @Test
    fun rTest() {
        val replaceMap =
            mutableMapOf(
                "一" to "1",
                "二" to "2",
                "三" to "3",
                "四" to "4",
                "五" to "5",
                "六" to "6",
                "七" to "7",
                "八" to "8",
                "九" to "9",
                "十" to "10"
            )

        val d2 = "一二三四五六七八九十"
        val sb = StringBuilder(d2)
        measureTimeMillis {
            sb.replace(replaceMap.keys.joinToString("|").toRegex()) {
                replaceMap[it.value].orEmpty()
            }
        }
            .also { println("$it") }

        var d = "一二三四五六七八九十"

        measureTimeMillis {
            for ((k, v) in replaceMap) {
                d = d.replace(k.toRegex(), v)
            }
        }
            .also { println(" $it") }
    }

    @Test
    fun reg() {
        var dst = "Leon666"
        val pattern: Pattern = Pattern.compile("(?m)^(?i)leon\\d+(.*)")
        val matcher = pattern.matcher(dst)
        println(dst)
        dst = matcher.replaceAll("ddd $1")
        println(dst)
    }

    @Test
    fun trans() {

        val req =
            """
GET /aaa/static/bbb/js/cc.js HTTP/2
Host: dss0.bd.com
Sec-Ch-Ua: "(Not(A:Brand";v="8", "Chromium";v="98"
Accept-Language: zh-CN,zh;q=0.9""".trimIndent()

        val rsp =
            """
            HTTP/1.1 200 OK
            Access-Control-Allow-Credentials: true
            Access-Control-Allow-Methods: GET, POST, OPTIONS
            Access-Control-Allow-Origin: *
            Connection: close
        """.trimIndent()

        Pattern.compile("[A-Z]{3,} /.*? HTTP/").matcher(req).find().also { println(it) }
        Pattern.compile("HTTP/[\\d.]+ \\d+").matcher(rsp).find().also { println(it) }

        val dataToTranslated =
            arrayOf(
                "Log exceptions to a local directory:",
                "Burp Suite Professional v2022.2.2 - licensed to leon",
                "Project file: ",
                "View filter: Please wait ...",
                "Capture filter: ",
                "OK",
                "Automatic Project Backup [disk projects only]",
                "C:\\Users\\Leon",
                "Updates [installer version only]",
                "Automatic Project Backup [disk projects only]",
                """
GET /aa/static/bb/js/cc.js HTTP/2
Host: dss0.bd.com
Sec-Ch-Ua: "(Not(A:Brand";v="8", "Chromium";v="98"
Sec-Ch-Ua-Mobile: ?0
Sec-Ch-Ua-Platform: "Windows"
Accept: */*
Sec-Fetch-Site: cross-site
Sec-Fetch-Mode: no-cors
Sec-Fetch-Dest: script
Referer: https://www.baidu.com/
Accept-Encoding: gzip, deflate
Accept-Language: zh-CN,zh;q=0.9""".trimIndent()
            )
        dataToTranslated.forEach { Translator.translate("cn", it).run { println("$it--->$this ") } }
    }
}
