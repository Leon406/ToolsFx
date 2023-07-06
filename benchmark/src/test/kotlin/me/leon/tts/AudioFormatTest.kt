package me.leon.tts

import kotlin.test.Test
import me.leon.ext.voice.Audio

/**
 * @author Leon
 * @since 2023-07-05 17:39
 * @email deadogone@gmail.com
 */
class AudioFormatTest {
    val DIR =
        "E:\\software\\Lily5\\soft\\OfficeBox离线打包版\\data\\resources\\officebox\\musicconverter"

    @Test
    fun ogg() {
        Audio.playFromFile("$DIR/Example5.OGG")
    }

    @Test
    fun ape() {
        Audio.playFromFile("$DIR/Example1.ape")
    }

    @Test
    fun flac() {
        Audio.playFromFile("$DIR/Example2.flac")
    }

    @Test
    fun mp3() {
        Audio.playFromFile("$DIR/Example3.mp3")
    }

    @Test
    fun wav() {
        Audio.playFromFile("$DIR/videoInvite.wav")
    }

    @Test
    fun aac() {
        Audio.playFromFile("$DIR/Epic_TakeFlight_Main.aac")
    }

    @Test
    fun spx() {
        Audio.playFromFile("$DIR/23708-31.spx")
    }

    @Test
    fun onlineMp3() {
        Audio.playFromUrl(
            "https://wj1.kumeiwp.com:812/wj/bl/2022/03/16/9b885c09ef77d1fed558a976b1c078d8.mp3" +
                "?t=1688542681&key=4609aaddd77c7e86f76c"
        )
    }
}
