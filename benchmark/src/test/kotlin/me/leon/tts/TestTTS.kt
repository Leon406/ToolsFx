package me.leon.tts

import javax.sound.sampled.*
import me.leon.DESKTOP
import me.leon.ext.toFile
import me.leon.ext.voice.Audio.play
import me.leon.ext.voice.TTS
import me.leon.ext.voice.TTSVoice

object TestTTS {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val content =
            ("Find definitions and references for functions and other symbols in this file by clicking a symbol below" +
                    " or in the code.")
                .repeat(1)
        val newsletter = "$DESKTOP/news.txt".toFile()
        if (newsletter.exists()) {
            edgeTts(newsletter.readText())
        } else {
            edgeTts(content)
        }
    }

    private fun edgeTts(content: String) {
        val voice = TTSVoice.provides().first { it.ShortName == "en-US-MichelleNeural" }
        //        println(
        //            TTSVoice.provides()
        //                .filter { it.Status == "GA" && it.Gender == "Female" &&
        // it.FriendlyName!!.contains("English") }
        //                .joinToString(System.lineSeparator()) { it.ShortName.orEmpty() })
        //        return

        println(content.length)
        TTS(voice, content)
            .cache(true)
            //            .findHeadHook() //                .formatMp3()  // default mp3.
            //            .formatOpus() // or opus java默认不支持
            .voicePitch("+0%")
            .voiceRate("+20%")
            .voiceVolume("+100%")
            .trans()
            ?.run {
                println(size)
                play(this.inputStream())
            }
    }
}
