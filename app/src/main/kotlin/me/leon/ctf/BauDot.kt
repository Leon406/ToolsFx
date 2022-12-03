package me.leon.ctf

import me.leon.ext.splitBySpace

val letterDecodeDict =
    mapOf(
        "00000" to "\u0001",
        "00001" to "E",
        "00010" to "\n",
        "00011" to "A",
        "00100" to " ",
        "00101" to "S",
        "00110" to "I",
        "00111" to "U",
        "01000" to "\r",
        "01001" to "D",
        "01010" to "R",
        "01011" to "J",
        "01100" to "N",
        "01101" to "F",
        "01110" to "C",
        "01111" to "K",
        "10000" to "T",
        "10001" to "Z",
        "10010" to "L",
        "10011" to "W",
        "10100" to "H",
        "10101" to "Y",
        "10110" to "P",
        "10111" to "Q",
        "11000" to "O",
        "11001" to "B",
        "11010" to "G",
        "11011" to "Figures",
        "11100" to "M",
        "11101" to "X",
        "11110" to "V",
        "11111" to "Letters",
    )

val letterEncodeMap = letterDecodeDict.entries.associate { it.value to it.key }

const val FIGURE = "11011"
const val LETTER = "11111"
val figureDecodeDict =
    mapOf(
        "00000" to "\u0001",
        "00001" to "3",
        "00010" to "\n",
        "00011" to "-",
        "00100" to " ",
        "00101" to "'",
        "00110" to "8",
        "00111" to "7",
        "01000" to "\r",
        "01001" to "\u0005",
        "01010" to "4",
        "01011" to "\u0007",
        "01100" to ",",
        "01101" to "!",
        "01110" to ":",
        "01111" to "(",
        "10000" to "5",
        "10001" to "+",
        "10010" to ")",
        "10011" to "2",
        "10100" to "$",
        "10101" to "6",
        "10110" to "0",
        "10111" to "1",
        "11000" to "9",
        "11001" to "?",
        "11010" to "^",
        "11011" to "Figures",
        "11100" to ".",
        "11101" to "/",
        "11110" to ";",
        "11111" to "Letters",
    )
val figureEncodeMap = figureDecodeDict.entries.associate { it.value to it.key }

fun String.baudot(): String {
    var isLetter = true
    return asIterable().joinToString(" ") {
        if (isLetter) {
            letterEncodeMap[it.toString().uppercase()]
                ?: (FIGURE + " " + figureEncodeMap[it.toString()]).also { isLetter = false }
        } else {
            figureEncodeMap[it.toString()]
                ?: (LETTER + " " + letterEncodeMap[it.toString().uppercase()]).also {
                    isLetter = true
                }
        }
    }
}

fun String.baudotDecode(): String {
    var isLetter = true
    return splitBySpace()
        .filter { it.matches("[01]+".toRegex()) }
        .asSequence()
        .map {
            if (it == LETTER || it == FIGURE) {
                isLetter = it == LETTER
                ""
            } else {
                if (isLetter) {
                    letterDecodeDict[it]
                } else {
                    figureDecodeDict[it]
                }
            }
        }
        .joinToString("")
        .lowercase()
}
