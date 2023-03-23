package me.leon.crack

import com.google.gson.annotations.SerializedName
import me.leon.encode.base.base64UrlDecode2String

private const val XIAO_E_TONG_BASE64_DICT =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0@#$%56789+/"

/**
 * @author Leon
 * @since 2023-03-16 16:45
 * @email deadogone@gmail.com
 */
data class XiaoeVideoInfoResponse(
    val code: Int,
    val msg: String,
    val data: Data?,
) {
    data class Data(
        @SerializedName("video_urls") val videoUrls: String?,
        @SerializedName("video_player_type") val videoPlayerType: Int?,
        @SerializedName("video_info") val videoInfo: VideoInfo?
    ) {
        fun decryptVideoUrl() =
            videoUrls
                ?.replace("__ba", "")
                ?.base64UrlDecode2String(XIAO_E_TONG_BASE64_DICT)
                .orEmpty()
    }

    data class VideoInfo(
        @SerializedName("app_id") val appId: String?,
        @SerializedName("resource_id") val resourceId: String?,
        @SerializedName("video_length") val videoLength: Int?,
        @SerializedName("file_name") val fileName: String?,
        @SerializedName("resource_type") val resourceType: Int?
    )
}

data class DecryptedVideoItem(
    @SerializedName("resource_type") val definitionName: String,
    @SerializedName("definition_p") val definitionP: String,
    val url: String,
    @SerializedName("is_support") val isSupport: Boolean,
    val ext: Ext
) {
    data class Ext(val host: String, val path: String, val param: String)

    fun path(file: String): String =
        ext.run { "$host/$path/$file${"?".takeUnless { file.contains("?") } ?: "&"}$param" }
}
