package me.leon.misc.net

/**
 * @author Leon
 * @since 2023-04-10 9:00
 * @email deadogone@gmail.com
 */
@Suppress("ALL") data class DnsResponse(val Status: Int, val Answer: List<Answer>)

@Suppress("ALL") data class Answer(val name: String, val type: Int, val data: String)
