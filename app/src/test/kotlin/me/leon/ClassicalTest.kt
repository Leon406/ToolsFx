package me.leon

import kotlin.test.assertEquals
import me.leon.classical.*
import me.leon.ctf.*
import me.leon.encode.base.base64
import me.leon.encode.base.base64Decode
import org.junit.Test
import java.math.BigInteger

class ClassicalTest {
    @Test
    fun caesar() {
        "ATTACKATDAWN".virgeneneEncode("LEMONLEMONLE").also { println(it) }
        "CRYPTO IS SHORT FOR CRYPTOGRAPHY".virgeneneEncode("ABCDEF AB CDEFA BCD EFABCDEFABCD")
            .also { println(it) }
        "LXFOPVEFRNHR".virgeneneDecode("LEMONLEMONLE").also { println(it) }

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
        println("ATTACKATDOWN".morseEncrypt())

        println(".- - - .- -.-. -.- .- - -.. --- .-- -.".morseDecrypt())
        println("-- --- -..- .. -- --- -..- ..".morseDecrypt())
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
        println(
            "442315 4145241325 1242345233 213453 2445323543 34511542 442315 31115554 143422".polybiusDecrypt()
        )
    }

    @Test
    fun bacon() {
        println("Leon 406 Hello".baconEncrypt24())
        println("ABABAAABAAABBABABBAA 406 AABBBAABAAABABAABABAABBAB".baconDecrypt24())
        println("Leon 406 Hhaha".baconEncrypt26())
        println("ABABBAABAAABBBAABBAB".baconDecrypt26())
        println("ABABBAABAAABBBAABBAB 406 AABBBAABBBAAAAAAABBBAAAAA".baconDecrypt26())
    }

    @Test
    fun oneTimePad() {
        val key = "MASKL NSFLD FKJPQ"
        val data = "This is an example"
        val encrypted = "FHACTFSSPAFWYAU"
        // check length
        val k2 = key.filter { it.isLetter() }.uppercase()
        val d2 = data.filter { it.isLetter() }.uppercase()
        val isSameSize = k2.length == d2.length
        println(isSameSize)
        // do add
        d2
            .mapIndexed { index, c -> 'A' + (c.code + k2[index].code - 130) % 26 }
            .joinToString("")
            .also { println(it) }

        encrypted
            .mapIndexed { index, c -> 'A' + (c.code - k2[index].code + 26) % 26 }
            .joinToString("")
            .also { println(it) }

        println()
        println(data.oneTimePad(key))
        println(encrypted.oneTimePadDecrypt(key))
    }

    @Test
    fun qwe() {
        println("Hello Leon".qweEncrypt())
        println("ITSSGSTGF".qweDecrypt())
        println("QWERTYUIOP".qweDecrypt())
        println("ASDFGHJKL".qweDecrypt())
        println("ZXCVBNM ".qweDecrypt())
    }

    @Test
    fun b100() {
        val s = "hello开发工具箱".toByteArray()
        val encoded = s.base100()
        println(encoded)
        println(encoded.base100Decode2String())
    }

    @Test
    fun coreValues() {
        println("hello开发工具箱".socialistCoreValues())
        ("公正爱国公正平等公正诚信文明公正诚信文明公正诚信平等友善爱国平等诚信民主诚信文明爱国富强友善爱国平等爱国诚信平等敬业民主诚信自由平等" +
                "友善平等法治诚信富强平等友善爱国平等爱国平等诚信民主法治诚信自由法治友善自由友善爱国友善平等民主")
            .socialistCoreValuesDecrypt()
            .also { println(it) }
    }

    @Test
    fun railFence() {
        val msg = "ATTACKATDAWN"
        val count = 5
        println(msg.railFenceEncrypt(count))

        val encrypt = "AKWTANTT@AD@CA@"
        println(encrypt.railFenceDecrypt(5))
        assertEquals(msg, encrypt.railFenceDecrypt(5))
    }

    @Test
    fun railFenceW() {
        val msg = "ATTACKATDAWN"
        repeat(11) {
            println("${it + 2}")
            val message = msg.railFenceWEncrypt(it + 2)
            println(message)
            println(message.railFenceWDecrypt(it + 2))
        }
    }

    @Test
    fun brainFuck() {
        val data =
            "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook! Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook! Ook! Ook! Ook! " +
                "Ook! Ook! Ook? Ook. Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook! Ook? Ook. " +
                "Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook! Ook! Ook! " +
                "Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook! " +
                "Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook? Ook. " +
                "Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! " +
                "Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! " +
                "Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook. Ook! Ook. " +
                "Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook! " +
                "Ook! Ook! Ook! Ook! Ook! Ook? Ook. Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! " +
                "Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? " +
                "Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook? " +
                "Ook. Ook? Ook! Ook. Ook? Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! " +
                "Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook! Ook. Ook. Ook. Ook! Ook. Ook. Ook. Ook! Ook. Ook. Ook. Ook! Ook. " +
                "Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! " +
                "Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook! Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook! Ook. Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook! Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook! Ook? Ook! Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook? Ook. Ook? Ook! Ook. Ook? Ook. Ook. Ook. Ook. Ook. Ook. Ook. Ook. " +
                "Ook. Ook. Ook. Ook. Ook! Ook. Ook? Ook."
        println(data)
        val ookEngine = OokEngine(8)
        println(ookEngine.interpret(data))
        println(ookEngine.interpret(data))
        val bf = "++++++++++[>+++++++++>++++++++>+++++++<<<-]>---.>+.---.-.-.>+.-----."

        println(BrainfuckEngine(1024).interpret(bf))
        val troll =
            "Trooloolooloolooloolooloolooloolollooooolooloolooloolooloolooooolooloolooloolooloolo" +
                "oloolooloooooloolooloooooloooloolooloololllllooooloololoooooololooolooloolooloolooloololool" +
                "ooolooloololooooooloololooooloololooloolooloolooloolooloolooloolooloolooloololoooooloooloolool" +
                "olooollollollollollolllooollollollollollollollollloooooololooooolooll"
        println(troll)
        println(TrollScriptEngine(1024).interpret(troll))
    }

