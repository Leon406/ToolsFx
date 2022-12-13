package me.leon.ctf

import me.leon.encode.base.*

/**
 * @author Leon
 * @since 2022-10-13 10:07
 */
val EIGHT_MAP =
    listOf(
        "坤",
        "剥",
        "比",
        "观",
        "豫",
        "晋",
        "萃",
        "否",
        "谦",
        "艮",
        "蹇",
        "渐",
        "小过",
        "旅",
        "咸",
        "遁",
        "师",
        "蒙",
        "坎",
        "涣",
        "解",
        "未济",
        "困",
        "讼",
        "升",
        "蛊",
        "井",
        "巽",
        "恒",
        "鼎",
        "大过",
        "姤",
        "复",
        "颐",
        "屯",
        "益",
        "震",
        "噬嗑",
        "随",
        "无妄",
        "明夷",
        "贲",
        "既济",
        "家人",
        "丰",
        "离",
        "革",
        "同人",
        "临",
        "损",
        "节",
        "中孚",
        "归妹",
        "睽",
        "兑",
        "履",
        "泰",
        "大畜",
        "需",
        "小畜",
        "大壮",
        "大有",
        "夬",
        "乾"
    )

fun String.eightDiagram(delimiter: String = "") =
    base64(needPadding = false).asIterable().joinToString(delimiter) {
        EIGHT_MAP[BASE64_DICT.indexOf(it)]
    }

fun String.eightDiagramDecode(delimiter: String = "") =
    dictValueParse(delimiter = delimiter)
        .map { BASE64_DICT[EIGHT_MAP.indexOf(it)] }
        .joinToString("")
        .base64Decode2String()

fun String.dictValueParse(map: List<String> = EIGHT_MAP, delimiter: String = ""): List<String> {
    if (delimiter.isNotEmpty() && contains(delimiter)) return split(delimiter)
    val tmp: StringBuilder = StringBuilder()
    return fold(mutableListOf()) { acc, c ->
        acc.apply {
            if (map.contains(tmp.toString() + c)) {
                add(tmp.toString() + c)
                tmp.clear()
            } else {
                tmp.append(c)
            }
        }
    }
}
