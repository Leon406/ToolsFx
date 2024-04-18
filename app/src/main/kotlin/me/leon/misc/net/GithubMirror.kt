package me.leon.misc.net

/**
 * @author Leon
 * @since 2023-04-25 12:44
 * @email deadogone@gmail.com
 */
private const val RAW = "https://raw.githubusercontent.com"
private const val RAW2 = "https://raw.github.com"
private const val GITHUB = "https://github.com"

/** https://github.com/XIU2/UserScript/blob/master/GithubEnhanced-High-Speed-Download.user.js */
private val RAW_MIRRORS =
    listOf(
        "https://raw.kkgithub.com",
        "https://mirror.ghproxy.com/https://raw.githubusercontent.com",
        "https://ghproxy.net/https://raw.githubusercontent.com",
        "https://fastraw.ixnic.net",
        "https://raw.cachefly.998111.xyz",
        "https://github.moeyy.xyz/https://raw.githubusercontent.com",
        "https://fastly.jsdelivr.net/gh",
        "https://gcore.jsdelivr.net/gh",
        "https://cdn.jsdelivr.us/gh",
        "https://jsdelivr.b-cdn.net/gh",
    )
private const val RELEASE_RESOURCE_KEY = "/releases/download/"

/** true 完整url false 仅path */
private val RELEASE_MIRRORS =
    listOf(
        "https://mirror.ghproxy.com" to true,
        "https://ghproxy.net" to true,
        "https://kkgithub.com" to false,
        "https://gh.h233.eu.org" to true,
        "https://gh.ddlc.top" to true,
        "https://dl.ghpig.to" to true,
        "https://slink.ltd" to true,
        "https://gh.con.sh" to true,
        "https://cors.isteed.cc" to true,
        "https://hub.gitmirror.com" to true,
        "https://sciproxy.com" to true,
        "https://ghproxy.cc" to true,
        "https://cf.ghproxy.cc" to true,
        "https://gh.jiasu.in" to true,
        "https://dgithub.xyz" to true,
        "https://download.scholar.rr.nu" to false,
        "https://download.nuaa.cf" to false,
        "https://download.yzuu.cf" to false,
    )

val REG_GITHUB_RAW = "(https://raw\\.githubusercontent\\.com)/([^/]+/[^/]+)/".toRegex()
val REG_GITHUB = "(https://github\\.com)/([^/]+/[^/]+)/(tree|blob)/".toRegex()

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

fun String.githubRawUrl() = replace(REG_GITHUB, "$RAW/$2/")

fun String.githubRepoUrl() = replace(REG_GITHUB_RAW, "https://github.com/$2/tree/")

private fun String.jsDelivrPath() =
    "(/[^/]+/[^/]+)(?:/blob)?/([^/]+)(/.+)".toRegex().replace(this, "$1@$2$3")

enum class GithubAction(val func: String.() -> String) {
    Mirror(String::githubMirror),
    RAW(String::githubRawUrl),
    RepoUrl(String::githubRepoUrl),
    ;

    fun convert(s: String) = func(s)
}
