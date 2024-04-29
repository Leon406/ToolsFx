package me.leon.config

import java.io.File

const val TEXT_AREA_LINES = 8
const val REPO_URL = "https://github.com/Leon406/ToolsFx"
const val REPO_ISSUE = "https://github.com/Leon406/ToolsFx/issues/new"
const val PJ52_URL = "https://www.52pojie.cn/thread-1501153-1-1.html"
const val LAN_ZOU_DOWNLOAD_URL = "https://leon.lanzoub.com/b0d9av2kb?pwd=52pj"
const val LAN_ZOU_PLUGIN_DOWNLOAD_URL = "https://leon.lanzoub.com/b0d9w4cof?pwd=ax63"
const val BAIDU_OCR_APPLY_URL = "https://console.bce.baidu.com/ai/#/ai/ocr/app/list"
const val CHECK_UPDATE_URL =
    "https://ghproxy.net/https://raw.githubusercontent.com/Leon406/ToolsFx/main/update.json"
const val CHECK_UPDATE_URL2 = "https://gitee.com/LeonShih/ToolsFx/raw/main/update.json"
const val DEV_UPDATE_URL =
    "https://ghproxy.net/https://raw.githubusercontent.com/Leon406/ToolsFx/dev/update.json"
const val DEV_UPDATE_URL2 = "https://raw.githubusercontent.com/Leon406/ToolsFx/dev/update.json"
const val LICENSE = "https://raw.githubusercontent.com/Leon406/ToolsFx/main/LICENSE"
const val WIKI_CTF = "https://github.com/Leon406/ToolsFx/wiki/CTF"
const val WIKI = "https://github.com/Leon406/ToolsFx/wiki/Home"
val APP_ROOT: String = File("").absolutePath
val DICT_DIR: String = "$APP_ROOT/dict"
val VOCABULARY_DIR: String = "$APP_ROOT/vocabulary"
