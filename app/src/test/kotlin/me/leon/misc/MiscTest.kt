package me.leon.misc

import kotlin.test.*
import me.leon.misc.net.*

/**
 * @author Leon
 * @since 2023-01-31 16:25
 * @email deadogone@gmail.com
 */
class MiscTest {
    val japaneseRomans =
        mapOf(
            //  元音
            "あ" to "a",
            "ア" to "a",
            "い" to "i",
            "イ" to "i",
            "う" to "u",
            "ウ" to "u",
            "え" to "e",
            "エ" to "e",
            "お" to "o",
            "オ" to "o",
            //  k + 元音
            "か" to "ka",
            "カ" to "ka",
            "き" to "ki",
            "キ" to "ki",
            "く" to "ku",
            "ク" to "ku",
            "け" to "ke",
            "ケ" to "ke",
            "こ" to "ko",
            "コ" to "ko",
            //  s + 元音
            "さ" to "sa",
            "タ" to "sa",
            "し" to "shi",
            "シ" to "shi",
            "す" to "su",
            "ス" to "su",
            "せ" to "se",
            "セ" to "se",
            "そ" to "so",
            "ソ" to "so",
            //  t + 元音
            "た" to "ta",
            "タ" to "ta",
            "ち" to "chi",
            "チ" to "chi",
            "つ" to "tsu",
            "ツ" to "tsu",
            "て" to "te",
            "テ" to "te",
            "と" to "to",
            "ト" to "to",
            //  n + 元音
            "な" to "na",
            "ナ" to "na",
            "に" to "ni",
            "ニ" to "ni",
            "ぬ" to "nu",
            "ヌ" to "nu",
            "ね" to "ne",
            "ネ" to "ne",
            "の" to "no",
            "ノ" to "no",
            //  h + 元音
            // “は”作助词时,读作wa, 名词读ha
            "は" to "ha",
            "ハ" to "ha",
            "ひ" to "hi",
            "ヘ" to "hi",
            "ふ" to "fu",
            "フ" to "fu",
            "へ" to "he",
            "へ" to "he",
            "ほ" to "ho",
            "ホ" to "ho",
            //  m + 元音
            "ま" to "ma",
            "マ" to "ma",
            "み" to "mi",
            "ミ" to "mi",
            "む" to "mu",
            "ム" to "mu",
            "め" to "me",
            "メ" to "me",
            "も" to "mo",
            "モ" to "mo",
            //  y + 元音
            "や" to "ya",
            "ヤ" to "ya",
            "ゆ" to "yu",
            "ユ" to "yu",
            "よ" to "yo",
            "ヨ" to "yo",
            //  r + 元音
            "ら" to "ra",
            "ラ" to "ra",
            "り" to "ri",
            "リ" to "ri",
            "る" to "ru",
            "ル" to "ru",
            "れ" to "re",
            "レ" to "re",
            "ろ" to "ro",
            "ロ" to "ro",
            //  w + 元音
            "わ" to "wa",
            "ワ" to "wa",
            "を" to "wo",
            "ヲ" to "wo",
            // 无元音
            "ん" to "n",
            "ン" to "n",

            // 浊音(变音) 右上两点
            // g + 元音
            "が" to "ga",
            "ガ" to "ga",
            "ぎ" to "gi",
            "ギ" to "gi",
            "ぐ" to "gu",
            "グ" to "gu",
            "げ" to "ge",
            "ゲ" to "ge",
            "ご" to "go",
            "ゴ" to "go",
            // z + 元音
            "ざ" to "za",
            "ザ" to "za",
            "じ" to "ji",
            "ジ" to "ji",
            "ず" to "zu",
            "ズ" to "zu",
            "ぜ" to "ze",
            "ゼ" to "ze",
            "ぞ" to "zo",
            "ゾ" to "zo",
            // d + 元音
            "だ" to "da",
            "ダ" to "da",
            "ぢ" to "ji",
            "ヂ" to "ji",
            "づ" to "zu",
            "ヅ" to "zu",
            "で" to "de",
            "デ" to "de",
            "ど" to "do",
            "ド" to "do",
            // b + 元音
            "ば" to "ba",
            "バ" to "ba",
            "び" to "bi",
            "ビ" to "bi",
            "ぶ" to "bu",
            "ブ" to "bu",
            "べ" to "be",
            "ぼ" to "bo",
            "ボ" to "bo",
            // 半浊音 右上圆点
            // p + 元音
            "ぱ" to "pa",
            "パ" to "pa",
            "ぴ" to "pi",
            "ピ" to "pi",
            "ぷ" to "pu",
            "プ" to "pu",
            "ぺ" to "pe",
            "ペ" to "pe",
            "ぽ" to "po",
            "ポ" to "po",
            // 拗音
            "キャ" to "kya",
            "キュ" to "kyu",
            "キョ" to "kyo",
            "シャ" to "sha",
            "シュ" to "shu",
            "ショ" to "sho",
            "チャ" to "cha",
            "チュ" to "chu",
            "チョ" to "cho",
            "ニャ" to "nya",
            "ニュ" to "nyu",
            "ニョ" to "nyo",
            "ヒャ" to "hya",
            "ヒュ" to "hyu",
            "ヒョ" to "hyo",
            "ミャ" to "mya",
            "ミュ" to "myu",
            "ミョ" to "myo",
            "リャ" to "rya",
            "リュ" to "ryu",
            "リョ" to "ryo",
            "ギャ" to "gya",
            "ギュ" to "gyu",
            "ギョ" to "gyo",
            "ジャ" to "ja",
            "ジュ" to "ju",
            "ジョ" to "jo",
            "ビャ" to "bya",
            "ビュ" to "byu",
            "ビョ" to "byo",
            "ピャ" to "pya",
            "ピュ" to "pyu",
            "ピョ" to "pyo",
            "ゔぁ" to "ba",
            "ヴァ" to "ba",
            "ゔぃ" to "bi",
            "ヴィ" to "bi",
            "ゔぇ" to "be",
            "ヴェ" to "be",
            "ゔぉ" to "bo",
            "ヴォ" to "bo",
            "シェ" to "shie",
            "チェ" to "chie",
            "ティ" to "tei",
            "ニィ" to "ni",
            "ニェ" to "nie",
            "ファ" to "fua",
            "フェ" to "fue",
            "フォ" to "fuo",
            "ジェ" to "jie",
            "ディ" to "dei",
            "デュ" to "deyu",
            "ウィ" to "ui",
            "ウェ" to "ue",
            "ウォ" to "uo",
            // “ー” 不发音,长音符,拖长一拍
        )

