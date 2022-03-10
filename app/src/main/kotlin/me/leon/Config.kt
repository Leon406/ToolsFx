package me.leon

import java.io.File

const val VERSION = "1.13.0.beta01"
const val BUILD_DATE = "2022-03-10"
const val REPO_URL = "https://github.com/Leon406/ToolsFx"
const val REPO_ISSUE = "https://github.com/Leon406/ToolsFx/issues/new"
const val PJ52_URL = "https://www.52pojie.cn/thread-1501153-1-1.html"
const val LAN_ZOU_DOWNLOAD_URL = "https://leon.lanzoub.com/b0d9av2kb"
const val LAN_ZOU_PLUGIN_DOWNLOAD_URL = "https://leon.lanzoub.com/b0d9w4cof"
const val CHECK_UPDATE_URL =
    "https://ghproxy.com/https://raw.githubusercontent.com/Leon406/ToolsFx/main/current"
const val CHECK_UPDATE_URL2 = "https://gitee.com/LeonShih/ToolsFx/raw/main/current"
const val DEV_UPDATE_URL = "https://raw.fastgit.org/Leon406/ToolsFx/dev/current"
const val DEV_UPDATE_URL2 =
    "https://ghproxy.com/https://raw.githubusercontent.com/Leon406/ToolsFx/dev/current"
const val LICENSE = "https://cdn.staticaly.com/gh/Leon406/ToolsFx/main/LICENSE"
val APP_ROOT: String = File("").absolutePath
val PLUGIN_DIR: String = "$APP_ROOT${File.separatorChar}plugin"
