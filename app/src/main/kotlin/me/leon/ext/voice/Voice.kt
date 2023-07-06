package me.leon.ext.voice

@Suppress("All")
data class Voice(
    var Name: String,
    var ShortName: String,
    var Gender: String,
    var Locale: String,
    var SuggestedCodec: String,
    var FriendlyName: String,
    var Status: String,
    var VoiceTag: VoiceTagBean? = null,
) {
    data class VoiceTagBean(
        val ContentCategories: List<String>,
        val VoicePersonalities: List<String>,
    )
}