    @Test
    @Ignore
    fun port() {
        println("127.0.0.1".connect(7890, 20))
        val ports = 7882..10_000
        ports.filter { "127.0.0.1".connect(it) >= 0 }.forEach { println("~~~~~~~~$it") }
    }

    @Test
    @Ignore
    fun portAsync() {
        println("baidu.com".portScan())
    }

    @Test
    @Ignore
    fun lanScan() {
        println("192.168.0".lanScan())
    }

    @Test
    fun cronExplain() {

        val corns =
            arrayOf(
                "30 * * * ?",
                "30 * * * * ?",
                "30 10 * * * ?",
                "30 10 1 20 * ?",
                "30 10 1 20 10 ? *",
                "30 10 1 20 10 ? 2011",
                "30 10 1 ? 10 SUN 2011",
                "5,30,45 * * * * ?",
                "15/5 * * * * ?",
                "*/15 * * * * ?",
                "15-45 * * * * ?",
                "15-30/5 * * * * ?",
                "0 15 10 LW * ?",
                "0 15 10 ? * 5L",
                "0 15 10 ? * 5#3",
                "0,5,15,17,25,32,38,45 0,17,24,36 * * * ?",
                "0/30 0 4/6 * * ?",
                "0 1 1/5 * *",
                "0 0 12 ? * WED",
                "0 15 10 ? * MON-FRI",
                "0 15 10 ? * 6L 2002-2005",
            )

        corns
            .map { CronExpression(it.trim()) }
            .forEach { println(it.expression + " " + it.explain()) }
    }

    @Test
    fun ieee754() {
        val floatToRawIntBits = java.lang.Float.floatToRawIntBits(220.5F)
        println(Integer.toBinaryString(floatToRawIntBits))
        println(Integer.toHexString(floatToRawIntBits))
        println(Integer.toBinaryString(java.lang.Float.floatToRawIntBits(-220.5F)))
        println(java.lang.Long.toBinaryString(java.lang.Double.doubleToRawLongBits(1.111)))
        println(java.lang.Long.toHexString(java.lang.Double.doubleToRawLongBits(1.111)))
        println(java.lang.Long.toBinaryString(java.lang.Double.doubleToRawLongBits(-1.111)))
        println(java.lang.Long.toHexString(java.lang.Double.doubleToRawLongBits(-1.111)))
    }

    @Test
    @Ignore
    fun githubMirror() {

        val url = "https://raw.githubusercontent.com/Leon406/SubCrawler/main/sub/share/host"
        val url2 =
            "https://github.com/git-for-windows/git/releases/download/v2.41.0.windows.1/Git-2.41.0-64-bit.exe"
        val file = "https://github.com/Leon406/ToolsFx/blob/dev/testdata/sm2/sm_qd_data.txt"
        println("_____raw")
        println(url.githubMirror())
        println("_____release download")
        println(url2.githubMirror())
        println("_____blob")
        println(file.githubMirror())
    }

    @Test
    fun githubRepoUrl() {

        val url = "https://raw.githubusercontent.com/Leon406/SubCrawler/main/sub/share/host"

        val url2 =
            "https://github.com/git-for-windows/git/releases/download/v2.41.0.windows.1/Git-2.41.0-64-bit.exe"
        val file = "https://github.com/Leon406/ToolsFx/blob/dev/testdata/sm2/sm_qd_data.txt"

        val repoUrl = url.githubRepoUrl()
        println(repoUrl)
        println(repoUrl.replace(REG_GITHUB, "https://raw.githubusercontent.com/$2/"))
        println("_____raw")
    }

    @Test
    fun romanNumber() {
        val str = "399"
        println(str.roman())
        repeat(3999) { assertEquals(it + 1, (it + 1).toRoman().romanToInt()) }
    }

    @Test
    fun romanji() {
        println("こんにちは".map { japaneseRomans[it.toString()] ?: it })
        println(japaneseRomans.size)
        val tokens = com.atilika.kuromoji.ipadic.Tokenizer().tokenize("でも大丈夫で")
        tokens.forEach {
            println(
                it.surface +
                    " " +
                    it.pronunciation +
                    " " +
                    it.pronunciation.map { japaneseRomans[it.toString()] ?: it }
            )
        }
    }

    @Test
    fun romanjiNet() {
        val data = "222でも大丈夫で"
        println(data.kawa().pretty())
        println(data.kawa(KawaType.CHINESE).pretty())
        println("喜欢你".kawa(KawaType.CANTONESE).pretty())
        println(("여보세요\n" + "안녕하세요\n" + "배용준").kawa(KawaType.HANGUL).pretty())
    }
}
