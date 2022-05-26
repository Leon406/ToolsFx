package me.leon.socket

import java.net.DatagramPacket
import java.net.DatagramSocket

class UdpServer(val port: Int) {
    private var socket: DatagramSocket

    init {
        println("Server start")
        socket = DatagramSocket(port)
    }

    fun receive() {
        val packet = DatagramPacket(ByteArray(512), 512)
        while (true) {
            socket.receive(packet)

            println(packet.data.decodeToString(0, packet.length))
        }
    }
}
