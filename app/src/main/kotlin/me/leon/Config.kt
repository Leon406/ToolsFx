package me.leon

import java.io.File

const val VERSION = "1.10.1"
const val REPO_URL = "https://github.com/Leon406/ToolsFx"
const val PJ52_URL = "https://www.52pojie.cn/thread-1501153-1-1.html"
const val LAN_ZOU_DOWNLOAD_URL = "https://leon.lanzoui.com/b0d9av2kb"
const val CHECK_UPDATE_URL =
    "https://ghproxy.com/https://raw.githubusercontent.com/Leon406/ToolsFx/main/current"
const val CHECK_UPDATE_URL2 = "https://gitee.com/LeonShih/ToolsFx/raw/main/current"
const val DEV_UPDATE_URL = "https://raw.fastgit.org/Leon406/ToolsFx/dev/current"
const val DEV_UPDATE_URL2 =
    "https://ghproxy.com/https://raw.githubusercontent.com/Leon406/ToolsFx/dev/current"
const val LICENSE = "https://cdn.staticaly.com/gh/Leon406/ToolsFx/main/LICENSE"
val APP_ROOT: String = File("").absolutePath
val PLUGIN_DIR: String = "$APP_ROOT${File.separatorChar}plugin"
