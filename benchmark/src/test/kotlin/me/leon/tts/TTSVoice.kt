package me.leon.tts

import me.leon.ext.GsonUtil.jsonToArrayList

object TTSVoice {
    private var voices: List<Voice>? = null

    init {
        runCatching {
            TTSVoice::class
                .java
                .classLoader
                .getResourceAsStream("voicesList.json")
                ?.bufferedReader()
                ?.use { voices = jsonToArrayList(it.readText(), Voice::class.java) }
        }
    }

    fun provides(): List<Voice> {
        return voices!!
    }
}
