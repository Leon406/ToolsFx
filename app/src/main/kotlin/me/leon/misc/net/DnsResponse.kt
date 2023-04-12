package me.leon.misc.net

/**
 * @author Leon
 * @since 2023-04-10 9:00
 * @email deadogone@gmail.com
 */
@Suppress("ALL")
data class DnsResponse(
    val Status: Int,
    val TC: Boolean,
    val RD: Boolean,
    val RA: Boolean,
    val AD: Boolean,
    val CD: Boolean,
    val Question: Question,
    val Answer: List<Answer>
)

data class Question(val name: String, val type: Int)

@Suppress("ALL") data class Answer(val name: String, val TTL: Int, val type: Int, val data: String)
