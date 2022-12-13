package me.leon.ctf

import me.leon.encode.base.radixNDecode2String
import me.leon.encode.base.radixNEncode

/**
 * 天干地支 ported from {https://github.com/chai2010/base60}
 *
 * @author Leon
 * @since 2022-10-13 15:04
 */
const val STEM = "甲乙丙丁戊己庚辛壬癸"
const val BRANCH = "子丑寅卯辰巳午未申酉戌亥"
val STEM_BRANCH =
    mutableListOf<String>().apply {
        for (i in 0 until 60) {
            add("${STEM[i % 10]}${BRANCH[i % 12]}")
        }
    }

fun String.stemBranch() = radixNEncode(STEM_BRANCH)

fun String.stemBranchDecode() = radixNDecode2String(STEM_BRANCH)
