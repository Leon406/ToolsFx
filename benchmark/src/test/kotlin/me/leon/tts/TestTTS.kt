package me.leon.tts

import me.leon.DESKTOP
import me.leon.ext.toFile
import me.leon.tts.Audio.play
import javax.sound.sampled.*

object TestTTS {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val content =
            ("Find definitions and references for functions and other symbols in this file by clicking a symbol below" +
                    " or in the code.")
                .repeat(10)
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
            //            .findHeadHook() //                .formatMp3()  // default mp3.
            //            .formatOpus() // or opus java默认不支持
            //                            .voicePitch()
            .voiceRate("+20%")
            .voiceVolume("-40%")
            .trans()
            ?.run {
                println(size)
                play(this.inputStream())
            }
    }
}
