package me.leon

import io.github.eb4j.mdict.MDictDictionary

/**
 * @author Leon
 * @since 2023-06-30 14:05
 * @email deadogone@gmail.com
 */
object MdictLoader {
    fun loadMdx(mdx: String): MDictDictionary {
        val start = System.currentTimeMillis()
        val dict = MDictDictionary.loadDictionary(mdx)
        System.out.printf(
            "%s %s %s takes %s\n",
            dict.mdxVersion,
            dict.description,
            dict.encoding,
            System.currentTimeMillis() - start
        )
        return dict
    }
}
