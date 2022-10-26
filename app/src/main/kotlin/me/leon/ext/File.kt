package me.leon.ext

import java.io.File
import me.leon.encode.base.base64

/** @link https://en.wikipedia.org/wiki/List_of_file_signatures */
val magics =
    mapOf(
        "ffd8ff" to "jpg",
        "60ea" to "arj",
        "785634" to "pbt",
        "aced" to "Serialized Java Data",
        "0000000c6a5020200d0a" to "jp2",
        "89504e47" to "png",
        "474946383761" to "gif",
        "474946383961" to "gif",
        "0001000000" to "ttf",
        "49492a00227105008037" to "tif",
        "424d" to "bmp",
        "edabeedb" to "rpm",
        "41433130313500000000" to "dwg",
        "7b5c727466315c616e73" to "rtf",
        "38425053000100000000" to "psd",
        "46726f6d3a203d3f6762" to "eml",
        "5374616e64617264204a" to "mdb",
        "252150532d41646f6265" to "ps",
        "255044462d312e" to "pdf",
        "2e524d46000000120001" to "rmvb",
        "464c56" to "flv",
        "0000001c66747970" to "mp4",
        "00000020667479706" to "mp4",
        "00000018667479706d70" to "mp4",
        "494433" to "mp3",
        "000001ba210001000180" to "mpg",
        "3026b2758e66cf11a6d9" to "wmv",
        "52494646e27807005741" to "wav",
        "52494646d07d60074156" to "avi",
        "4d546864000000060001" to "mid",
        "526172211a07" to "rar",
        "235468697320636f6e66" to "ini",
        "504b03040a0000080800" to "jar",
        "504b03040a0000000000" to "jar",
        "504b03040a0000080000" to "jar",
        "504b0304140008000800" to "jar",
        "504b0304140008080800" to "jar",
        "504b03040a0008080800" to "jar",
        "d0cf11e0a1b11ae1" to "xls",
        "d0cf11e0a1b11ae1" to "xlsx",
        "504b0304" to "zip",
        "4d5a9000030000000400" to "exe",
        "7f454c46" to "elf",
        "3c25402070616765206c" to "jsp",
        "4d616e69666573742d56" to "mf",
        "7061636b616765207765" to "java",
        "406563686f206f66660d" to "bat",
        "1f8b" to "gz",
        "cafebabe000000" to "class",
        "49545346030000006000" to "chm",
        "04000000010000001300" to "mxp",
        "6431303a637265617465" to "torrent",
        "6d6f6f76" to "mov",
        "ff575043" to "wpd",
        "cfad12fec5fd746f" to "dbx",
        "2142444e" to "pst",
        "ac9ebd8f" to "qdf",
        "e3828596" to "pwl",
        "2e7261fd" to "ram",
        "00000100" to "ico",
        "6465780a30333500" to "dex",
        "377abcaf271c" to "7z",
        "3c3f786d6c20" to "xml",
        "4c0000000" to "lnk",
        "feedfeed" to "jks",
        "52494646" to "webp",
        "1b4c7561" to "luac",
    )

val multiExts = listOf("zip", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "jar", "apk", "exe")

val unsupportedExts = (magics.values + multiExts).toSet()

fun File.realExtension() =
    if (isFile) {
        if (extension.lowercase() == "txt") {
            "txt"
        } else {
            magicNumber().run {
                magics.keys
                    .firstOrNull { this.startsWith(it, true) }
                    ?.let { key ->
                        //                    println(name + " " + key + " " + magics[key])
                        (if (magics[key] in multiExts) {
                            extension.takeIf { it != name } ?: magics[key]
                        } else {
                            magics[key]
                        })
                    }
                    ?: extension.also { println("unknown magic number $this $name") }
            }
        }
    } else {
        "dir"
    }

fun File.magicNumber(bytes: Int = 10) = inputStream().use { it.readNBytes(bytes).toHex() }

fun File.toBase64() = readBytes().base64()

/** 读取文件内容,限制大小 */
fun File.properText(limit: Int = 128 * 1024, hints: String = "") =
    if (length() <= limit) {
        if (realExtension() in unsupportedExts) {
            "unsupported file extension"
        } else {
            readText()
        }
    } else {
        "not support file larger than ${hints.ifEmpty { "128KB" }}"
    }

fun String.toFile() = File(this)
