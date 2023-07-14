package me.leon.misc

import me.leon.ext.readResourceText

/**
 * @author Leon
 * @since 2023-07-14 16:05
 * @email deadogone@gmail.com
 */
object ZhConvert {
    private val dicts = mutableMapOf<String, String>()
    private var tries = HanziTrie()
    private var currentType = ZhType.S2T_COMMON

    enum class ZhType(val showName: String, val res: String) {
        T2S("繁转简", "/t2s.txt"),
        HK2S("繁(港)转简", "/hk2s"),
        TW2S("繁(港)转简", "/tw2s.txt"),
        T2HK("繁转繁(港)", "/t2hk.txt"),
        T2TW("繁转繁(台)", "/t2tw.txt"),
        S2T("简转繁", "/s2t-diff.txt"),
        S2TW("简转繁(台)", "/s2tw-diff.txt"),
        S2HK("简转繁(港)", "/s2hk-diff.txt"),
        S2T_COMMON("简转繁", "/s2t-common.txt"),
    }

    private fun loadDict(type: ZhType = ZhType.T2S) {
        if (currentType == type) {
            return
        }
        dicts.clear()
        if (type.res.startsWith("/s2")) {
            readResourceText(ZhType.S2T_COMMON.res)
                .lines()
                .map { it.split("=") }
                .filter { it.size > 1 }
                .fold(dicts) { acc, s ->
                    acc[s[0]] = s[1]
                    acc
                }
        }
        readResourceText(type.res)
            .lines()
            .map { it.split("=") }
            .filter { it.size > 1 }
            .fold(dicts) { acc, s ->
                acc[s[0]] = s[1]
                acc
            }
        tries =
            dicts.keys.fold(HanziTrie()) { acc, s ->
                acc.insert(s)
                acc
            }
    }

    fun convert(text: String, type: ZhType = ZhType.T2S) = buildString {
        loadDict(type)
        var pre = ""
        for (c in text) {
            if (pre.isEmpty()) {
                if (tries.search("" + c)) {
                    pre = c + ""
                } else {
                    append(c)
                }
            } else {
                if (tries.search(pre + c)) {
                    pre += c
                } else {
                    append(dicts[pre] ?: pre)
                    pre =
                        if (tries.search("" + c)) {
                            c + ""
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
}
