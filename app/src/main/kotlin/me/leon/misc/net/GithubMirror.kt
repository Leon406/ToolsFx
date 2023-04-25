package me.leon.misc.net

/**
 * @author Leon
 * @since 2023-04-25 12:44
 * @email deadogone@gmail.com
 */
private const val RAW = "https://raw.githubusercontent.com"
private const val GITHUB = "https://github.com"
private val RAW_MIRRORS =
    listOf(
        "https://ghproxy.net/https://raw.githubusercontent.com",
        "https://raw.iqiq.io",
        "https://raw.fastgit.org",
        "https://raw.kgithub.com",
        "https://cdn.staticaly.com/gh",
        "https://cdn.statically.io/gh",
        "https://fastly.jsdelivr.net/gh",
        "https://cdn.jsdelivr.net/gh",
        "https://github.moeyy.xyz/https://raw.githubusercontent.com",
    )

fun String.githubMirror() =
    with(substringAfter(RAW).substringAfter(GITHUB)) {
        RAW_MIRRORS.map {
                if (it.contains(".jsdelivr.net")) {
                    "$it${jsDelivrPath()}"
                } else {
                    "$it${replace("/blob/", "/")}"
                }
            }
            .linkCheck()
            .filter { it.second }
            .joinToString(System.lineSeparator()) { it.first }
    }

private fun String.jsDelivrPath() =
    "(/[^/]+/[^/]+)(?:/blob)?/([^/]+)(/.+)".toRegex().replace(this, "$1@$2$3")