    @Test
    fun adfgx() {
        val msg = "implementedByleonJill".replace("\\s".toRegex(), "")
        var table = "phqgmeaynofdxkrcvszwbutil"
        val key = "chinacc".toList().distinct().joinToString("")
        val encrypted = msg.adfgx(table, key)
        println(encrypted)
        println(encrypted.adfgxDecrypt(table, key))
    }

    @Test
    fun adfgvx() {
        val msg = "implementedByleonJillds123".replace("\\s".toRegex(), "")
        var table = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val key = "chinacc".toList().distinct().joinToString("")
        val encrypted = msg.adfgvx(table, key)
        println(encrypted)
        println(encrypted.adfgvxDecrypt(table, key))
    }

    @Test
    fun autoKey() {
        val msg = "ATTACK AT DAWN"
        val key = "QUEENLY"
        // 它的密钥开头是一个关键词，之后则是明文的重复
        println(msg.autoKey(key))
        println("QNXEPVYTWTWP".autoKeyDecrypt(key))
        println("QNXEPV YT WTWP".autoKeyDecrypt(key))
    }

    @Test
    fun playFair() {
        val key = "playfair example".replace(" ", "")
        var msg = "Hide the gold in the tree stump"
        println(msg.playFair(key))
        println("BMODZBXDNABEKUDMUIMXMOVUIFYE".playFairDecrypt(key))
        println("BM OD ZB XD NA BE KU DM UI MX MO VU IF YE".playFairDecrypt(key))
    }

    @Test
    fun nihiList() {
        "I am leon thank you for using my software".nihilist("helloworld").also { println(it) }
        "33 2335 13121441 4511234134 541451 311421 5144334132 3554 4414314515232112"
            .nihilistDecrypt("helloworld")
            .also { println(it) }
    }

    @Test
    fun blind() {
        var d =
            "⡥⠂⡶⡃⡔⡷⡦⡛⡨⠁⠟⡚⠉⠇⡳⡜⡉⡤⡴⡑⡓⡆⡑⡔⡆⡠⡩⡹⠂⡢⡪⡵⡢⡟⡶⡹⠃⡒⠁⡥⡞⠟⡚⡞⡣⡣⡤⡀⡡⡆⠉⡼⡻⠀⠉⡧⡙⠇⡦⡇⡧⡅⡺⡑⠺⡑⡉⡑⠂⡞⡱⡳⠁" +
                "⡊⡢⡩⡊⡚⡊⡕⡛⠀⡕⠂⡩⡱⡾⡴⠂⡶⡛⠈⡹⡇⡗⡑⠃⠁⡆⡝⡽⡺⡨⡙⠛⠅⠁⡠⡇⡩⡅⡸⡑⡧⡑⡸⠅⡆⡨⠛⡣⡨⡑⡢⡝⠁⡟⡚⡿⠺⠛⡿⡕⡴⡛⡡⠀⡔⠉" +
                "⠂⡴⡃⠃⠀⡿⡹⠄⡺⡀⡵⡊⡝⡪⡨⡛⡦⡖⡛⡧⡡⡪⠈⡲⠟⡝⡔⡕⠅⡄⡞⠟⠂⡵⡉⠅⡩⡦⡼⡈⡴⡩⡈⠟⡞⡦⡩⡆⡛⡴⡾⡈⡁⡁⡗⠺⡹⡾⡆⡢⡹⡠⡈⡃⡛⠆" +
                "⡁⡖⡻⡉⡡⡻⡓⠆⡁⡼⡷⠃⡛⠅⡵⠈⡝⡂⠉⡃⡄⡠⡠⡡⡒⡁⡃⡁⠅⡾⡨⠆⡘⠇⡄⡁⡲⠅⡖⠛⡓⡤⡃⡕⡺⡃⡝⡛⡳⠀⡢⡒⡙⠂⠺⡱⡉⡻⡒⡨⡄⡒⡒⡈⡱⡧⡽" +
                "⠆⡉⡷⡹⠛⡊⠟⡥⡜⡳⡶⠆⡺⠉⠂⡂⡛⡥⡓⡝⡴⠆⡽⡟⠅⡿⡻⡸⡺⠆⡇⠂⠈⡼⡤⡕⠂⠈⡤⠅⠛⠁⡇⡟⡧⡈⡗⡲⡊⡸⠉⡻⠺⡱⡻⡥⠍="
        println(d.blindDecode())
        println("abdcd".blindEncode())
    }

    @Test
    fun baudot() {
        "adfdad12314sadf/.,,".baudot().also { println(it) }.also { println(it.baudotDecode()) }
    }

    @Test
    fun alphaIndex() {
        "alphabet index".alphabetIndex().also { println(it) }
        "1 12 16,8;1 2 5 20 9 14 4 5 24".alphabetIndexDecode().also { println(it) }
    }
    @Test
    fun zero1234() {
        "alphabet index".zero1248().also { println(it) }
        "8842101220480224404014224202480122".zero1248Decode().also { println(it) }
    }
}
