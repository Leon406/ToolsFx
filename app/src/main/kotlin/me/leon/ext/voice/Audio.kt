package me.leon.ext.voice

import java.io.File
import java.io.InputStream
import javax.sound.sampled.*
import kotlin.concurrent.thread
import me.leon.ext.readBytesFromNet
import me.leon.ext.toFile

/**
 * @author Leon
 * @since 2023-07-05 16:45
 * @email deadogone@gmail.com
 */
object Audio {

    val MPEG2L3 = AudioFormat.Encoding("MPEG2L3")
    val MPEG1L3 = AudioFormat.Encoding("MPEG1L3")
    val APE = AudioFormat.Encoding("APE")
    val VORBISENC = AudioFormat.Encoding("VORBISENC")
    val AAC = AudioFormat.Encoding("AAC")
    val SPEEX = AudioFormat.Encoding("SPEEX")

    fun playFromUrl(url: String, isAsync: Boolean = false): SourceDataLine? =
        url.readBytesFromNet().inputStream().let { play(it, isAsync) }

    fun playFromFile(file: String, isAsync: Boolean = false): SourceDataLine? =
        play(file.toFile(), isAsync)

    fun play(file: File, isAsync: Boolean = false): SourceDataLine? =
        playAudioStream(AudioSystem.getAudioInputStream(file), isAsync)

    fun play(stream: InputStream?, isAsync: Boolean = false): SourceDataLine? {
        if (stream == null || stream.available() == 0) {
            return null
        }
        return playAudioStream(AudioSystem.getAudioInputStream(stream), isAsync)
    }

    fun playAudioStream(stream: AudioInputStream, isAsync: Boolean = false): SourceDataLine? {
        // http://java.ittoolbox.com/groups/technical-functional/java-l/sound-in-an-application-90681
        var ais = stream
        var audioFormat = ais.format
        println("Format: $audioFormat, BigEndian ${audioFormat.isBigEndian}")
        // ULAW format to PCM format conversion
        if (
            audioFormat.encoding === AudioFormat.Encoding.ULAW ||
                audioFormat.encoding === AudioFormat.Encoding.ALAW
        ) {
            val newFormat =
                AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    audioFormat.sampleRate,
                    audioFormat.sampleSizeInBits * 2,
                    audioFormat.channels,
                    audioFormat.frameSize * 2,
                    audioFormat.frameRate,
                    true
                )
            ais = AudioSystem.getAudioInputStream(newFormat, ais)
            audioFormat = newFormat
        } else {
            val newFormat =
                AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    audioFormat.sampleRate,
                    16,
                    audioFormat.channels,
                    audioFormat.channels * 2,
                    audioFormat.sampleRate,
                    false
                )
            ais = AudioSystem.getAudioInputStream(newFormat, ais)
            audioFormat = newFormat
        }

        // checking for a supported output line
        val dataLine = DataLine.Info(SourceDataLine::class.java, audioFormat)
        if (!AudioSystem.isLineSupported(dataLine)) {
            println("!!! not supported $dataLine")
            return null
        } else {
            println("==> supported $dataLine")
            // opening the sound output line
            val srcDataLine = AudioSystem.getLine(dataLine) as SourceDataLine
            srcDataLine.open(audioFormat)
            srcDataLine.start()
            srcDataLine.addLineListener {
                if (it.type == LineEvent.Type.STOP) {
                    srcDataLine.close()
                }
            }
            if (isAsync) {
                thread { writeData(audioFormat, srcDataLine, ais) }
            } else {
                writeData(audioFormat, srcDataLine, ais)
            }

            return srcDataLine
        }
    }

    /** Copy data from the input stream to the output data line */
    private fun writeData(
        audioFormat: AudioFormat,
        srcDataLine: SourceDataLine,
        ais: AudioInputStream
    ) {
        val frameSize = audioFormat.frameSize
        val bufferSize = srcDataLine.bufferSize / 8
        val bufferLength = bufferSize * frameSize
        val soundData = ByteArray(bufferLength)
        var bytesLength: Int
        while (ais.read(soundData).also { bytesLength = it } != -1) {
            srcDataLine.write(soundData, 0, bytesLength)
        }
        srcDataLine.stop()
    }
}
