package me.leon

import me.leon.classical.*
import org.junit.Test

class ClassicalTest {
    @Test
    fun caesar() {
        val plain = "hello! yoshiko"
        println(plain.shift26(26))
        val encrypt = "PELCGBTENCUL"
        for (i in 1..25) {
            println(encrypt.shift26(i))
        }
    }

    @Test
    fun rotTest() {
        val rot13 =
            "How can you tell an extrovert from an\n" +
                    "introvert at NSA? Va gur ryringbef,\n" +
                    "gur rkgebireg ybbxf ng gur BGURE thl'f fubrf. "

        println(rot13)

        println(rot13.shift26(13).also { println(it) }.shift26(13))

        val rot47 = "The Quick Brown Fox Jumps Over The Lazy Dog."
        println(rot47.shift94(47).shift94(47))

        val dd = "ROT5/13/18/47 is the easiest and yet powerful cipher!"
        println(dd.shift10(5))
        println(dd.rot18())
        println(dd.shift26(13))
        println(dd.shift94(47))

        "123sb".shift10(5).also { println(it) }
        "123sb".rot18().also { println(it) }
    }

    @Test
    fun affine() {
        "AFFINECIPHER".affineEncrypt(5, 8).also {
            println(it)
            println(it.affineDecrypt(5, 8))
        }
    }

    @Test
    fun vig() {
        "ATTACKATDAWN".virgeneneEncode("LEMONLEMONLE").also { println(it) }
        "CRYPTO IS SHORT FOR CRYPTOGRAPHY".virgeneneEncode("ABCDEF AB CDEFA BCD EFABCDEFABCD")
            .also { println(it) }
        "LXFOPVEFRNHR".virgeneneDecode("LEMONLEMONLE").also { println(it) }
    }

    @Test
    fun atbash() {
        "Hello".atBash().also {
            println(it)
            println(it.atBash())
        }
    }

    @Test
    fun morse() {
        println("Leon406".morseEncrypt())
        println(".-.. . --- -. ....- ----- -....".morseDecrypt())
    }

    @Test
    fun mapReverse() {
        val map = mapOf(1 to 100, 2 to 200, 3 to 300)
        val mapRe = mutableMapOf<Int, Int>()
        mapRe.putAll(map.values.zip(map.keys))
        println(map)
        println(mapRe)
    }

    @Test
    fun polybius() {
        println("THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG".polybius())
        println("442315 4145241325 1242345233 213453 2445323543 34511542 442315 31115554 143422".polybiusDecrypt())

    }

    @Test
    fun bacon() {
        println("Leon 406 Hello".baconEncrypt24())
        println("ABABAAABAAABBABABBAA 406 AABBBAABAAABABAABABAABBAB".baconDecrypt24())
        println("Leon 406 Hhaha".baconEncrypt26())
        println("ABABBAABAAABBBAABBAB".baconDecrypt26())
        println("ABABBAABAAABBBAABBAB 406 AABBBAABBBAAAAAAABBBAAAAA".baconDecrypt26())
    }
}
