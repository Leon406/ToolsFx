package me.leon.misc

import java.text.SimpleDateFormat
import kotlin.test.Ignore
import me.leon.ext.toFile
import org.junit.Test

@Ignore
class LogTest {
    private val logFormat =
        """(?<ip>\d+\.\d+\.\d+\.\d+) - - \[(?<time>[^]]+)] "(?<reqLine>[^"]+)\" (?<code>\d+) 
            |(?<len>\d+) "(?<refer>[^"]+)" "(?<ua>[^"]+)\" ?
            |(?<realip>\d+\.\d+\.\d+\.\d+)?"""
            .trimMargin()
            .replace("\n", "")
            .toRegex()
    private val logPath = "C:\\Users\\Leon\\Downloads\\access.log"

    @Test
    fun rawString() {
        println(logFormat)
    }

    @Test
    fun logFilter() {
        logPath
            .toFile()
            .also { if (!it.exists()) return }
            .bufferedReader()
            .lineSequence()
            .map { logFormat.find(it) to it }
            .filter { it.first != null && filterCondition(it.first!!.toNginxLog()) }
            .map { it.second }
            .forEach { println(it) }
    }

    @Test
    fun groupCondition() {
        grouping { mapping(it) }
    }

    @Test
    fun groupByIp() {
        grouping { it.ip }
    }

    @Test
    fun groupByIpR() {
        groupingInfo { it.ip }
    }

    @Test
    fun groupByRefer() {
        grouping { it.refer }
    }

    @Test
    fun groupByUri() {
        grouping {
            when {
                it.request.contains("/resource/") -> "/resource/"
                it.request.contains("/JudicialZj/") -> "/JudicialZj/"
                it.request.contains(".php") -> "php"
                it.request.contains("/public/legopinion/opinion-detail.jsp") ->
                    "/public/legopinion/opinion-detail.jsp"
                it.request.contains("/public/orgpsnQt/searchDtl") -> "/public/orgpsnQt/searchDtl"
                it.request.contains("/ssyflowstat/post") -> "/ssyflowstat/post"
                it.request.contains("/file/download") -> "/file/download"
                it.request.contains("/sysinfo/attach/selectList/") -> "/sysinfo/attach/selectList"
                it.request.contains("/public/orgpsn") -> "/public/orgpsn"
                it.request.contains("public/orginfo/search") -> "POST /public/orginfo/search"
                it.request.contains("/public/") -> "/public/"
                else -> it.request
            }
        }
    }

    @Test
    fun groupByTime() {
        grouping { SimpleDateFormat("MMdd_HH").format(it.date) }
    }

    @Test
    fun groupByStatus() {
        grouping { it.status.toString() }
    }

    private fun filterCondition(result: NginxLog): Boolean =
        // true
        result.uaCondition("Android")

    private fun groupCondition(result: NginxLog): Boolean = filterCondition(result)

    private fun mapping(result: NginxLog) = result.userAgent

    private fun grouping(action: (NginxLog) -> String) {

        var count = 0
        logPath
            .toFile()
            .also { if (!it.exists()) return }
            .bufferedReader()
            .lineSequence()
            .map { logFormat.find(it) }
            .filter { it != null && groupCondition(it.toNginxLog()) }
            .map { action.invoke(it!!.toNginxLog()) }
            .fold(mutableMapOf<String, Int>()) { acc, s ->
                count++
                acc.apply { put(s, getOrPut(s) { 0 } + 1) }
            }
            .also {
                val list = it.toList().sortedByDescending { it.second }
                println("数量: " + list.size)
                println(
                    list.joinToString("\n") {
                        it.first +
                            " : " +
                            it.second +
                            "/$count (${String.format("%.2f", it.second * 100f / count)}%)"
                    }
                )
            }
    }

    private fun groupingInfo(action: (NginxLog) -> String) {

        var count = 0
        logPath
            .toFile()
            .also { if (!it.exists()) return }
            .bufferedReader()
            .lineSequence()
            .map { logFormat.find(it) }
            .filter { it != null && groupCondition(it.toNginxLog()) }
            .map { it!!.toNginxLog() }
            .map { action.invoke(it) to it }
            .fold(mutableMapOf<String, HashSet<String>>()) { acc, s ->
                count++
                acc.apply {
                    put(
                        s.first,
                        getOrPut(s.first) { hashSetOf() }.apply { add(s.second.userAgent) }
                    )
                }
            }
            .also {
                val list = it.toList().sortedByDescending { it.second.size }
                println("数量: " + list.size)
                println(
                    list.joinToString("\n") {
                        it.first +
                            " : " +
                            it.second.joinToString("\n\t\t") +
                            "  " +
                            it.second.size +
                            "/$count" +
                            " (${String.format("%.2f", it.second.size * 100f / count)}%)"
                    }
                )
            }
    }
}
