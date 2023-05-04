package me.leon.misc

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NginxLog(
    val ip: String,
    val date: Date,
    val request: String,
    val status: Int,
    val length: Int,
    val refer: String,
    val userAgent: String
) {
    fun timeRangeCondition(start: Date, end: Date = Date()): Boolean {
        return date.after(start) && date.before(end)
    }

    fun timeRangeCondition(start: String, end: Date = Date()): Boolean {
        return date.after(sdf.parse(start)) && date.before(end)
    }

    fun timeRangeCondition(start: String, end: String): Boolean {
        return date.after(sdf.parse(start)) && date.before(sdf.parse(end))
    }

    fun statusRangeCondition(start: Int, end: Int = start): Boolean {
        return status in start..end
    }

    fun refererCondition(reg: Regex): Boolean {
        return refer.contains(reg)
    }

    fun refererCondition(reg: String): Boolean {
        return refer.contains(reg)
    }

    fun uaCondition(reg: Regex): Boolean {
        return userAgent.contains(reg)
    }

    fun uaCondition(reg: String): Boolean {
        return userAgent.contains(reg)
    }

    fun ipCondition(reg: String): Boolean {
        return ip.contains(reg)
    }

    fun reqCondition(reg: Regex): Boolean {
        return request.contains(reg)
    }

    fun reqCondition(reg: String): Boolean {
        return request.contains(reg)
    }
}

val sdf = SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss +0800", Locale.US)

fun MatchResult.toNginxLog() =
    with(groups) {
        NginxLog(
            this["ip"]!!.value,
            sdf.parse(this["time"]!!.value),
            this["reqLine"]!!.value,
            this["code"]!!.value.toInt(),
            this["len"]!!.value.toInt(),
            this["refer"]!!.value,
            this["ua"]!!.value
        )
    }
