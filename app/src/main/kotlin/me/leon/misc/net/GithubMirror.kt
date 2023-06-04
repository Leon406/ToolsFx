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
private const val RELEASE_RESOURCE_KEY = "/releases/download/"
private val RELEASE_MIRRORS =
    listOf(
        "https://slink.ltd" to true,
        "https://gh.gh2233.ml" to true,
        "https://gh.ddlc.top" to true,
        "https://ghdl.feizhuqwq.cf" to true,
        "https://git.xfj0.cn" to true,
        "https://ghproxy.com" to true,
        "https://gh.con.sh" to true,
        "https://ghps.cc" to true,
        "https://hub.gitmirror.com" to true,
        "https://js.xxooo.ml" to true,
        "https://proxy.freecdn.ml/?url=" to true,
        "https://cors.isteed.cc/github.com" to false,
        "https://download.njuu.cf" to false,
        "https://download.yzuu.cf" to false,
        "https://download.nuaa.cf" to false,
        "https://download.fastgit.org" to false,
        "https://download.fastgit.ixmu.net" to false,
        "https://kgithub.com" to false,
    )

/** 支持release, raw, 预览路径 */
fun String.githubMirror() =
    with(substringAfter(RAW)) {
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
