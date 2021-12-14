package me.leon.socket

import org.junit.Test

class SocketTest {
    @Test
    fun launchServer() {
        val tcpServer = TcpServer(11111)
        tcpServer.receive()

        while (true) {
            // if needed
        }
    }

    @Test
    fun launchUdpServer() {
        val tcpServer = UdpServer(11112)
        tcpServer.receive()

        while (true) {
            // if needed
        }
    }
}
