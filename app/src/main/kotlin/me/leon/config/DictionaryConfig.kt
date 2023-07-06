package me.leon.config

/**
 * @author Leon
 * @since 2023-06-28 13:10
 * @email deadogone@gmail.com
 */
data class DictionaryConfig(
    val active: Int,
    val dictFileName: String? = null,
    val autoPronounce: Boolean = true,
    val dicts: List<Dict>
) {

    val dictFile
        get() = dictFileName?.ifEmpty { "vocabulary.txt" } ?: "vocabulary.txt"

    data class Dict(
        val name: String,
        val url: String,
        val hideCssElement: String,
        val js: String?
    ) {
        var autoPronounce: Boolean = true
    }
}
