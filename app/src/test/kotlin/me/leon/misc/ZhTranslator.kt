package me.leon.misc

import me.leon.ext.readResourceText

/**
 * @author Leon
 * @since 2023-07-18 11:01
 * @email deadogone@gmail.com
 */
data class ZhTranslator(val path: String, val reversed: Boolean = false) {
    private var tries = HanziTrie()
    private val dicts = mutableMapOf<String, String>()

    init {
        loadDict()
    }

    private fun loadDict() {
        readResourceText(path)
            .lines()
            .map { it.split("\t") }
            .filter { it.size > 1 }
            .fold(dicts) { acc, s ->
                val value = s[1].split(" ").first()
                if (reversed) {
                    acc[value] = s[0]
                } else {
                    acc[s[0]] = value
                }
                acc
            }
        tries =
            dicts.keys.fold(HanziTrie()) { acc, s ->
                acc.insert(s)
                acc
            }
    }

    fun loadDict(other: String, isReverse: Boolean = false) {
        readResourceText(other)
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

        dicts.keys.fold(tries) { acc, s ->
            acc.insert(s)
            acc
        }
    }

    fun convert(text: String): String = buildString {
        var pre = ""
        for (c in text) {
            if (pre.isEmpty()) {
                if (tries.search(c.toString())) {
                    pre = c.toString()
                } else {
                    append(c)
                }
            } else {
                if (tries.search(pre + c)) {
                    pre += c
                } else {
                    if (dicts[pre] != null) {
                        append(dicts[pre])
                    } else {
                        append(pre.ss())
                    }
                    pre =
                        if (tries.search(c.toString())) {
                            c.toString()
                        } else {
                            append(c)
                            ""
                        }
                }
            }
        }
        if (pre.isNotEmpty()) {
            append(dicts[pre] ?: pre)
        }
    }

    /** fixme performance issue */
    private fun String.ss(): String {
        //        println(this)
        if (isEmpty()) {
            return ""
        }
        if (length == 1) {
            return mapping()
        }
        if (length == 2) {
            return first().toString().mapping() + last().toString().mapping()
        }
        for (i in length - 1 downTo 1) {
            val s = dicts[substring(0, i)]
            if (s != null) {
                val sub = substring(i, length)
                return s + sub.ss()
            }
        }
        return (dicts[first().toString()] ?: first().toString()) + substring(1).ss()
    }

    private fun String.mapping() = dicts[this] ?: this
}

/** 缓存翻译 */
val CACHE_TRANSLATOR = mutableMapOf<String, ZhTranslator>()
