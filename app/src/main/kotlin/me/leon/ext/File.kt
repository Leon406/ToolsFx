package me.leon.ext

import java.io.File
import me.leon.ctf.Words
import me.leon.encode.base.base64

/** @link https://en.wikipedia.org/wiki/List_of_file_signatures */
val magics =
    mapOf(
        "ffd8ff" to "jpg",
        "000000246674797068656963" to "heif",
        "000000186674797068656963" to "heif",
        "000000206674797068656963" to "heif",
        "60ea" to "arj",
        "785634" to "pbt",
        "aced" to "Serialized Java Data",
        "0000000c6a5020200d0a" to "jp2",
        "89504e47" to "png",
        "474946383761" to "gif(87a)",
        "474946383961" to "gif(89a)",
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
        "664c6143" to "flac",
        "52494646d07d60074156" to "avi",
        "4d546864000000060001" to "mid",
        "526172211a07" to "rar",
        "235468697320636f6e66" to "ini",
        "504b03040a0000080800" to "jar",
        "504b03040a0000000000" to "xlsx",
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
        "cafebabe00000031" to "class(49,jdk1.5)",
        "cafebabe00000032" to "class(50,jdk1.6)",
        "cafebabe00000033" to "class(51,jdk1.7)",
        "cafebabe00000034" to "class(52,jdk1.8)",
        "cafebabe00000035" to "class(53,jdk9)",
        "cafebabe00000036" to "class(54,jdk10)",
        "cafebabe00000037" to "class(55,jdk11)",
        "cafebabe00000038" to "class(56,jdk12)",
        "cafebabe00000039" to "class(57,jdk13)",
        "cafebabe0000003a" to "class(58,jdk14)",
        "cafebabe0000003b" to "class(59,jdk15)",
        "cafebabe0000003c" to "class(60,jdk16)",
        "cafebabe0000003d" to "class(61,jdk17)",
        "cafebabe0000003e" to "class(62,jdk18)",
        "cafebabe0000003f" to "class(63,jdk19)",
        "cafebabe00000040" to "class(64,jdk20)",
        "cafebabe00000041" to "class(65,jdk21)",
        "cafebabe00000042" to "class(66,jdk22)",
        "cafebabe00000043" to "class(67,jdk23)",
        "cafebabe00000044" to "class(68,jdk24)",
        "cafebabe00000045" to "class(69,jdk25)",
        "cafebabe00000046" to "class(70,jdk26)",
        "cafebabe00000047" to "class(71,jdk27)",
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
        "04224d18" to "lz4",
        "4c5a4950" to "lz",
        "3c3f786d6c20" to "xml",
        "4c0000000" to "lnk",
        "feedfeed" to "jks",
        "52494646" to "webp",
        "1b4c7561" to "luac",
        "d4c3b2a1" to "pcap",
        "a1b2c3d4" to "pcap",
        "4d3cb2a1" to "pcap",
        "a1b23c4d" to "pcap",
        "0a0d0d0a" to "pcapng",
        "2321" to "#! shell",
        "774f4646" to "woff",
        "774f4632" to "woff2",
        "0061736d" to "wasm",
        "4f54544f" to "otf",
        "2321414d52" to "amr",
        "2e736e64" to "snd"
    )

val multiExts = listOf("zip", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "jar", "apk")

val unsupportedExts = (magics.values + multiExts).toSet()

fun File.realExtension() =
    if (isFile) {
        if (extension.lowercase() == "txt") {
            "txt"
        } else {
            magicNumber().run {
                magics.keys
                    .filter { this.startsWith(it, true) }
                    .maxByOrNull { it.length }
                    ?.let { key ->
                        println(name + "magic: $this " + key + " " + magics[key])
                        (if (magics[key] in multiExts) {
                            extension.takeIf { extension in multiExts } ?: magics[key]
                        } else {
                            magics[key]
                        })
                    }
                    ?: "$extension(probably)".also { println("unknown magic number $this $name") }
            }
        }
    } else {
        "dir"
    }

fun File.magicNumber(bytes: Int = 12) =
    inputStream().use {
        val b = ByteArray(bytes)
        it.read(b)
        b.toHex()
    }

fun File.toBase64() = readBytes().base64()

/** 读取文件内容,限制大小 */
fun File.properText(limit: Int = 128 * 1024, hints: String = "") =
    if (length() <= limit) {
        val ext = realExtension()
        println("-- $ext")
        if (unsupportedExts.contains(ext.replace("(probably)", ""))) {
            "unsupported extension $ext"
        } else {
            readText()
        }
    } else {
        "not support file larger than ${hints.ifEmpty { "128KB" }}, extension ${realExtension()}"
    }

fun String.toFile() = File(this)

fun readResourceText(path: String) =
    Words.javaClass.getResourceAsStream(path)?.reader()?.readText().orEmpty()

inline fun <reified T> readRes(path: String) =
    T::class.java.getResourceAsStream(path)?.reader()?.readText().orEmpty()
