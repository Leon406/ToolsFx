package me.leon.ctf

import me.leon.ext.math.circleIndex

/** emoji-aes */
val emojiMap =
    listOf(
        "🍎",
        "🍌",
        "🏎",
        "🚪",
        "👁",
        "👣",
        "😀",
        "🖐",
        "ℹ",
        "😂",
        "🥋",
        "✉",
        "🚹",
        "🌉",
        "👌",
        "🍍",
        "👑",
        "👉",
        "🎤",
        "🚰",
        "☂",
        "🐍",
        "💧",
        "✖",
        "☀",
        "🦓",
        "🏹",
        "🎈",
        "😎",
        "🎅",
        "🐘",
        "🌿",
        "🌏",
        "🌪",
        "☃",
        "🍵",
        "🍴",
        "🚨",
        "📮",
        "🕹",
        "📂",
        "🛩",
        "⌨",
        "🔄",
        "🔬",
        "🐅",
        "🙃",
        "🐎",
        "🌊",
        "🚫",
        "❓",
        "⏩",
        "😁",
        "😆",
        "💵",
        "🤣",
        "☺",
        "😊",
        "😇",
        "😡",
        "🎃",
        "😍",
        "✅",
        "🔪",
        "🗒"
    )
const val EMOJI_BASE64_DICT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+/="

fun String.emojiReplace(shift: Int = 0) =
    asIterable().joinToString("") {
        emojiMap[EMOJI_BASE64_DICT.indexOf(it).circleIndex(emojiMap.size, shift)]
    }

fun String.emojiReplaceDecode(shift: Int = 0) =
    toByteArray(Charsets.UTF_32BE)
        .asIterable()
        .chunked(4)
        .map {
            EMOJI_BASE64_DICT[
                (emojiMap
                    .indexOf(it.toByteArray().toString(Charsets.UTF_32BE))
                    .circleIndex(emojiMap.size, -shift))]
        }
        .joinToString("")
