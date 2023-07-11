package me.leon.ext.fx

import java.util.prefs.Preferences

const val TTS_DEFAULT_MODEL = "en-US-MichelleNeural"
const val TTS_DEFAULT_RATE = "+0%"
const val TRANSLATE_DEFAULT_LANGUAGE = "zh-CN"

object Prefs {
    private const val IGNORE_UPDATE = "isIgnoreUpdate"
    private const val ALWAYS_ON_TOP = "alwaysOnTop"
    private const val LANGUAGE = "language"
    private const val AUTO_COPY = "autoCopy"
    private const val TRAY = "tray"
    private const val HI_DPI = "hidpi"
    private const val TTS_VOICE = "ttsVoice"
    private const val TTS_SPEED = "ttsSpeed"
    private const val TTS_VOLUME = "ttsVolume"
    private const val TTS_PITCH = "ttsPitch"
    private const val TTS_CACHE = "ttsCache"
    private const val TTS_LONG_SENTENCE = "ttsLongSentence"
    private const val TRANSLATE_TARGET_LAN = "translateTargetLanguage"
    private const val OCR_KEY = "ocrKey"
    private const val OCR_SECRET = "ocrSecret"
    private val preference = Preferences.userNodeForPackage(Prefs::class.java)
    var isIgnoreUpdate
        get() = preference.getBoolean(IGNORE_UPDATE, false)
        set(value) {
            preference.putBoolean(IGNORE_UPDATE, value)
        }

    var alwaysOnTop
        get() = preference.getBoolean(ALWAYS_ON_TOP, false)
        set(value) {
            preference.putBoolean(ALWAYS_ON_TOP, value)
        }

    /** 默认是自适应 */
    var hidpi
        get() = preference.getBoolean(HI_DPI, true)
        set(value) {
            preference.putBoolean(HI_DPI, value)
        }

    var language: String
        get() = preference.get(LANGUAGE, "zh")
        set(value) {
            preference.put(LANGUAGE, value)
        }

    var ttsVoice: String
        get() = preference.get(TTS_VOICE, TTS_DEFAULT_MODEL)
        set(value) {
            preference.put(TTS_VOICE, value)
        }

    var ttsSpeed: String
        get() = preference.get(TTS_SPEED, TTS_DEFAULT_RATE)
        set(value) {
            preference.put(TTS_SPEED, value)
        }

    var ttsVolume: String
        get() = preference.get(TTS_VOLUME, TTS_DEFAULT_RATE)
        set(value) {
            preference.put(TTS_VOLUME, value)
        }

    var ttsPitch: String
        get() = preference.get(TTS_PITCH, TTS_DEFAULT_RATE)
        set(value) {
            preference.put(TTS_PITCH, value)
        }

    var translateTargetLan: String
        get() = preference.get(TRANSLATE_TARGET_LAN, TRANSLATE_DEFAULT_LANGUAGE)
        set(value) {
            preference.put(TRANSLATE_TARGET_LAN, value)
        }

    var ttsCacheable: Boolean
        get() = preference.getBoolean(TTS_CACHE, false)
        set(value) {
            preference.putBoolean(TTS_CACHE, value)
        }

    var ttsLongSentence: Boolean
        get() = preference.getBoolean(TTS_LONG_SENTENCE, false)
        set(value) {
            preference.putBoolean(TTS_LONG_SENTENCE, value)
        }

    var autoCopy: Boolean
        get() = preference.getBoolean(AUTO_COPY, false)
        set(value) {
            preference.putBoolean(AUTO_COPY, value)
        }

    var miniToTray: Boolean
        get() = preference.getBoolean(TRAY, false)
        set(value) {
            preference.putBoolean(TRAY, value)
        }

    var ocrKey: String
        get() = preference.get(OCR_KEY, "")
        set(value) {
            preference.put(OCR_KEY, value)
        }

    var ocrSecret: String
        get() = preference.get(OCR_SECRET, "")
        set(value) {
            preference.put(OCR_SECRET, value)
        }

    fun preference(): Preferences = preference

    fun configTtsParams(
        voice: String = TTS_DEFAULT_MODEL,
        speed: String = TTS_DEFAULT_RATE,
        volume: String = TTS_DEFAULT_RATE,
        pitch: String = TTS_DEFAULT_RATE,
        cacheable: Boolean = false,
        longSentence: Boolean = false,
    ) {
        ttsVoice = voice
        ttsSpeed = speed
        ttsVolume = volume
        ttsPitch = pitch
        ttsCacheable = cacheable
        ttsLongSentence = longSentence
    }

    fun configOcr(key: String = "", secret: String = "") {
        ocrKey = key
        ocrSecret = secret
    }
}
