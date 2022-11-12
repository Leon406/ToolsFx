package me.leon

import kotlin.test.Test
import me.leon.ext.toFile
import me.leon.toolsfx.plugin.net.HttpUrlUtil
import me.leon.toolsfx.plugin.net.HttpUrlUtil.verifySSL

/**
 *
 * @author Leon
 * @since 2022-11-03 9:06
 * @email: deadogone@gmail.com
 */
class JbLicenseTest {

    @Test
    //    @Ignore
    fun licenseServerValidate() {
        val file = "C:\\Users\\Leon\\Desktop\\jblicense.txt".toFile()
        HttpUrlUtil.followRedirect = true
        verifySSL(false)
        val raw = mutableListOf<String>()
        file
            .readLines()
            .distinct()
            .filterNot { it.startsWith("#") || it.isBlank() }
            .also { raw.addAll(it) }
            .map { it to checkUrl(it) }
            .filter { it.second }
            .map { it.first }
            .also {
                println("${it.size} / ${raw.size} ")
                println("\tok\n${it.joinToString("\n")}")
                println("\tfail\n${(raw - it).joinToString("\n")}")
            }
    }

    private fun checkUrl(url: String): Boolean {
        HttpUrlUtil.followRedirect = true
        val response = runCatching { HttpUrlUtil.get(url) }.getOrNull()
        val location = response?.headers?.get("Location")
        val hasAuth = location?.run { this.toString().contains("fls-auth") } ?: false

        // 域名设置followRedirect会自动跳转,只有ip会有location
        if (location == null) {
            return response?.data?.contains("loader_config={") ?: false
        }
        var validate = false
        if (hasAuth) {
            // 设置followRedirect会自动会重定向到 /login
            validate =
                runCatching {
                        HttpUrlUtil.get(location.toString()).data.contains("loader_config={")
                    }
                    .getOrDefault(false)
        }
        return validate
    }

    @Test
    fun check() {
        checkUrl("https://35.188.104.230").also { println(it) }
        verifySSL(false)
        checkUrl("https://35.188.104.230").also { println(it) }
        verifySSL(true)
        checkUrl("https://35.188.104.230").also { println(it) }
        verifySSL(false)
        checkUrl("https://35.188.104.230").also { println(it) }
    }
}
