package me.leon.ctf

import kotlin.test.assertEquals
import me.leon.classical.*
import me.leon.ext.crypto.TABLE_A_Z
import me.leon.ext.crypto.TABLE_A_Z_LOWER
import me.leon.ext.stripAllSpace
import org.junit.Test

class ClassicalTest {
    @Test
    fun caesar() {
        val plain = "hello! yoshiko"
        assertEquals("khoor! brvklnr", plain.shift26(3))
        assertEquals(plain.uppercase(), "KHOOR! BRVKLNR".shift26(23))
        assertEquals(plain.uppercase(), "KHOOR! BRVKLNR".shift26(-3))
        val encrypt = "PELCGBTENCUL"
        for (i in 1..25) {
            println(encrypt.shift26(i))
        }
    }

    @Test
    fun virgenene() {
        val plain = "ATTACKATDAWN"
        val encrypt = "LXFOPVEFRNHR"
        val key = "LEMONLEMONLE"
        assertEquals(encrypt, plain.virgeneneEncode(key))
        assertEquals(plain, encrypt.virgeneneDecode(key))
    }

    @Test
    fun rotTest() {
        val rot13 =
            "introvert at NSA? Va gur ryringbef," + "gur rkgebireg ybbxf ng gur BGURE thl'f fubrf. "
        assertEquals(
            "vagebireg ng AFN? In the elevators,the extrovert looks at the OTHER guy's shoes. ",
            rot13.shift26(13)
        )
        assertEquals(rot13, rot13.shift26(13).shift26(13))

        val rot47 = "The Quick Brown Fox Jumps Over The Lazy Dog."
        assertEquals("%96 \"F:4< qC@H? u@I yF>AD ~G6C %96 {2KJ s@8]", rot47.shift94(47))
        assertEquals(rot47, rot47.shift94(47).shift94(47))

        assertEquals("678sb", "123sb".shift10(5))
        assertEquals("123sb", "123sb".shift10(5).shift10(5))

        assertEquals("678fo", "123sb".rot18())
        assertEquals("678fO", "123sB".rot18())
        assertEquals("123sb", "123sb".rot18().rot18())
    }

    @Test
    fun affine() {
        "AffineCipher".affineEncrypt(5, 8).also {
            assertEquals("IhhwvcSwfrcp", it)
            assertEquals("AffineCipher", it.affineDecrypt(5, 8))
        }

        "aoxL{XaaHKP_tHgwpc_hN_ToXnnht}".affineDecrypt(37, 23, TABLE_A_Z_LOWER + TABLE_A_Z).also {
            println(it)
        }
        "flaG(AffINE_cIpher_iS_ClAssic}".affineEncrypt(37, 23, TABLE_A_Z_LOWER + TABLE_A_Z).also {
            println(it)
        }
    }

    @Test
    fun vig() {
        val key = "LEMONLEMONLE"
        "ATTACKATDAWN".virgeneneEncode(key).also {
            assertEquals("LXFOPVEFRNHR", it)
            assertEquals("ATTACKATDAWN", it.virgeneneDecode(key))
        }
        "ATTACK AT DAWN".virgeneneEncode(key).also {
            assertEquals("LXFOPV EF RNHR", it)
            assertEquals("ATTACK AT DAWN", it.virgeneneDecode(key))
        }
        "AttackAtDawn".virgeneneEncode(key).also {
            assertEquals("LxfopvEfRnhr", it)
            assertEquals("AttackAtDawn", it.virgeneneDecode(key))
        }
    }

    @Test
    fun atbash() {
        "Hello".atBash().also {
            assertEquals("Svool", it)
            assertEquals("Hello", it.atBash())
        }
    }

    @Test
    fun morse() {
        assertEquals(".- - - .- -.-. -.- .- - -.. --- .-- -.", "ATTACKATDOWN".morseEncrypt())
        assertEquals("ATTACKATDOWN", ".- - - .- -.-. -.- .- - -.. --- .-- -.".morseDecrypt())
        assertEquals("MOXIMOXI", "-- --- -..- .. -- --- -..- ..".morseDecrypt())
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
        val msg = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG"
        val encrypted =
            "442315 4145241325 1242345233 213453 2445323543 34511542 442315 31115554 143422"
        assertEquals(encrypted, msg.polybius())
        assertEquals(msg.replace('J', 'I'), encrypted.polybiusDecrypt())
    }

