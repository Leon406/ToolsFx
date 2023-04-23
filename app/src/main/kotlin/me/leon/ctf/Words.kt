package me.leon.ctf

import me.leon.ext.readResourceText

/**
 * @author Leon
 * @since 2023-04-23 15:57
 * @email deadogone@gmail.com
 */
object Words {

    val DICT_WORDS: HashSet<String> by lazy {
        readResourceText("/words.txt").lines().filter { it.isNotEmpty() }.toHashSet()
    }

    fun String.isWord() = DICT_WORDS.contains(lowercase())
}
