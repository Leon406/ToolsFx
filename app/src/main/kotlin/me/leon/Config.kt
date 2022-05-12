package me.leon

import java.io.File

const val VERSION = "1.13.0"
const val BUILD_DATE = "2022-05-15"
const val REPO_URL = "https://github.com/Leon406/ToolsFx"
const val REPO_ISSUE = "https://github.com/Leon406/ToolsFx/issues/new"
const val LAN_ZOU_DOWNLOAD_URL = "https://leon.lanzoub.com/b0d9av2kb?pwd=52pj"
const val LAN_ZOU_PLUGIN_DOWNLOAD_URL = "https://leon.lanzoub.com/b0d9w4cof?pwd=ax63"
const val CHECK_UPDATE_URL =
    "https://ghproxy.com/https://raw.githubusercontent.com/Leon406/ToolsFx/main/update.json"
const val CHECK_UPDATE_URL2 = "https://gitee.com/LeonShih/ToolsFx/raw/main/update.json"
const val DEV_UPDATE_URL = "https://raw.fastgit.org/Leon406/ToolsFx/dev/update.json"
const val DEV_UPDATE_URL2 =
    "https://ghproxy.com/https://raw.githubusercontent.com/Leon406/ToolsFx/dev/update.json"
const val LICENSE = "https://cdn.staticaly.com/gh/Leon406/ToolsFx/main/LICENSE"
val APP_ROOT: String = File("").absolutePath
val DICT_DIR: String = "$APP_ROOT/dict"
