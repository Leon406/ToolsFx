package me.leon.socket

import java.io.DataInputStream
import java.net.ServerSocket

class TcpServer(port: Int) {
    var serverSocket: ServerSocket = ServerSocket(port)

    fun receive() {
        println("开始接受信息")
        while (true) {
            val socket = serverSocket.accept()
            runCatching {
                val input = DataInputStream(socket.getInputStream())
                var line = ""
                while (line != "exit") {
                    line = input.readUTF()
                    println(line)
                }
                println(socket.getInputStream().readBytes().decodeToString())
            }
        }
    }

    fun close() {
        serverSocket.close()
    }
}
