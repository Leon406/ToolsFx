package me.leon.ext.voice

import java.net.URI
import java.nio.ByteBuffer
import java.util.Arrays
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake

class TTSWebsocket(
    serverUri: String,
    httpHeaders: Map<String, String>,
    private val findHeadHook: Boolean
) : WebSocketClient(URI(serverUri), httpHeaders) {

    var byteArrays: MutableList<Byte> = mutableListOf()

    override fun onOpen(handshakedata: ServerHandshake) {
        byteArrays.clear()
    }

    override fun onMessage(message: String) {
        if (message.contains("Path:turn.end")) {
            close()
        }
    }

    override fun onMessage(originBytes: ByteBuffer) {
        if (findHeadHook) {
            findHeadHook(originBytes)
        } else {
            fixHeadHook(originBytes)
        }
    }

    /**
     * This implementation method is more generic as it searches for the file header marker in the
     * given file header and removes it. However, it may have lower efficiency.
     *
     * @param originBytes
     */
    private fun findHeadHook(originBytes: ByteBuffer) {
        val origin = originBytes.array()
        var headIndex = -1
        for (i in 0 until origin.size - head.size) {
            var match = true
            for (j in head.indices) {
                if (origin[i + j] != head[j]) {
                    match = false
                    break
                }
            }
            if (match) {
                headIndex = i
                break
            }
        }

        if (headIndex != -1) {
            val bytes = Arrays.copyOfRange(origin, headIndex + head.size, origin.size)
            byteArrays.addAll(bytes.toList())
            //            (storage + File.separator + fileName).toFile().appendBytes(bytes)
        }
    }

    /**
     * This method directly specifies the file header marker, which makes it faster. However, if the
     * format changes, it may become unusable.
     *
     * @param originBytes
     */
    fun fixHeadHook(originBytes: ByteBuffer) {
        val str = String(originBytes.array())
        val origin = originBytes.array()
        val skip =
            if (str.contains("Content-Type")) {
                if (str.contains("audio/mpeg")) {
                    130
                } else if (str.contains("codec=opus")) {
                    142
                } else {
                    0
                }
            } else {
                105
            }
        val bytes = Arrays.copyOfRange(origin, skip, origin.size)
        byteArrays.addAll(bytes.toList())
        //        (storage + File.separator + fileName).toFile().appendBytes(bytes)
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        // nop
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
    }

    companion object {
        private val head =
            byteArrayOf(0x50, 0x61, 0x74, 0x68, 0x3a, 0x61, 0x75, 0x64, 0x69, 0x6f, 0x0d, 0x0a)
    }
}
