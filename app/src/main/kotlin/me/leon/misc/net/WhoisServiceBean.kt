package me.leon.misc.net

/**
 * @author Leon
 * @since 2023-02-23 13:33
 * @email deadogone@gmail.com
 */
data class WhoisServiceBean(
    val description: String,
    val publication: String,
    val services: List<List<List<String>>>,
    val version: String
)
