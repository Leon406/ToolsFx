package me.leon.music.ncm

import java.io.File
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import me.leon.ext.crypto.decrypt
import me.leon.music.RandomAccessStream
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.datatype.Artwork

/** @author created by qingchuan.xia */
object NcmDecoder {
    private const val ALG = "AES/ECB/PKCS5Padding"
    private const val SAVE_FILE_PATH = "music"

    private const val AES_KEY = "687A4852416D736F356B496E62617857"
    private const val META_KEY = "2331346C6A6B5F215C5D2630553C2728"
    private const val MAGIC = "4354454e4644414d"

    fun ncmDecrypt(file: File) {
        RandomAccessStream(file).run {
            // 8位header
            val magic = readNBytes(8)
            require(magic.contentEquals(MAGIC.hex2ByteArray())) { "not ncm format!!!" }
            // skip 2 bytes gap
            skip(2)
            // decode from const AES keys, 长度+data
            val keyData = decryptKey()
            // 音乐文件的meta字符串,长度+data
            val metaData = readMetaData()
            readCrc32()
            // cover读取 length + data
            val cover = readAlbumCover()
            // 剩下所有都是加密music data
            val musicData = decryptMusicData(keyData)

            val musicInfo = parseMetaJson(File(file.parent, SAVE_FILE_PATH).absolutePath, metaData)
            val saveFile = File(musicInfo.filePath)
            saveFile.outputStream().use { it.write(musicData) }

            // 设置 tag
            AudioFileIO.read(saveFile).run {
                tag.run {
                    deleteArtworkField()
                    setField(FieldKey.ARTIST, musicInfo.artist)
                    setField(FieldKey.ALBUM, musicInfo.album)
                    setField(FieldKey.TITLE, musicInfo.name)
                    setField(
                        Artwork().apply {
                            binaryData = cover
                            mimeType = "image/jpeg"
                            pictureType = 3
                        }
                    )
                }
                commit()
            }
        }
    }

    private fun RandomAccessStream.decryptKey(): ByteArray {
        val keyLength = readLength()
        // 读取接下来制定长度的byte数据
        val encryptedKey = readValidate(keyLength)
        for (i in encryptedKey.indices) {
            encryptedKey[i] = encryptedKey[i] xor 0x64
        }

        var bytes = encryptedKey.decrypt(AES_KEY.hex2ByteArray(), byteArrayOf(), ALG)
        val key = removeChar(String(bytes), 17)
        bytes = key.toByteArray()

        val arr = ByteArray(256) { it.toByte() }
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

    private fun RandomAccessStream.readMetaData(): String {
        val length = readLength()
        var metaData = readValidate(length)
        metaData =
            metaData
                .map { it xor 0x63 }
                .toByteArray()
                .copyOfRange(22, metaData.size)
                .base64Decode()
                .decrypt(META_KEY.hex2ByteArray(), byteArrayOf(), ALG)
        return removeChar(String(metaData), 6)
    }

    private fun RandomAccessStream.readCrc32(): Int {
        // 具体这个Crc32有什么作用还不了解
        return readLength()
    }

    private fun RandomAccessStream.readAlbumCover(): ByteArray {
        skip(5)
        // 专辑封面图片未加密
        val coverLength = readLength()
        return readValidate(coverLength)
    }

    private fun RandomAccessStream.decryptMusicData(keyByte: ByteArray): ByteArray {
        val musicBytes = readRemain()
        val resultKey = ByteArray(keyByte.size)
        for (i in 0..0xFF) {
            resultKey[i] = keyByte[keyByte[i] + keyByte[i + keyByte[i] and 0xFF] and 0xFF]
        }
        for (j in musicBytes.indices) {
            val cursor = resultKey[(j + 1) % resultKey.size]
            musicBytes[j] = musicBytes[j] xor cursor
        }
        return musicBytes
    }

    private fun parseMetaJson(parentPath: String, metaData: String): MusicInfo {
        println("meta:  $metaData")
        val meta = metaData.fromJson(NcmMeta::class.java)
        val name = meta.musicName.replace("/", "／")
        val artist = meta.artist.map { it.first() }.joinToString(",").replace("/", "／")

        val filePath = "$parentPath${File.separator}$artist-$name.${meta.format}"
        return MusicInfo(name, artist, meta.album, meta.format, filePath)
    }

    /** 移除掉的是'neteasecloudmusic'字符串，但是不确定是否一定是这个，所以直接截取掉前17位 */
    private fun removeChar(str: String, from: Int) =
        str.trim { it <= ' ' }.replace("[\r\n]".toRegex(), "").substring(from)

    private fun RandomAccessStream.readLength() = readValidate(4).unpack()
}
