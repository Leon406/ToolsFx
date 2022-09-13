package me.leon.classical

import me.leon.ext.crypto.TABLE_A_Z
import me.leon.ext.letters

/** ported from https://github.com/jameslyons/pycipher/blob/master/pycipher/porta.py */
fun String.porta(key: String = "FORTIFICATION"): String {
    val properKey = key.letters().uppercase()
    return letters()
        .uppercase()
        .foldIndexed(StringBuilder()) { index, acc, char ->
            acc.append(
                when (properKey[index % properKey.length]) {
                    'A',
                    'B' -> "NOPQRSTUVWXYZABCDEFGHIJKLM"[TABLE_A_Z.indexOf(char)]
                    'Y',
                    'Z' -> "ZNOPQRSTUVWXYBCDEFGHIJKLMA"[TABLE_A_Z.indexOf(char)]
                    'W',
                    'X' -> "YZNOPQRSTUVWXCDEFGHIJKLMAB"[TABLE_A_Z.indexOf(char)]
                    'U',
                    'V' -> "XYZNOPQRSTUVWDEFGHIJKLMABC"[TABLE_A_Z.indexOf(char)]
                    'S',
                    'T' -> "WXYZNOPQRSTUVEFGHIJKLMABCD"[TABLE_A_Z.indexOf(char)]
                    'Q',
                    'R' -> "VWXYZNOPQRSTUFGHIJKLMABCDE"[TABLE_A_Z.indexOf(char)]
                    'O',
                    'P' -> "UVWXYZNOPQRSTGHIJKLMABCDEF"[TABLE_A_Z.indexOf(char)]
                    'M',
                    'N' -> "TUVWXYZNOPQRSHIJKLMABCDEFG"[TABLE_A_Z.indexOf(char)]
                    'K',
                    'L' -> "STUVWXYZNOPQRIJKLMABCDEFGH"[TABLE_A_Z.indexOf(char)]
                    'I',
                    'J' -> "RSTUVWXYZNOPQJKLMABCDEFGHI"[TABLE_A_Z.indexOf(char)]
                    'G',
                    'H' -> "QRSTUVWXYZNOPKLMABCDEFGHIJ"[TABLE_A_Z.indexOf(char)]
                    'E',
                    'F' -> "PQRSTUVWXYZNOLMABCDEFGHIJK"[TABLE_A_Z.indexOf(char)]
                    'C',
                    'D' -> "OPQRSTUVWXYZNMABCDEFGHIJKL"[TABLE_A_Z.indexOf(char)]
                    else -> ""
                }
            )
        }
        .toString()
}
