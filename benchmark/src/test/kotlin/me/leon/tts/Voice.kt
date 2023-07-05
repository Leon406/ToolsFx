package me.leon.tts

@Suppress("All")
data class Voice(
    var Name: String? = null,
    var ShortName: String? = null,
    var Gender: String? = null,
    var Locale: String? = null,
    var SuggestedCodec: String? = null,
    var FriendlyName: String? = null,
    var Status: String? = null,
    var VoiceTag: VoiceTagBean? = null,
) {
    data class VoiceTagBean(
        val ContentCategories: List<String>?,
        val VoicePersonalities: List<String>?,
    )
}
