package me.leon.music

import java.io.*
import java.util.Base64
import me.leon.ext.crypto.decrypt
import me.leon.ext.fromJson
import me.leon.music.ByteUtil.fillByteArr
import me.leon.music.ByteUtil.unpack
import org.bouncycastle.util.encoders.Hex
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.*
import org.jaudiotagger.tag.*
import org.jaudiotagger.tag.datatype.Artwork

/** @author created by qingchuan.xia */
class UnpackNcm {
    var stream: RepeatReadStream = RepeatReadStream()
    private val coreKey = Hex.decode("687A4852416D736F356B496E62617857")
    private val metaKey = Hex.decode("2331346C6A6B5F215C5D2630553C2728")

    //    private val headerKey = Hex.decode("4354454e4644414d")
    @Throws(
        TagException::class,
        ReadOnlyFileException::class,
        CannotReadException::class,
        InvalidAudioFrameException::class,
        IOException::class,
        CannotWriteException::class
    )
    fun ncm2NormalFormat(file: File) {
        try {
            // 文件头有8位header 2位空byte
            stream.setFile(file)
            stream.skip(10)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return
        }

        // 音乐段byte的密码
        val keyData = readKey()
        // 音乐文件的mete字符串
        val meteData = readMetaData()
        readCrc32()
        val cover = readAlbumCover()
        val musicData = readMusic(keyData)
        val musicInfo = getInfo(file.parent + SAVE_FILE_PATH, meteData)
        val saveFile = File(musicInfo.filePath)
        write(saveFile, musicData)
        val music = AudioFileIO.read(saveFile)
        val tag = music.tag
        tag.deleteArtworkField()
        setMusicTag(tag, musicInfo)
        val artwork = Artwork()
        setCover(artwork, cover)
        tag.setField(artwork)
        music.commit()
    }

    @Throws(IOException::class)
    fun readKey(): ByteArray {
        val keyLength = readLength()
        // 读取接下来制定长度的byte数据
        val keyValidByte = stream.readFile(keyLength)
        val encryptedKey = keyValidByte.validByte
        for (i in encryptedKey.indices) {
            encryptedKey[i] = (encryptedKey[i].toInt() xor 0x64).toByte()
        }
        // 使用AES工具解密

        var bytes = encryptedKey.decrypt(coreKey, byteArrayOf(), "AES/ECB/PKCS5Padding")
        val key = removeChar(String(bytes), 17)
        bytes = key.toByteArray()
        val arr = fillByteArr()
        var i = 0
        var j = 0
        while (i < arr.size) {
            j = j + arr[i] + bytes[i % bytes.size] and 0xFF
            val tmp = arr[i]
            arr[i] = arr[j]
            arr[j] = tmp
            i++
        }
        return arr
    }

    @Throws(IOException::class)
    fun readMetaData(): String {
        val meteLength = readLength()
        var metaData = stream.readFile(meteLength).validByte
        for (i in metaData.indices) {
            metaData[i] = (metaData[i].toInt() xor 0x63).toByte()
        }
        metaData = metaData.copyOfRange(22, metaData.size)
        metaData = Base64.getDecoder().decode(metaData)
        metaData = metaData.decrypt(metaKey, byteArrayOf(), "AES/ECB/PKCS5Padding")
        return removeChar(String(metaData), 6)
    }

    fun readCrc32(): Int {
        // 具体这个Crc32有什么作用还不了解
        return readLength()
    }

    fun readAlbumCover(): ByteArray {
        stream.skip(5)
        // 专辑封面图片未加密
        val coverLength = readLength()
        return stream.readFile(coverLength).validByte
    }

    @Throws(FileNotFoundException::class)
    fun readMusic(keyByte: ByteArray): ByteArray {
        val musicBytes = stream.readFile().validByte
        val resultKey = ByteArray(keyByte.size)
        for (i in 0..0xFF) {
            resultKey[i] = keyByte[keyByte[i] + keyByte[i + keyByte[i] and 0xFF] and 0xFF]
        }
        for (j in musicBytes.indices) {
            val cursor = resultKey[(j + 1) % resultKey.size]
            musicBytes[j] = (musicBytes[j].toInt() xor cursor.toInt()).toByte()
        }
        return musicBytes
    }

    fun getInfo(parentPath: String, metaData: String): MusicInfo {
        println("________$metaData")
        val meta: NcmMeta = metaData.fromJson(NcmMeta::class.java)
        val name = meta.musicName.replace("/".toRegex(), "／")
        val artistBuilder = StringBuilder()
        val artists = meta.artist
        for (i in artists.indices) {
            val artist = artists[i]
            artistBuilder.append(artist.first()).append(",")
        }
        val artist =
            artistBuilder
                .deleteCharAt(artistBuilder.length - 1)
                .toString()
                .replace("/".toRegex(), "／")
        val album = meta.album
        val format = meta.format
        val filePath = "$parentPath\\$artist - $name.$format"
        println("File: $filePath")
        return MusicInfo(name, artist, album, format, filePath)
    }

    fun write(target: File, data: ByteArray) {
        target.outputStream().use { it.write(data) }
    }

    fun setMusicTag(tag: Tag, musicInfo: MusicInfo) {
        tag.deleteArtworkField()
        tag.setField(FieldKey.ARTIST, musicInfo.artist)
        tag.setField(FieldKey.ALBUM, musicInfo.album)
        tag.setField(FieldKey.TITLE, musicInfo.name)
    }

    /** 移除掉的是'neteasecloudmusic'字符串，但是不确定是否一定是这个，所以直接截取掉前17位 */
    private fun removeChar(str: String, from: Int): String {
        return str.trim { it <= ' ' }.replace("\r|\n".toRegex(), "").substring(from)
    }

    private fun readLength(): Int {
        val lengthBytes = stream.readFile(4)
        return unpack(lengthBytes.validByte)
    }

    private fun setCover(artwork: Artwork, cover: ByteArray) {
        artwork.binaryData = cover
        artwork.mimeType = "image/jpeg"
        artwork.pictureType = 3
    }

    companion object {
        const val SAVE_FILE_PATH = "\\music"
    }
}
