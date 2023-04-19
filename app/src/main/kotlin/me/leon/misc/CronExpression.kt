package me.leon.misc

/**
 * @author Leon
 * @since 2023-04-18 16:00
 * @email deadogone@gmail.com
 */
data class CronExpression(val expression: String) {
    val parts: List<String> = expression.split(" ")
    val length: Int = parts.size

    val second: String = if (length == 5) "*" else parts[0]
    val minute: String = if (length == 5) parts[0] else parts[1]
    val hours: String = if (length == 5) parts[1] else parts[2]
    val date: String = if (length == 5) parts[2] else parts[3]
    val month: String = if (length == 5) parts[3] else parts[4]
    val week: String = if (length == 5) parts[4] else parts[5]
    val year: String = if (length == 7) parts[6] else "*"

    fun explain(): String {
        val sb = StringBuilder()
        if (year != EVERY) {
            sb.append(year).append("Y ")
        }
        if (week != EVERY && week != DATE_WEEK_EXCLUDE) {
            val index = WEEK_LIST.indexOf(week.uppercase())
            sb.append(week.takeIf { index != -1 } ?: parseWeek(week)).append(" ")
        }

        if (month != EVERY) {
            sb.append(parseRange(month, 12)).append("M ")
        }
        if (date != EVERY && date != DATE_WEEK_EXCLUDE) {
            sb.append(parseRange(date, 31)).append("D ")
        }
        if (hours != EVERY) {
            sb.append(parseRange(hours, 23)).append("h ")
        }
        if (minute != EVERY) {
            sb.append(parseRange(minute, 59)).append("m ")
        }
        if (second != EVERY) {
            sb.append(parseRange(second, 59)).append("s ")
        }

        return sb.toString()
    }
}

private const val EVERY = "*"
private const val SPECIFIC = ","
// date
private const val DATE_WEEK_EXCLUDE = "?"
private val WEEK_LIST = listOf("", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
private val REGEXP_RANGE = """(\d+)-(\d+)/(\d+)""".toRegex()
private val REGEXP_RANGE_START = """(\d+|\*)/(\d+)""".toRegex()
private val REGEXP_WEEK = """(\d+)#(\d+)""".toRegex()
private val REGEXP_WEEK2 = """(\d)L""".toRegex()

fun parseRange(s: String, max: Int = 0) =
    if (s == "LW") {
        "last workday"
    } else if (s == "L") {
        "last wday"
    } else if (s == "W") {
        "workday"
    } else if (REGEXP_RANGE.matches(s)) {
        val (_, from, end, steps) = REGEXP_RANGE.find(s)!!.groupValues
        (from.toInt()..end.toInt() step steps.toInt()).toList().joinToString(SPECIFIC)
    } else if (REGEXP_RANGE_START.matches(s)) {
        val (_, from, steps) = REGEXP_RANGE_START.find(s.replace(EVERY, "0"))!!.groupValues
        (from.toInt()..max step steps.toInt()).toList().joinToString(SPECIFIC)
    } else {
        s
    }

fun parseWeek(w: String) =
    if (WEEK_LIST.contains(w.uppercase())) {
        w.uppercase()
    } else if (REGEXP_WEEK.matches(w)) {
        val (_, week, nth) = REGEXP_WEEK.find(w.replace(EVERY, "0"))!!.groupValues
        "${nth.toInt().th} ${WEEK_LIST[week.toInt()]}"
    } else if (REGEXP_WEEK2.matches(w)) {
        val (_, week) = REGEXP_WEEK2.find(w)!!.groupValues
        "last ${WEEK_LIST[week.toInt()]}"
    } else {
        w
    }

val Int.th: String
    get() =
        "$this${
        when (this) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }"
