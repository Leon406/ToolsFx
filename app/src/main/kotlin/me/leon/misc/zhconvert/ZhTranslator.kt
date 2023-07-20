package me.leon.misc.zhconvert

import me.leon.ext.readResourceText

/**
 * @author Leon
 * @since 2023-07-18 11:01
 * @email deadogone@gmail.com
 */
data class ZhTranslator(val path: String, val reversed: Boolean = false) {
    private var tries = Trie()
    private val dicts = mutableMapOf<String, String>()

    init {
        loadDict(path, reversed)
    }

    fun loadDict(dict: String, isReverse: Boolean = false) {
        readResourceText(dict)
            .lines()
            .map { it.split("\t") }
            .filter { it.size > 1 }
            .fold(dicts) { acc, s ->
                val values = s[1].split(" ")
                if (isReverse) {
                    for (value in values) {
                        acc[value] = s[0]
                    }
                } else {
                    acc[s[0]] = values.first()
                }
                acc
            }

        // 优化内存占用, 只有词组才构建
        dicts.keys
            .filter { it.length > 1 }
            .fold(tries) { acc, s ->
                acc.insert(s)
                acc
            }
    }

    fun convert(text: String): String = buildString {
        var pre = ""
        for (c in text) {
            if (pre.isEmpty()) {
                if (tries.search(c)) {
                    pre = c.toString()
                } else {
                    append(c.mapping())
                }
            } else {
                if (tries.search(pre + c)) {
                    pre += c
                } else {
                    append(pre.findMapping())
                    pre =
                        if (tries.search(c)) {
                            c.toString()
                        } else {
                            append(c.mapping())
                            ""
                        }
                }
            }
        }
        if (pre.isNotEmpty()) {
            append(pre.findMapping())
        }
    }

    /** fixme performance issue */
    private fun String.findMapping(): String {
        //        println(this)
        // 剪枝优化
        if (isEmpty()) {
            return ""
        }
        if (dicts[this] != null) {
            return dicts[this]!!
        }
        if (length <= 2) {
            return map { it.mapping() }.joinToString("")
        }

        // 长度大于2,递归查找
        println("~~~~~$this")
        for (i in length - 1 downTo 1) {
            val s = dicts[substring(0, i)]
            if (s != null) {
                val sub = substring(i, length)
                return s + sub.findMapping()
            }
        }
        return first().mapping() + substring(1).findMapping()
    }

    private fun String.mapping() = dicts[this] ?: this

    private fun Char.mapping() = toString().mapping()
}

/** 缓存翻译 */
val CACHE_TRANSLATOR = mutableMapOf<String, ZhTranslator>()
