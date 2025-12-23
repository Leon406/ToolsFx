package me.leon.tts

import me.leon.DESKTOP
import me.leon.ext.fx.Prefs
import me.leon.ext.toFile
import me.leon.ext.voice.Audio.play
import me.leon.ext.voice.ttsMultiStream

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

        //        tts(content)

        ttsMultiStream(
                content,
                voiceModel = Prefs.ttsVoice,
                rate = Prefs.ttsSpeed,
                volume = Prefs.ttsVolume,
                pitch = Prefs.ttsPitch,
                cacheable = Prefs.ttsCacheable,
            )
            ?.forEach {
                val (_, bytes) = it
                val sourceDataline = bytes?.run { play(this.inputStream(), true) }
                while (sourceDataline?.isOpen == true) {
                    // loop check
                }
            }
    }
}
