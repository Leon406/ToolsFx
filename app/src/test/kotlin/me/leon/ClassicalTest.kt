package me.leon

import kotlin.test.assertEquals
import me.leon.classical.*
import me.leon.ctf.*
import me.leon.encode.base.base100
import me.leon.encode.base.base100Decode2String
import me.leon.ext.stripAllSpace
import org.junit.Test

class ClassicalTest {
    @Test
    fun caesar() {

        val plain = "hello! yoshiko"
        println(plain.shift26(3))
        assertEquals("KHOOR! BRVKLNR", plain.shift26(3))
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
            assertEquals("IHHWVCSWFRCP", it)
            assertEquals("AFFINECIPHER", it.affineDecrypt(5, 8))
        }
    }

    @Test
    fun vig() {
        "ATTACKATDAWN".virgeneneEncode("LEMONLEMONLE").also { println(it) }
        "CRYPTO IS SHORT FOR CRYPTOGRAPHY".virgeneneEncode("LEON").also { println(it) }
        "LXFOPVEFRNHR".virgeneneDecode("LEMONLEMONLE").also { println(it) }
    }

    @Test
    fun atbash() {
        "Hello".atBash().also {
            assertEquals("SVOOL", it)
            assertEquals("Hello".uppercase(), it.atBash())
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
        val encrypted = "442315 4145241325 1242345233 213453 2445323543 34511542 442315 31115554 143422"
        assertEquals(encrypted, msg.polybius())
        assertEquals(msg.replace('J', 'I'), encrypted.polybiusDecrypt())
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
        assertEquals("Hello Leon".stripAllSpace().uppercase(), "ITSSGSTGF".qweDecrypt())
        assertEquals("ITSSGSTGF", "Hello Leon".qweEncrypt())
    }

    @Test
    fun b100() {
        val s = "hello开发工具箱".toByteArray()
        val encoded = s.base100()
        assertEquals("👟👜👣👣👦🏜🎳🍷🏜🎆🎈🏜🎮🎜🏜🍼🎮🏞🎥🎨", s.base100())
        assertEquals("hello开发工具箱", encoded.base100Decode2String())
    }

    @Test
    fun coreValues() {
        println("hello开发工具箱".socialistCoreValues())
        ("公正爱国公正平等公正诚信文明公正诚信文明公正诚信平等友善爱国平等诚信民主诚信文明爱国富强友善爱国平等爱国诚信平等敬业民主诚信自由平等" +
                "友善平等法治诚信富强平等友善爱国平等爱国平等诚信民主法治诚信自由法治友善自由友善爱国友善平等民主")
            .socialistCoreValuesDecrypt()
            .also { assertEquals("hello开发工具箱", it) }
    }

    @Test
    fun railFence() {
        val msg = "ATTACKATDAWN"
        val encrypt = "AKWTANTT@AD@CA@"
        val count = 5
        assertEquals(encrypt, msg.railFenceEncrypt(count))
        assertEquals(msg, encrypt.railFenceDecrypt(5))
    }

    @Test
    fun railFenceW() {
        val msg = "ATTACKATDAWN"
        val encrypt = "ACDTAKTANTAW"
        assertEquals(encrypt, msg.railFenceWEncrypt(3))
        assertEquals(msg, encrypt.railFenceWDecrypt(3))
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
        val ookEngine = OokEngine(8)
        assertEquals("flag{ok-ctf-1234-admin}",ookEngine.interpret(data))

        val bf = "++++++++++[>+++++++++>++++++++>+++++++<<<-]>---.>+.---.-.-.>+.-----."
        assertEquals("WQNMLGB",BrainfuckEngine(1024).interpret(bf))


        val troll =
            "Trooloolooloolooloolooloolooloolollooooolooloolooloolooloolooooolooloolooloolooloolo" +
                    "oloolooloooooloolooloooooloooloolooloololllllooooloololoooooololooolooloolooloolooloololool" +
                    "ooolooloololooooooloololooooloololooloolooloolooloolooloolooloolooloolooloololoooooloooloolool" +
                    "olooollollollollollolllooollollollollollollollollloooooololooooolooll"
        assertEquals("Hello World!\n",TrollScriptEngine(1024).interpret(troll))
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
        println(msg.autoKey(key))
        assertEquals(encrypt, msg.autoKey(key))
        assertEquals(msg, encrypt.autoKeyDecrypt(key))
        assertEquals(encrypt.stripAllSpace(), msg.autoKey(key).stripAllSpace())
        assertEquals(msg.stripAllSpace(), encrypt.stripAllSpace().autoKeyDecrypt(key))
    }

    @Test
    fun playFair() {
        val key = "playfair example".replace(" ", "")
        val msg = "Hide the gold in the tree stump"
        val encrypted = "BM OD ZB XD NA BE KU DM UI MX MO VU IF"
        assertEquals(encrypted,msg.playFair(key))
        assertEquals(msg.uppercase().stripAllSpace(),encrypted.playFairDecrypt(key))
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
        val d =
            "⡥⠂⡶⡃⡔⡷⡦⡛⡨⠁⠟⡚⠉⠇⡳⡜⡉⡤⡴⡑⡓⡆⡑⡔⡆⡠⡩⡹⠂⡢⡪⡵⡢⡟⡶⡹⠃⡒⠁⡥⡞⠟⡚⡞⡣⡣⡤⡀⡡⡆⠉⡼⡻⠀⠉⡧⡙⠇⡦⡇⡧⡅⡺⡑⠺⡑⡉⡑⠂⡞⡱⡳⠁" +
                    "⡊⡢⡩⡊⡚⡊⡕⡛⠀⡕⠂⡩⡱⡾⡴⠂⡶⡛⠈⡹⡇⡗⡑⠃⠁⡆⡝⡽⡺⡨⡙⠛⠅⠁⡠⡇⡩⡅⡸⡑⡧⡑⡸⠅⡆⡨⠛⡣⡨⡑⡢⡝⠁⡟⡚⡿⠺⠛⡿⡕⡴⡛⡡⠀⡔⠉" +
                    "⠂⡴⡃⠃⠀⡿⡹⠄⡺⡀⡵⡊⡝⡪⡨⡛⡦⡖⡛⡧⡡⡪⠈⡲⠟⡝⡔⡕⠅⡄⡞⠟⠂⡵⡉⠅⡩⡦⡼⡈⡴⡩⡈⠟⡞⡦⡩⡆⡛⡴⡾⡈⡁⡁⡗⠺⡹⡾⡆⡢⡹⡠⡈⡃⡛⠆" +
                    "⡁⡖⡻⡉⡡⡻⡓⠆⡁⡼⡷⠃⡛⠅⡵⠈⡝⡂⠉⡃⡄⡠⡠⡡⡒⡁⡃⡁⠅⡾⡨⠆⡘⠇⡄⡁⡲⠅⡖⠛⡓⡤⡃⡕⡺⡃⡝⡛⡳⠀⡢⡒⡙⠂⠺⡱⡉⡻⡒⡨⡄⡒⡒⡈⡱⡧⡽" +
                    "⠆⡉⡷⡹⠛⡊⠟⡥⡜⡳⡶⠆⡺⠉⠂⡂⡛⡥⡓⡝⡴⠆⡽⡟⠅⡿⡻⡸⡺⠆⡇⠂⠈⡼⡤⡕⠂⠈⡤⠅⠛⠁⡇⡟⡧⡈⡗⡲⡊⡸⠉⡻⠺⡱⡻⡥⠍="

        val plain =
            "U2FsdGVkX1/j97ClyTDacvadvPYI2RZERoFI3b1Un/jnSSTpQv9LK09Wi7VwWuJa\r\n" +
                    "aya2nAC1zRYzjzek0e2YAND2Fk8Iwga31vmMJXi+51PwYuHaWaH5vX+SXaRm1ojO\r\n" +
                    "+OeDkQ0d92Ds30OI4JpEzmZXkVfkWQZ8B/mde5tn/2Ey5YVLxDYx/nVYvkDNxqqg\r\n" +
                    "INvRIPxsk6qfKyQKc6qLG3k5E8mr9stPPQbqsq5NX6h7tqB5f+cTseJsmkC0Rbi2\r\n" +
                    "AyKbXtbbxAWM6yGI+z/UlCF6J92rkUcmD6Mo5OKHJ6w28LTe28T5+1woWxgBzH9K\r\n" +
                    "AKU="

        assertEquals(plain, d.blindDecode())
        assertEquals("⡑⡒⡔⡓⡔=", "abdcd".blindEncode())
    }

    @Test
    fun elementPeriod() {
        val d = "No Hs Bk Lr Db Uup Lr Rg Rg Fm"
        assertEquals("flagisgood", d.elementPeriodDecode())
        val plain = "periodic element table"
        val encrypted = "Cn Md Fl Db Rg Fm Db Es Ge Md Hs Md Mt Md Ds Lv Ge Lv Bk Cf Hs Md"
        assertEquals(encrypted, plain.elementPeriodEncode())
        assertEquals(plain, encrypted.elementPeriodDecode())
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
    fun zero1234() {
        assertEquals("108408808010204108840810842040410888", "alphabet index".zero1248())
        assertEquals("WELLDONE", "8842101220480224404014224202480122".zero1248Decode())
    }

    @Test
    fun zwc() {
        val d = "w\u200D\uFEFF\u200C\u200B\u200D\uFEFF\u200D\u200B\u200D\uFEFF\uFEFFhat"
        assertEquals(d, "abc".zwc("what"))
        assertEquals("abc", d.zwcDecode())
    }

    @Test
    fun gray() {
        val data = "graycode加密"
        data.grayEncode().also {
            println(it)
            assertEquals(data, it.grayDecode())
        }
        data.grayEncode(4).also {
            println(it)
            assertEquals(data, it.grayDecode(4))
        }
        data.grayEncode(5).also {
            println(it)
            assertEquals(data, it.grayDecode(5))
        }
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
}
