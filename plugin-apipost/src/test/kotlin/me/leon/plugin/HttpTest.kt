package me.leon.plugin

import java.io.File
import java.util.Base64
import me.leon.toolsfx.plugin.net.HttpUrlUtil
import me.leon.toolsfx.plugin.net.NetHelper
import org.junit.Test

class HttpTest {
    @Test
    fun getTest() {
        HttpUrlUtil.get(
            "https://d006.eduyun.cn/videoworks/mda-ki0cfj2mmzb2j5vr/ykt_tbkt_hls_1080_7/video/%E5%88%9D%E4%" +
                "B8%89%E3%80%90%E6%95%B0%E5%AD%A6%EF%BC%88%E4%BA%BA%E6%95%99%E7%89%88%EF%BC%89%E3%80%9122.1." +
                "3%E4%BA%8C%E6%AC%A1%E5%87%BD%E6%95%B0y=a(x-h)%C2%B2+k%E7%9A%84%E5%9B%BE%E8%B1%A1%E5%92%8C" +
                "%E6%80%A7%E8%B4%A8%EF%BC%884%EF%BC%89.m3u8"
        )
    }

    @Test
    fun postTest() {
        HttpUrlUtil.addPreHandle {
            it.headers["preHead"] = "pre"
            it.params["prePrams"] = it.params.entries.joinToString("") { "${it.key}-${it.value}" }
        }

        HttpUrlUtil.addPostHandle { Base64.getEncoder().encodeToString(it) }
        HttpUrlUtil.post(
            "https://lab.magiconch.com/api/nbnhhsh/guess",
            mutableMapOf("text" to "cylx"),
            isJson = true
        )
    }

    @Test
    fun headTest() {
        HttpUrlUtil.head("https://lab.magiconch.com/api/nbnhhsh/guess")
    }

    @Test
    fun options() {
        HttpUrlUtil.options("https://httpbin.org/anything")
    }

    @Test
    fun put() {
        HttpUrlUtil.put("https://httpbin.org/anything")
    }

    @Test
    fun delete() {
        HttpUrlUtil.delete("https://httpbin.org/anything")
    }

    @Test
    fun patch() {
        HttpUrlUtil.patch("http://httpbin.org/anything")
        HttpUrlUtil.patch("https://httpbin.org/anything")
    }
    @Test
    fun trace() {
        HttpUrlUtil.trace("https://httpbin.org/anything")
    }
    @Test
    fun downloadTest() {
        val url =
            "https://pics6.baidu.com/feed/42a98226cffc1e17b0a7684e8fbbc70a738de96e.jpeg" +
                "?token=19433714addc862045404c72677e91b3"
        HttpUrlUtil.get(url, isDownload = true)
    }

    @Test
    fun proxy() {
        HttpUrlUtil.setupProxy(java.net.Proxy.Type.SOCKS, "127.0.0.1", 7890)
        val url =
            "https://pics6.baidu.com/feed/42a98226cffc1e17b0a7684e8fbbc70a738de96e.jpeg" +
                "?token=19433714addc862045404c72677e91b3"
        HttpUrlUtil.get(url, isDownload = true)
    }

    @Test
    fun preHandle() {
        HttpUrlUtil.addPreHandle {}

        val url =
            "https://pics6.baidu.com/feed/42a98226cffc1e17b0a7684e8fbbc70a738de96e.jpeg" +
                "?token=19433714addc862045404c72677e91b3"
        HttpUrlUtil.get(url, isDownload = true)
    }

    @Test
    fun postHandle() {
        val url =
            "https://pics6.baidu.com/feed/42a98226cffc1e17b0a7684e8fbbc70a738de96e.jpeg" +
                "?token=19433714addc862045404c72677e91b3"
        HttpUrlUtil.get(url, isDownload = true)
    }

    @Test
    fun uploadTest() {
        HttpUrlUtil.setupProxy(java.net.Proxy.Type.SOCKS, "127.0.0.1", 7890)
        HttpUrlUtil.postFile(
            "https://www.hualigs.cn/api/upload",
            listOf(
                File("E:\\prj\\Android-app\\app\\src\\main\\res\\drawable\\icon_photograph.png")
            ),
            "image",
            mutableMapOf(
                "apiType" to "bilibili,muke",
                "token" to "5c483f653d928ef0c83d3547efb12792",
            )
        )
    }

    @Test
    fun uploadTest2() {
        HttpUrlUtil.setupProxy(java.net.Proxy.Type.SOCKS, "127.0.0.1", 7890)
        HttpUrlUtil.postFile(
            "https://image.kieng.cn/upload.html?type=tt",
            listOf(
                File("E:\\prj\\Android-app\\app\\src\\main\\res\\drawable\\icon_photograph.png")
            ),
            "image"
        )
    }

    @Test
    fun params() {
        val headers =
            """
            accept: */*
            accept-encoding: gzip, deflate, br
            accept-language: zh-CN,zh;q=0.9,en;q=0.8
            Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
            dnt: 1
            origin: https://blog.csdn.net
            referer: https://blog.csdn.net/qq_39658819/article/details/77527670
            sec-fetch-dest: empty
            sec-fetch-mode: cors
            sec-fetch-site: same-site
            user-agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)
             Chrome/86.0.4240.198 Safari/537.36
        """.trimIndent()
       val header2 = "Max-Forwards:44\n" +
                "aa:bb"
        println(NetHelper.parseHeaderString(header2))
    }
}
