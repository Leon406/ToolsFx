package me.leon.ctf

import me.leon.encode.base.base36Decode
import me.leon.ext.math.toBigInteger

/**
 * @see { https://www.calcresult.com/misc/cyphers/twin-hex.html }
 * @author Leon
 * @since 2022-10-28 16:32
 */
val cypherBase: List<String> = run {
    val list = mutableListOf<String>()
    for (x in 32 until 128) for (y in 32 until 128) {
        list.add(x.toChar().toString() + y.toChar())
    }
    list
}

fun String.twinHex() =
    chunked(2).joinToString("") {
        cypherBase.indexOf(it.padEnd(2, ' ')).toString(36).padEnd(3, ' ')
    }

fun String.twinHexDecrypt() =
    chunked(3).joinToString("") {
        cypherBase[it.uppercase().base36Decode().toBigInteger().toInt()].trim()
    }
