package me.leon.misc.net

/**
 * @author Leon
 * @since 2023-04-25 12:44
 * @email deadogone@gmail.com
 */
private const val RAW = "https://raw.githubusercontent.com"
private const val RAW2 = "https://raw.github.com"
private const val GITHUB = "https://github.com"
private val RAW_MIRRORS =
    listOf(
        "https://ghproxy.net/https://raw.githubusercontent.com",
        "https://ghproxy.com/https://raw.githubusercontent.com",
        "https://gh-proxy.com/https://raw.githubusercontent.com",
        "https://raw.fgit.cf",
        "https://raw.fastgit.org",
        "https://fastly.jsdelivr.net/gh",
        "https://gcore.jsdelivr.net/gh",
        "https://github.moeyy.xyz/https://raw.githubusercontent.com",
    )
private const val RELEASE_RESOURCE_KEY = "/releases/download/"
private val RELEASE_MIRRORS =
    listOf(
        "https://gh.h233.eu.org" to true,
        "https://slink.ltd" to true,
        "https://gh.gh2233.ml" to true,
        "https://gh.ddlc.top" to true,
        "https://git.xfj0.cn" to true,
        "https://ghproxy.com" to true,
        "https://gh-proxy.com" to true,
        "https://gh.con.sh" to true,
        "https://hub.gitmirror.com" to true,
        "https://proxy.freecdn.ml/?url=" to true,
        "https://cors.isteed.cc/github.com" to false,
        "https://download.njuu.cf" to false,
        "https://download.yzuu.cf" to false,
        "https://download.nuaa.cf" to false,
        "https://download.fastgit.org" to false,
    )

/** 支持release, raw, 预览路径 */
fun String.githubMirror() =
    with(substringAfter(RAW).substringAfter(RAW2)) {
        val path = this.substringAfter(GITHUB).replace("/blob/", "/")
        val mirrors =
            if (contains(RELEASE_RESOURCE_KEY)) {
                RELEASE_MIRRORS.map {
                    if (it.second) {
                        "${it.first}/$this"
                    } else {
                        "${it.first}$path"
                    }
                }
            } else {
                RAW_MIRRORS.map {
                    if (it.contains(".jsdelivr.net")) {
                        "$it${path.jsDelivrPath()}"
                    } else {
                        "$it$path"
                    }
                }
            }
        mirrors.linkCheck().filter { it.second }.joinToString(System.lineSeparator()) { it.first }
    }

private fun String.jsDelivrPath() =
    "(/[^/]+/[^/]+)(?:/blob)?/([^/]+)(/.+)".toRegex().replace(this, "$1@$2$3")
