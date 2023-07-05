package me.leon.tts

import javax.sound.sampled.*
import me.leon.tts.Audio.play

object TestTTS {

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        edgeTts()
    }

    private fun edgeTts() {
        val voice = TTSVoice.provides().first { it.ShortName == "en-US-MichelleNeural" }
        //        println(
        //            TTSVoice.provides()
        //                .filter { it.Status == "GA" && it.Gender == "Female" &&
        // it.FriendlyName!!.contains("English") }
        //                .joinToString(System.lineSeparator()) { it.ShortName.orEmpty() })
        //        return
        val content =
            ("Find definitions and references for functions and other symbols in this file by clicking a symbol below" +
                    " or in the code.")
                .repeat(10)
        println(content.length)
        TTS(voice, content)
            //            .findHeadHook() //                .formatMp3()  // default mp3.
            //            .formatOpus() // or opus
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