    @Test
    fun tapeCode() {
        val msg = "water"
        val tapCode = "••••• ••   • •   •••• ••••   • •••••   •••• ••"
        val encrypted = "5211441542"

        assertEquals(encrypted, msg.tapCode())
        assertEquals(msg.uppercase(), encrypted.tapCodeDecrypt())
        assertEquals(msg.uppercase(), tapCode.tapCodeDecrypt())
    }

    @Test
    fun bacon() {
        val msg = "Leon 406 Hello"
        var encrypted = "ABABAAABAAABBABABBAA 406 AABBBAABAAABABAABABAABBAB"
        assertEquals(encrypted, msg.baconEncrypt24())
        assertEquals(msg.uppercase(), encrypted.baconDecrypt24())

        encrypted = "ABABBAABAAABBBAABBAB 406 AABBBAABAAABABBABABBABBBA"
        assertEquals(encrypted, msg.baconEncrypt26())
        assertEquals(msg.uppercase(), encrypted.baconDecrypt26())
    }

    @Test
    fun oneTimePad() {
        val key = "MASKL NSFLD FKJPQ"
        val data = "This is an example"
        val encrypted = "FHACTFSSPAFWYAU"
        assertEquals(encrypted, data.oneTimePad(key))
        assertEquals(data.stripAllSpace().uppercase(), encrypted.oneTimePadDecrypt(key))
    }

    @Test
    fun qwe() {
        assertEquals("Hello Leon".stripAllSpace(), "ItssgStgf".qweDecrypt())
        assertEquals("ItssgStgf", "Hello Leon".qweEncrypt())
    }

    @Test
    fun railFence() {
        val msg = "ATTACKATDAWN"
        val encrypt = "AKWTANTTADCA"

        val count = 5

        assertEquals(encrypt, msg.railFenceEncrypt(count))
        assertEquals(msg, encrypt.railFenceDecrypt(count))
        (2 until msg.length).forEach {
            println(it)
            assertEquals(msg, msg.railFenceEncrypt(it).railFenceDecrypt(it))
        }
    }

    @Test
    fun railFenceW() {
        val msg = "ATTACKATDAWN"
        val encrypt = "ACDTAKTANTAW"
        assertEquals(encrypt, msg.railFenceWEncrypt(3))
        assertEquals(msg, encrypt.railFenceWDecrypt(3))
        assertEquals("ATNATCADWTKA", msg.railFenceWEncrypt(3, 1))
        assertEquals(msg, "ATNATCADWTKA".railFenceWDecrypt(3, 1))
    }

    @Test
    fun adfgx() {
        val msg = "implementedByleonJill"
        val table = "phqgmeaynofdxkrcvszwbutil"
        val key = "chinacc"
        val expectedEncrypt = "AADADAXXXAAGFFDGXGXXXDXXXXAXDFXXDGXDADADGX"
        val expectedDecrypt = "IMPLEMENTEDBYLEONIILL"

        assertEquals(expectedEncrypt, msg.adfgx(table, key))
        assertEquals(expectedDecrypt, expectedEncrypt.adfgxDecrypt(table, key))
    }

    @Test
    fun adfgvx() {
        val msg = "implementedByleonJillds123"
        val table = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val key = "chinacc"
        val expectedEncrypt = "FVFVVVDXGVDGFDAAFGDAVFDAGGDFDXVXFXADAXFFAGAAVADADDGV"
        val expectedDecrypt = "IMPLEMENTEDBYLEONJILLDS123"

        assertEquals(expectedEncrypt, msg.adfgvx(table, key))
        assertEquals(expectedDecrypt, expectedEncrypt.adfgvxDecrypt(table, key))
    }

    @Test
    fun autoKey() {
        val msg = "ATTACK AT DAWN"
        val key = "QUEENLY"
        val encrypt = "QNXEPV YT WTWP"
        // 它的密钥开头是一个关键词，之后则是明文的重复
        assertEquals(encrypt, msg.autoKey(key))
        assertEquals(msg, encrypt.autoKeyDecrypt(key))
        assertEquals(encrypt.stripAllSpace(), msg.autoKey(key).stripAllSpace())
        assertEquals(msg.stripAllSpace(), encrypt.stripAllSpace().autoKeyDecrypt(key))
    }

