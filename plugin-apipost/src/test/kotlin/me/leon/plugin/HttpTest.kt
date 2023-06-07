package me.leon.plugin

import java.io.File
import java.util.Base64
import kotlin.test.Test
import me.leon.Api
import me.leon.ext.*
import me.leon.toolsfx.plugin.net.*

class HttpTest {

    private val httpConfigPath = File(File("").absoluteFile.parentFile, "/testdata/https")

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
    fun getBodyTest() {
        HttpUrlUtil.getBody("https://httpbin.org/anything", "hello body").also { println(it) }
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
        HttpMethod.head("https://lab.magiconch.com/api/nbnhhsh/guess")
    }

    @Test
    fun options() {
        HttpMethod.options("https://httpbin.org/anything")
    }

    @Test
    fun put() {
        HttpMethod.put("https://httpbin.org/anything")
    }

    @Test
    fun delete() {
        HttpMethod.delete("https://httpbin.org/anything")
    }

    @Test
    fun connect() {
        HttpMethod.connect("https://httpbin.org/anything")
    }

    @Test
    fun patch() {
        HttpMethod.patch("http://httpbin.org/anything")
        HttpMethod.patch("https://httpbin.org/anything")
    }

    @Test
    fun trace() {
        HttpMethod.trace("https://www.baidu.com")
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
    fun parse() {
        val apis = readRes<HttpTest>("/apis.json").fromJsonArray(Api::class.java)
        println(apis)
        for (api in apis.take(1)) {
            println(api)
            val r =
                HttpUrlUtil.postFile(
                        api.api,
                        listOf(
                            File(
                                "E:\\prj\\Android-app\\app\\src\\main\\res\\drawable\\icon_photograph.png"
                            )
                        ),
                        api.file,
                        api.body.toMutableMap(),
                        api.headers.toMutableMap()
                    )
                    .data

            println(r.simpleJsonPath(api.result))
        }
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
        """
                .trimIndent()
        println(headers)
        val header2 = "Max-Forwards:44\n" + "aa:bb"
        println(NetHelper.parseHeaderString(headers))
        println(NetHelper.parseHeaderString(header2))
    }

    @Test
    fun cert() {

        HttpUrlUtil.verifySSL(true)
        TrustManager.parseFromCertification("$httpConfigPath/baidu.cer")
        HttpUrlUtil.get("https://www.baidu.com")
        // error cer
        TrustManager.parseFromCertification("$httpConfigPath/jianshu.cer")
        HttpUrlUtil.get("https://www.baidu.com")
    }

    @Test
    fun pkcs12() {

        println(httpConfigPath)
        TrustManager.parseFromPkcs12("$httpConfigPath/sq_client.p12", "bulianglin")
        HttpUrlUtil.postData(
            "https://193.110.201.185:11000/find_cached_nodes",
            "{\"network\":\"pnet\",\"target_id\":\"dzlOMk52c2RCU1Q4NmZHZnlTT2ZzSkUxY0tPWFlqV3dzdzVtc1k4REtwWTZiR" +
                "kxYcmo5dW4yNmQ0RWtsRVpKcQ==\",\"local_continent_code\":\"AS\",\"local_country_code\":\"CN\",\"tar" +
                "get_country_code\":\"KR\",\"num_nodes\":20}"
        )
        TrustManager.parseFromPkcs12("$httpConfigPath/baidu.p12", "123456")
        HttpUrlUtil.get("https://www.baidu.com")
    }
}
