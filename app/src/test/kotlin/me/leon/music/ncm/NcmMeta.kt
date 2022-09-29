package me.leon.music.ncm

data class NcmMeta(
    val album: String,
    val albumId: Int,
    val albumPic: String,
    val albumPicDocId: Long,
    val alias: List<Any>,
    val artist: List<List<Any>>,
    val bitrate: Int,
    val duration: Int,
    val format: String,
    val mp3DocId: String,
    val musicId: Int,
    val musicName: String,
    val mvId: Int,
    val transNames: List<Any>
)