    @Test
    fun playFair() {
        val key = "playfair example".replace(" ", "")
        val msg = "Hide the gold in the tree stump"
        val encrypted = "BM OD ZB XD NA BE KU DM UI XM MO UV IF"
        val encrypted2 = "Bm od zb xd na be ku dm ui Xm mo uv if"

        assertEquals(encrypted, msg.playFair(key).uppercase())
        assertEquals(encrypted2, msg.playFair(key))
        assertEquals(msg.uppercase().stripAllSpace(), encrypted.playFairDecrypt(key))
        assertEquals(msg.stripAllSpace(), encrypted2.playFairDecrypt(key))
    }

    @Test
    fun nihiList() {
        val data = "I am leon thank you for using my software"
        val encoded = "33 2335 13121441 4511234134 541451 311421 5144334132 3554 4414314515232112"
        assertEquals(encoded, data.nihilist("helloworld"))
        assertEquals(data.uppercase(), encoded.nihilistDecrypt("helloworld"))
    }

    @Test
    fun baudot() {
        val encrypted =
            "11001 00011 00111 01001 11000 10000 00100 11001 10101 00100 10010 00001 11000 01100" +
                " 11011 01010 10110 10101"
        val plain = "baudot by leon406"
        assertEquals(encrypted, plain.baudot())
        assertEquals(plain, encrypted.baudotDecode())
    }

    @Test
    fun alphaIndex() {
        val text = "alphabet index"
        assertEquals("1 12 16 8 1 2 5 20 9 14 4 5 24", text.alphabetIndex())
        assertEquals("ALPHABETINDEX", "1 12 16,8;1 2 5 20 9 14 4 5 24".alphabetIndexDecode())
    }

    @Test
    fun gray() {
        val data = "graycode加密"
        data.grayEncode().also { assertEquals(data, it.grayDecode()) }
        data.grayEncode(4).also { assertEquals(data, it.grayDecode(4)) }
        data.grayEncode(5).also { assertEquals(data, it.grayDecode(5)) }
    }

    @Test
    fun hill() {
        var key = "cefjcbdrh"
        var data = "att"
        var encrypted = "pfo"

        assertEquals(encrypted, data.hillEncrypt(key))
        assertEquals(data, encrypted.hillDecrypt(key))

        key = "2  4  5\n" + "9  2  1\n" + "3  17  7"

        assertEquals(encrypted, data.hillEncrypt(key))
        assertEquals(data, encrypted.hillDecrypt(key))

        data = "attackatdown"
        encrypted = "pfogoanpgzbn"

        assertEquals(encrypted, data.hillEncrypt(key))
        assertEquals(data, encrypted.hillDecrypt(key))

        key = "1 2 0 1"
        data = "flagishillissoeapy"
        encrypted = "dloguszijluswogany"

        assertEquals(encrypted, data.hillEncrypt(key, fromZero = false))
        assertEquals(data, encrypted.hillDecrypt(key, fromZero = false))
    }

    @Test
    fun hillCrack() {
        //        val key = "13 6 3 21"
        val data = "dloguszijluswogany".replace("[^a-zA-Z]".toRegex(), "")
        var decrypt = ""
        val flag = "flag"
        val range = 0..25
        label@ for (k1 in range) {
            for (k2 in range) for (k3 in range) for (k4 in range) {
                runCatching { decrypt = data.hillDecrypt("$k1 $k2 $k3 $k4", fromZero = false) }
                if (decrypt.startsWith(flag, true)) {
                    println("$k1 $k2 $k3 $k4 $decrypt")
                    //                            break@label
                }
            }
        }
    }

    @Test
    fun asciiSum() {
        val msg = "flag{8b1330652db0d937e6cdc4d4810118ed_A\$\$CII_and_\$uM}"
        val encoded =
            "0 102 210 307 410 533 589 687 736 787 838 886 940 993 1043 1143 1241 1289 1389 1446 1497 1552 1653" +
                " 1707 1806 1906 2005 2057 2157 2209 2265 2314 2362 2411 2460 2516 2617 2717 2812 2877 2913" +
                " 2949 3016 3089 3162 3257 3354 3464 3564 3659 3695 3812 3889 4014"

        assertEquals(encoded, msg.asciiSum())
        assertEquals(msg, encoded.asciiSumDecode())
    }
}
