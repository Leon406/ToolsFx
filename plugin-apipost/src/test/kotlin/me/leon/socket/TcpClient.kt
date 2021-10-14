package me.leon.socket

import java.io.*
import java.net.Socket

class TcpClient(host: String, private val port: Int) {
    private var socket: Socket

    init {
        socket = Socket(host, port)
        println("已连接$host:$port")
    }

    fun fromConsole() {

        val inputStream = BufferedReader(InputStreamReader(System.`in`))
        val outputStream = DataOutputStream(socket.getOutputStream())

        var line = ""
        while (line != "exit") {
            line = inputStream.readLine()
            println("客户端输入的是: $line")
            outputStream.writeUTF(line)
            outputStream.flush()
        }
        inputStream.close()
        socket.close()
        outputStream.close()
    }
}
