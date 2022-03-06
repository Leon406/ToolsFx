package me.leon.ctf

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
    toCharArray().joinToString("") {
        emojiMap[EMOJI_BASE64_DICT.indexOf(it).circleIndex(shift, emojiMap.size)]
    }

fun String.emojiReplaceDecode(shift: Int = 0) =
    toByteArray(Charsets.UTF_32BE)
        .toList()
        .chunked(4)
        .map {
            EMOJI_BASE64_DICT[
                (emojiMap
                    .indexOf(it.toByteArray().toString(Charsets.UTF_32BE))
                    .circleIndex(-shift, emojiMap.size))]
        }
        .joinToString("")

fun Int.circleIndex(shift: Int, length: Int) =
    if (this + shift < 0) (this + shift) % length + length
    else if (this + shift <= length - 1) this + shift else (this + shift) % length
