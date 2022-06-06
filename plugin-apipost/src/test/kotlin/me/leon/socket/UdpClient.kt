package me.leon.socket

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*

class UdpClient(val host: String, val port: Int, clientPort: Int) {
    private val socket = DatagramSocket(clientPort)

    init {
        println("已连接$host:$clientPort")
    }

    fun fromConsole() {
        val inetAddress = InetAddress.getByName(host)
        val inputStream = BufferedReader(InputStreamReader(System.`in`))

        var line = ""
        while (line != "exit") {
            line = inputStream.readLine()
            println("客户端输入的是: $line")
            val packet =
                DatagramPacket(line.toByteArray(), line.toByteArray().size, inetAddress, port)
            socket.send(packet)
        }
        inputStream.close()
        socket.close()
    }
}
