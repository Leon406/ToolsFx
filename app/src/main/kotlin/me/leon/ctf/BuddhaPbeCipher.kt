package me.leon.ctf

import me.leon.ext.crypto.PBE

/**
 * @author Leon
 * @since 2023-05-26 9:37
 * @email deadogone@gmail.com
 */
private const val KEY = "takuron.top"
private const val ALG = "PBEWithMD5and256bitAES-CBC-OPENSSL"
private val encode =
    mapOf(
        'A' to '埵',
        'B' to '孕',
        'C' to '唵',
        'D' to '呼',
        'E' to '羯',
        'F' to '蒙',
        'G' to '沙',
        'H' to '谨',
        'I' to '吉',
        'J' to '度',
        'K' to '俱',
        'L' to '尼',
        'M' to '喝',
        'N' to '佛',
        'O' to '迦',
        'P' to '豆',
        'Q' to '地',
        'R' to '墀',
        'S' to '驮',
        'T' to '提',
        'U' to '伊',
        'V' to '伽',
        'W' to '烁',
        'X' to '阇',
        'Y' to '他',
        'Z' to '遮',
        'a' to '摩',
        'b' to '无',
        'c' to '陀',
        'd' to '阿',
        'e' to '啰',
        'f' to '醯',
        'g' to '罚',
        'h' to '那',
        'i' to '耶',
        'j' to '哆',
        'k' to '怛',
        'l' to '萨',
        'm' to '卢',
        'n' to '娑',
        'o' to '诃',
        'p' to '南',
        'q' to '室',
        'r' to '悉',
        's' to '夜',
        't' to '婆',
        'u' to '唎',
        'v' to '菩',
        'w' to '帝',
        'x' to '皤',
        'y' to '嚧',
        'z' to '利',
        '0' to '穆',
        '1' to '参',
        '2' to '舍',
        '3' to '苏',
        '4' to '钵',
        '5' to '曳',
        '6' to '数',
        '7' to '写',
        '8' to '栗',
        '9' to '楞',
        '+' to '咩',
        '/' to '输',
        '=' to '漫',
    )

private val decode = encode.values.zip(encode.keys).toMap()

fun String.buddhaPbe(key: String = KEY): String {
    return "佛又曰：" +
        PBE.encrypt(key.ifEmpty { KEY }, this, PBE.getSalt(), ALG)
            .substring(10)
            .map { encode[it] }
            .joinToString("")
}

fun String.buddhaPbeDecrypt(pass: String = KEY): String {
    return PBE.decrypt(
        pass.ifEmpty { KEY },
        "U2FsdGVkX1" + substringAfter("：").map { decode[it] }.joinToString(""),
        8,
        ALG
    )
}
