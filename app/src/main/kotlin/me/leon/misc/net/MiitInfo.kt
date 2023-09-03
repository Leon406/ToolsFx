package me.leon.misc.net

import me.leon.ext.*

/**
 * @author Leon
 * @since 2023-02-23 10:02
 * @email deadogone@gmail.com
 */
object MiitInfo {
    private var _token = ""
    private var expirationTime = 0L
    private const val API_AUTH = "https://hlwicpfwc.miit.gov.cn/icpproject_query/api/auth"
    private const val API_INFO =
        "https://hlwicpfwc.miit.gov.cn/icpproject_query/api/icpAbbreviateInfo/queryByCondition"

    fun getToken(): String {
        if (System.currentTimeMillis() < expirationTime) {
            println("cache token")
            return _token
        }
        API_AUTH.readFromNet(
                "POST",
                data = "authKey=842e8a70ccfec79038d6416e2a4c198a&timeStamp=1677115804892",
                headers =
                    mapOf(
                        "Content-Type" to "application/x-www-form-urlencoded",
                        "Referer" to "https://beian.miit.gov.cn/"
                    )
            )
            .fromJson(MiitAuth::class.java)
            .also {
                _token = it.params.bussiness
                expirationTime = System.currentTimeMillis() + it.params.expire
                println("new token $_token")
            }
        return _token
    }

    fun domainInfo(domain: String) =
        API_INFO.readFromNet(
                "POST",
                data = "{\"unitName\": \"${domain}\",\"serviceType\":1}",
                headers =
                    mapOf(
                        "token" to getToken(),
                        "Content-Type" to "application/json",
                        "Referer" to "https://beian.miit.gov.cn/"
                    )
            )
            .also { println(it) }
            .fromJson(MiitDetail::class.java)
}
