package me.leon.ext.voice

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import javax.sound.sampled.SourceDataLine
import me.leon.ext.toHtmlEntity
import me.leon.ext.voice.Audio.play
import me.leon.hash

class TTS(private val voice: Voice?, private val content: String) {

    private var headers: MutableMap<String, String> = mutableMapOf()
    private var findHeadHook = false
    private var format = "audio-24khz-48kbitrate-mono-mp3"
    private var voicePitch = "+0Hz"
    private var voiceRate = "+0%"
    private var voiceVolume = "+0%"
    private var cacheable = false
    private var saveDir = "./ttsFile"

    fun voicePitch(voicePitch: String): TTS {
        this.voicePitch = voicePitch
        return this
    }

    fun voiceRate(voiceRate: String): TTS {
        this.voiceRate = voiceRate
        return this
    }

    fun cache(cacheable: Boolean): TTS {
        this.cacheable = cacheable
        return this
    }

    fun cacheDir(dir: String): TTS {
        this.saveDir = dir
        return this
    }

    fun voiceVolume(voiceVolume: String): TTS {
        this.voiceVolume = voiceVolume
        return this
    }

    fun formatMp3(): TTS {
        format = "audio-24khz-48kbitrate-mono-mp3"
        return this
    }

    fun formatOpus(): TTS {
        format = "webm-24khz-16bit-mono-opus"
        return this
    }

    /**
     * This hook is more generic as it searches for the file header marker in the given file header
     * and removes it. However, it may have lower efficiency.
     */
    fun findHeadHook(): TTS {
        findHeadHook = true
        return this
    }

    /**
     * default This hook directly specifies the file header marker, which makes it faster. However,
     * if the format changes, it may become unusable.
     */
    fun fixHeadHook(): TTS {
        findHeadHook = false
        return this
    }

    fun headers(headers: MutableMap<String, String>): TTS {
        this.headers = headers
        return this
    }

    fun trans(): ByteArray? {
        requireNotNull(voice)
        val ttsMp3 = cacheFile()
        if (cacheable && ttsMp3.exists()) {
            println("cache: $ttsMp3")
            return ttsMp3.readBytes()
        }
        val dateStr = dateToString(Date())
        val reqId = uuid()
        val audioFormat = mkAudioFormat(dateStr)
        val ssml = mkssml(voice.Locale, voice.Name)
        val ssmlHeadersPlusData = ssmlHeadersPlusData(reqId, dateStr, ssml)

        headers["Origin"] = EDGE_ORIGIN
        headers["Pragma"] = "no-cache"
        headers["Cache-Control"] = "no-cache"
        headers["User-Agent"] = EDGE_UA

        var fileName = "audio"
        if (format == "audio-24khz-48kbitrate-mono-mp3") {
            fileName += ".mp3"
        } else if (format == "webm-24khz-16bit-mono-opus") {
            fileName += ".opus"
        }
        return try {
            val client = TTSWebsocket(EDGE_URL, headers, findHeadHook)
            client.connect()
            while (!client.isOpen) {
                // wait open
                Thread.sleep(100)
            }
            client.send(audioFormat)
            client.send(ssmlHeadersPlusData)
            while (client.isOpen) {
                // wait close
            }

            client.byteArrays.toByteArray().also {
                if (cacheable) {
                    ttsMp3.parentFile.mkdirs()
                    ttsMp3.writeBytes(it)
                }
            }
            //            fileName
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            null
        }
    }

    private fun cacheFile(): File {
        val voiceCacheDir =
            File(saveDir, "${voice!!.ShortName}_${voicePitch}_${voiceRate}_$voiceVolume")
        return File(voiceCacheDir, content.hash())
    }

    private fun mkAudioFormat(dateStr: String): String {
        return "X-Timestamp:" +
            dateStr +
            "\r\n" +
            "Content-Type:application/json; charset=utf-8\r\n" +
            "Path:speech.config\r\n\r\n" +
            "{\"context\":{\"synthesis\":{\"audio\":" +
            "{\"metadataoptions\":{\"sentenceBoundaryEnabled\":\"false\"," +
            "\"wordBoundaryEnabled\":\"true\"}," +
            "\"outputFormat\":\"" +
            format +
            "\"}}}}\n"
    }

    private fun mkssml(locale: String, voiceName: String): String {
        return "<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='" +
            locale +
            "'>" +
            "<voice name='" +
            voiceName +
            "'><prosody pitch='" +
            voicePitch +
            "' rate='" +
            voiceRate +
            "' volume='" +
            voiceVolume +
            "'>" +
            content +
            "</prosody></voice></speak>"
    }

    private fun ssmlHeadersPlusData(requestId: String, timestamp: String, ssml: String): String {
        return "X-RequestId:" +
            requestId +
            "\r\n" +
            "Content-Type:application/ssml+xml\r\n" +
            "X-Timestamp:" +
            timestamp +
            "Z\r\n" +
            "Path:ssml\r\n\r\n" +
            ssml
    }

    private fun dateToString(date: Date): String {
        val sdf = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)")
        return sdf.format(date)
    }

    private fun uuid(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    companion object {
        const val EDGE_URL =
            "wss://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1" +
                "?TrustedClientToken=6A5AA1D4EAFF4E9FB37E23D68491D6F4"
        const val EDGE_UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/99.0.4844.74 Safari/537.36 Edg/99.0.1150.55"
        const val EDGE_ORIGIN = "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold"
        const val VOICES_LIST_URL =
            "https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/voices/list?" +
                "trustedclienttoken=6A5AA1D4EAFF4E9FB37E23D68491D6F4"
    }
}

fun tts(
    content: String,
    isAsync: Boolean = true,
    voiceModel: String = "en-US-MichelleNeural",
    rate: String = "+20%",
    volume: String = "+0%",
    pitch: String = "+0%",
    cacheable: Boolean = false,
): SourceDataLine? {
    if (content.isBlank()) {
        return null
    }
    val voice = TTSVoice.find(voiceModel)
    val properContent = content.trim().replace("…", "...").toHtmlEntity(isAll = false)
    println("TTS $voiceModel: speed $rate volume $volume pitch $pitch $cacheable\n$properContent")
    return TTS(voice, properContent)
        .voicePitch(pitch)
        .voiceRate(rate)
        .voiceVolume(volume)
        .cache(cacheable)
        .trans()
        ?.run {
            println(size)
            play(this.inputStream(), isAsync)
        }
}
